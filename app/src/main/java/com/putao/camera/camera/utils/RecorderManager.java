package com.putao.camera.camera.utils;


import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.putao.camera.util.BitmapToVideoUtil;

import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.googlecode.javacv.cpp.opencv_core.CV_32FC1;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateMat;
import static com.googlecode.javacv.cpp.opencv_imgproc.cv2DRotationMatrix;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvWarpAffine;

/**
 * Recorder controller, used to start,stop record, and combine all the videos
 * together
 */
public class RecorderManager {
    private static final String TAG = RecorderManager.class.getSimpleName();
    private boolean isMax = false;
    private long videoStartTime;
    private int totalTime = 0;
    private boolean isStart = false;

    private IplImage yuvIplimage = null;
    private volatile FFmpegFrameRecorder recorder;
    private final int sampleAudioRateInHz = 44100;
    private final int frameRate = 24;

    private AudioRecord audioRecord;
    private AudioRecordRunnable audioRecordRunnable;
    private Thread audioThread;
    private volatile boolean runAudioThread = true;
    private boolean isFinished = false;

    private int maxTime = 10000;
    private int width = 0;
    private int height = 0;
    private String videoPath = "";

    public RecorderManager(int maxTime, int width, int height, String videoPath) {
        this.maxTime = maxTime;
        this.width = width;
        this.height = height;
        this.videoPath = videoPath;
        try {
            System.loadLibrary("tbb");
        } catch (UnsatisfiedLinkError use) {
            Log.e("JNI", "WARNING: Could not load libtbb.so");
        }
        reset();
        initRecorder();
    }

    public boolean isStart() {
        return isStart;
    }

    public long getVideoStartTime() {
        return videoStartTime;
    }

    private int checkIfMax(long timeNow) {
        int during = 0;
        if (isStart) {
            during = (int) (totalTime + (timeNow - videoStartTime));
            if (during >= maxTime) {
                stopRecord();
                during = maxTime;
                isMax = true;
            }
        } else {
            during = totalTime;
            if (during >= maxTime) {
                during = maxTime;
            }
        }
        return during;
    }

    private void initRecorder() {
        if (yuvIplimage == null) {
            yuvIplimage = IplImage.create(width, height,
                    IPL_DEPTH_8U, 2);

        }
        recorder = new FFmpegFrameRecorder(videoPath, width,
                height, 1);
        recorder.setFormat("mp4");
        recorder.setSampleRate(sampleAudioRateInHz);
        recorder.setFrameRate(frameRate);
        audioRecordRunnable = new AudioRecordRunnable();
        audioThread = new Thread(audioRecordRunnable);
        try {
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        audioThread.start();
    }

    private class AudioRecordRunnable implements Runnable {

        @Override
        public void run() {
            android.os.Process
                    .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            int bufferSize;
            short[] audioData;
            int bufferReadResult;

            bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleAudioRateInHz,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            audioData = new short[bufferSize];

            audioRecord.startRecording();

            while (!isFinished && needVoice) {
                bufferReadResult = audioRecord.read(audioData, 0,
                        audioData.length);
                if (bufferReadResult > 0) {
                    if (isStart) {
                        try {
                            Buffer[] barray = new Buffer[1];
                            barray[0] = ShortBuffer.wrap(audioData, 0,
                                    bufferReadResult);
                            recorder.record(barray);
                        } catch (FFmpegFrameRecorder.Exception e) {
                            Log.v(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
            }
        }
    }

    private void reset() {
        isStart = false;
        totalTime = 0;
        isMax = false;
    }


    public void startRecord() {
        if (isMax) {
            return;
        }
        isStart = true;
        videoStartTime = new Date().getTime();
    }

    public void stopRecord() {
        if (recorder != null && isStart) {
            runAudioThread = false;
            if (!isMax) {
                totalTime += new Date().getTime() - videoStartTime;
                videoStartTime = 0;
            }
            isStart = false;
            releaseRecord();
        }
    }

    private void releaseRecord() {
        isFinished = true;
        try {
            recorder.stop();
            recorder.release();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
        recorder = null;
    }


    private void onPreviewFrame(Bitmap bmp) {
        int during = checkIfMax(new Date().getTime());
        if (yuvIplimage != null && isStart) {
            yuvIplimage.getByteBuffer().put(
                    BitmapToVideoUtil.getYUV420sp(bmp.getWidth(), bmp.getHeight(), bmp));
//            bmp.recycle();
//            yuvIplimage = rotateImage(yuvIplimage.asCvMat(), 90).asIplImage();
            try {
                if (during < maxTime && isStart) {
                    recorder.setTimestamp(1000 * during);
                    recorder.record(yuvIplimage);
                }
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private void onPreviewFrame(final byte[] data) {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int during = checkIfMax(new Date().getTime());
                if (yuvIplimage != null && isStart) {
                    yuvIplimage.getByteBuffer().put(data);
//            yuvIplimage = rotateImage(yuvIplimage.asCvMat(), 90).asIplImage();
                    try {
                        if (during < maxTime && isStart) {
                            recorder.setTimestamp(1000 * during);
                            recorder.record(yuvIplimage);
                        }
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private CvMat rotateImage(CvMat input, int angle) {
        CvPoint2D32f center = new CvPoint2D32f(input.cols() / 2.0F,
                input.rows() / 2.0F);

        CvMat rotMat = cvCreateMat(2, 3, CV_32FC1);
        cv2DRotationMatrix(center, angle, 1, rotMat);
        CvMat dst = cvCreateMat(input.rows(), input.cols(), input.type());
        cvWarpAffine(input, dst, rotMat);
        return dst;

    }

    /**
     * 一张图片合成视频
     *
     * @param bmp
     */
    public void combineVideo(final Bitmap bmp) {
        startRecord();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onPreviewFrame(bmp);
                }
                needVoice = true;
            }
        }).start();
    }

    private int picIndex = 0;
    private boolean needVoice = true;

    public void combineVideo(final List<byte[]> combineBmps) {
        startRecord();
        needVoice = false;
        picIndex = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (picIndex < combineBmps.size() && isStart) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    onPreviewFrame(combineBmps.get(picIndex));
                    picIndex++;

                    if (picIndex >= combineBmps.size()) {
                        picIndex = 0;
                    }
                }
                combineBmps.clear();
                needVoice = true;
            }
        }).start();
    }

    public void recordVideo(byte[] data) {
        onPreviewFrame(data);
    }
}

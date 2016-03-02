/******************************************************************************
 *     Copyright (c) 2015 PuTao Co., Ltd.
 *     All Rights Reserved.
 ******************************************************************************/
#include "MainSample.h"
#include "eyeDetect.h"

//#define _PICTURE_MODE_

static void help(void)
{
    PTDEBUG("\nThis program demonstrates the usage of eyeDetect library from putao Co., Ltd.\n"
            "Usage:./eyeDetect src.jpg\n"
           );
}

#ifdef _PICTURE_MODE_
int main(int argc, const char *argv[])
{
    help();

    assert(argc>=2);

    Mat src  = imread(argv[1], 1/*3-channel color image*/);
    if(src.empty()) {
       PTDEBUG("error: image[%s] is empty!\n", argv[1]);
       return -2;
    }

    PTDEBUG("src.cols[%d], src.rows[%d].\n", src.cols, src.rows);

    char strVendor[128] = {""};
    PTEyeDetectGetVendorString(strVendor);
    PTDEBUG("strVendor[%s].\n", strVendor);

    PTEyeDetectInitData initData;
    initData.nWidth  = src.cols;
    initData.nHeight = src.rows;
    initData.eFormat = PT_IMG_BGR888;
    initData.pFaceCascade = "data/haarcascade_frontalface_alt.xml";
    initData.pEyeCascade  = "data/haarcascade_eye_tree_eyeglasses.xml";
    initData.minFaceSize = 20;
    initData.maxFaceSize = min(src.cols, src.rows);

    PTEyeDetectHandler handler;

    PTEyeDetectInit(&initData, &handler);

    PTPoint ptLeftEye, ptRightEye;
    PTEyeDetect(handler, src.data, ptLeftEye, ptRightEye);

#ifdef _SHOW_
    circle(src, Point(ptLeftEye.x, ptLeftEye.y), 3, Scalar(0,0,255), 3, 8, 0);
    circle(src, Point(ptRightEye.x, ptRightEye.y), 3, Scalar(255,0,0), 3, 8, 0);
    imshow("result", src);
    waitKey(0);
#endif

    PTEyeDetectDeinit(&handler);

    return 0;
}

#else //!_PICTURE_MODE_

int main(int argc, const char *argv[])
{
    help();

    VideoCapture capture;
    if(!capture.open(0)) {
       PTDEBUG("Capture from camera #0 didn't work.\n");
    }

    Mat frame;
    capture >> frame;

    PTDEBUG("frame.cols[%d], frame.rows[%d].\n", frame.cols, frame.rows);

    char strVendor[128] = {""};
    PTEyeDetectGetVendorString(strVendor);
    PTDEBUG("strVendor[%s].\n", strVendor);

    PTEyeDetectInitData initData;
    initData.nWidth  = frame.cols;
    initData.nHeight = frame.rows;
    initData.eFormat = PT_IMG_BGR888;
    initData.pFaceCascade = "data/haarcascade_frontalface_alt.xml";
    initData.pEyeCascade  = "data/haarcascade_eye_tree_eyeglasses.xml";
    initData.minFaceSize = 20;
    initData.maxFaceSize = min(frame.cols, frame.rows);

    PTEyeDetectHandler handler;

    PTEyeDetectInit(&initData, &handler);

    while(true) {
       PTPoint ptLeftEye, ptRightEye;
       capture >> frame;
       flip(frame, frame, 1);
       PTEyeDetect(handler, frame.data, ptLeftEye, ptRightEye);

#ifdef _SHOW_
       circle(frame, Point(ptLeftEye.x, ptLeftEye.y), 3, Scalar(0,0,255), 3, 8, 0);
       circle(frame, Point(ptRightEye.x, ptRightEye.y), 3, Scalar(255,0,0), 3, 8, 0);
       imshow("result", frame);
       int c = waitKey(10);
       if( c == 27 || c == 'q' || c == 'Q' ) {
          break;
       }
#endif
    }

    PTEyeDetectDeinit(&handler);

    return 0;
}

#endif

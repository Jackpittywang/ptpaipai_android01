package com.sunnybear.library.model.http.progress;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * UI进度回调实体类
 * Created by guchenkai on 2015/10/26.
 */
public class ProgressModel implements Serializable, Parcelable {
    //当前读取字节长度
    private long currentBytes;
    //总字节长度
    private long contentLength;
    //是否读取完成
    private boolean done;

    public ProgressModel(long currentBytes, long contentLength, boolean done) {
        this.currentBytes = currentBytes;
        this.contentLength = contentLength;
        this.done = done;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "ProgressModel{" +
                "currentBytes=" + currentBytes +
                ", contentLength=" + contentLength +
                ", done=" + done +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.currentBytes);
        dest.writeLong(this.contentLength);
        dest.writeByte(done ? (byte) 1 : (byte) 0);
    }

    protected ProgressModel(Parcel in) {
        this.currentBytes = in.readLong();
        this.contentLength = in.readLong();
        this.done = in.readByte() != 0;
    }

    public static final Creator<ProgressModel> CREATOR = new Creator<ProgressModel>() {
        public ProgressModel createFromParcel(Parcel source) {
            return new ProgressModel(source);
        }

        public ProgressModel[] newArray(int size) {
            return new ProgressModel[size];
        }
    };
}

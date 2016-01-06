
package com.putao.camera.http;

public interface IBaseRequest {
    public void start();

    public String getHost();

    public String getMethod();

    public void startPostRequest();

    public void startGetRequest();
}

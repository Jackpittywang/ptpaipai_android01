package com.putao.mtlib.tcp;

/**
 * @author jidongdong
 *         <p/>
 *         2015年7月27日 下午6:19:50
 */
public class PTMessageConfig {

    private String SOCKET_SERVER;
    private int SOCKET_PORT;
    private int SOCKET_HEART_SECOND = 30;

    public final static int SOCKET_TIMOUT = 60 * 1000;

    public final static int SOCKET_READ_TIMOUT = 15 * 1000;

    public final static int SOCKET_SLEEP_SECOND = 3;

    private boolean HEART_ENABLE = true;

    public boolean getHeartEnable() {
        return HEART_ENABLE;
    }

    public void setHeartEnable(boolean enable) {
        HEART_ENABLE = enable;
    }

    public String getHost() {
        return SOCKET_SERVER;
    }

    public int getPort() {
        return SOCKET_PORT;
    }

    public void setHost(String host) {
        this.SOCKET_SERVER = host;
    }

    public void setPort(int port) {
        this.SOCKET_PORT = port;
    }

    public void setHeartSecond(int second) {
        this.SOCKET_HEART_SECOND = second;
    }

    public int getHeartSecond() {
        return SOCKET_HEART_SECOND;
    }

    public static class Builder {
        PTMessageConfig config = new PTMessageConfig();

        public Builder setHost(String host) {
            config.setHost(host);
            return this;
        }

        public Builder setHeartEnable(boolean enable) {
            config.setHeartEnable(enable);
            return this;
        }

        public Builder setPort(int port) {
            config.setPort(port);
            return this;
        }

        public Builder setHeartSecond(int second) {
            config.setHeartSecond(second);
            return this;
        }

        public PTMessageConfig build() {
            return config;
        }
    }

}

package com.putao.mtlib.tcp;

import android.text.TextUtils;

import com.putao.mtlib.util.PTLoger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 *
 */
public class PTTCPClient {
    // 信道选择
    private Selector selector;

    // 与服务器通信的信
    SocketChannel socketChannel;

    // 要连接的服务器Ip地址
    private String hostIp;

    // 要连接的远程服务器在监听的端
    private int hostPort;

    private static PTTCPClient s_Tcp = null;

    public boolean isInitialized = false;

    private static PTMessageConfig config;

    private static boolean mIsConnected = false;

    public static synchronized PTTCPClient instance() {
        if (s_Tcp == null) {
            config = PTSenderManager.sharedInstance().getConfig();
            s_Tcp = new PTTCPClient(config.getHost(), config.getPort());
        }
        return s_Tcp;
    }

    /**
     * @param HostIp
     * @param HostListenningPort
     * @throws IOException
     */
    public PTTCPClient(String HostIp, int HostPort) {
        this.hostIp = HostIp;
        this.hostPort = HostPort;

        try {
            initialize();
            this.isInitialized = true;
        } catch (IOException e) {
            this.isInitialized = false;
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            this.isInitialized = false;
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     */
    public void initialize() throws IOException {
        boolean done = false;
        PTLoger.d("initialize socket channel,current thread id=" + Thread.currentThread().getId());
        try {
            if (TextUtils.isEmpty(hostIp) || hostIp.equalsIgnoreCase("null") || hostPort <= 0) {
                throw new IllegalArgumentException("the host must can't be null and the port must be >0");
            }
            PTLoger.d("HOST:" + hostIp + ",port:" + hostPort);
            // 打开监听信道并设置为非阻塞模
            socketChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostPort));
            if (socketChannel != null) {
                socketChannel.socket().setTcpNoDelay(false);
                socketChannel.socket().setKeepAlive(true);
                // 设置 读socket的timeout时间
                socketChannel.socket().setSoTimeout(PTMessageConfig.SOCKET_READ_TIMOUT);
                socketChannel.configureBlocking(false);

                // 打开并注册择器到信道
                selector = Selector.open();
                if (selector != null) {
                    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                    done = true;
                }
            }
        } catch (IOException e){
             PTLoger.e("connect to server failed");
        }
       /* catch (ConnectException e) {
            PTLoger.e("connect to server failed");
        }*/ catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (!done && selector != null) {
                selector.close();
            }
            if (!done && socketChannel != null) {
                socketChannel.close();
            }
        }
    }

    static void blockUntil(SelectionKey key, long timeout) throws IOException {
        int nkeys = 0;
        if (timeout > 0) {
            nkeys = key.selector().select(timeout);
        } else if (timeout == 0) {
            nkeys = key.selector().selectNow();
        }
        if (nkeys == 0) {
            throw new SocketTimeoutException();
        }
    }

    /**
     * @param message
     * @throws IOException
     */
    public void sendMsg(String message) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("utf-8"));

        if (socketChannel == null) {
            throw new IOException();
        }
        socketChannel.write(writeBuffer);
    }

    /**
     * @param bytes
     * @throws IOException
     */
    public void sendMsg(byte[] bytes) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);

        if (socketChannel == null) {
            throw new IOException();
        }
        socketChannel.write(writeBuffer);
    }

    /**
     * @return
     */
    public synchronized Selector getSelector() {
        return this.selector;
    }

    /**
     * Socket连接是否是正常的
     *
     * @return
     */
    public boolean isConnect() {
        if (this.isInitialized) {
            if (this.socketChannel != null) {
                mIsConnected = this.socketChannel.isConnected();
            }
        }
        return mIsConnected;
    }

    public void setConnectState(boolean isconnect) {
        mIsConnected = isconnect;
        isInitialized = false;
    }

    /**
     * 关闭socket 重新连接
     *
     * @return
     */
    public boolean reConnect() {
        closeTCPSocket();
        PTLoger.i("client reconnect to server");
        try {
            initialize();
            isInitialized = true;
        } catch (IOException e) {
            isInitialized = false;
            e.printStackTrace();
        } catch (Exception e) {
            isInitialized = false;
            e.printStackTrace();
        }
        return isInitialized;
    }

    /**
     * @return
     */
    public boolean canConnectToServer() {
        try {
            if (socketChannel != null) {
                socketChannel.socket().sendUrgentData(0xff);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭socket
     */
    public void closeTCPSocket() {
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }

        } catch (IOException e) {

        }
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     *
     */
    public synchronized void repareRead() {
        if (socketChannel != null) {
            try {
                selector = Selector.open();
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

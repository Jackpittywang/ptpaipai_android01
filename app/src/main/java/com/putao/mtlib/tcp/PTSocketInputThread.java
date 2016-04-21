package com.putao.mtlib.tcp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.putao.mtlib.jni.MsgpackJNI;
import com.putao.mtlib.model.CS_NOTICEACK;
import com.putao.mtlib.model.MessagePackData;
import com.putao.mtlib.util.MsgPackUtil;
import com.putao.mtlib.util.PTLoger;
import com.sunnybear.library.util.Logger;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author jidongdong
 *         <p/>
 *         2015年7月27日 下午6:20:45
 */
public class PTSocketInputThread extends Thread {
    private Context mContext;
    private int bodylen = -1;
    private static final int MESSAGE_LENGTH_HEAD = 6;
    private byte[] head = new byte[6];
    private boolean isStart = true;
    private OnReceiveMessageListener mOnSocketResponseListener;

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread() {
                @Override
                public void run() {
                    readSocket();
                }
            }.start();
            mHandler.postDelayed(this, 3 * 1000);
        }
    };

    public PTSocketInputThread(Context context) {
        this.mContext = context;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
        if (!isStart)
            mHandler.removeCallbacks(runnable);
    }

    /**
     * set response listener
     *
     * @param listener
     */
    public void setOnSocketResponseListener(OnReceiveMessageListener listener) {
        mOnSocketResponseListener = listener;
    }

    @Override
    public void run() {
//        while (isStart) {
        // 手机能联网，读socket数据
//            if (NetManager.instance().isNetworkConnected()) {
////                if (!PTTCPClient.instance().isConnect()) {
//                try {
//                    sleep(PTMessageConfig.SOCKET_SLEEP_SECOND * 1000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
////                }
//                readSocket();
//            }
//        }
        mHandler.post(runnable);
    }

    /**
     * 处理消息体
     *
     * @param msg
     */
    void handlePTMessage(PTMessage msg) {
        Logger.d("ptl----收到消息,type=" + msg.type);
        if (msg.length > 0) {
            printBuffer(msg.data, msg.data.length);
        }
        String data = "";
        switch (msg.type) {
            case PTMessageType.SC_CONNECTACK:
                data = MsgpackJNI.UnPackMessage(msg.data, msg.length);
                break;
            case PTMessageType.SC_NOTICE:
                MessagePackData msgData = new MessagePackData();
                try {
                    msgData = MsgpackJNI.unpackMessageData(msg.data, msg.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(PTMessageReceiver.RedAction);
                Bundle redActionBundle = new Bundle();
                redActionBundle.putString(PTMessageReceiver.KeyMessage, msgData.getMsg());
                intent.putExtras(redActionBundle);
                mContext.sendBroadcast(intent);
               /* PTSenderManager.sharedInstance().sendMsg(PTMessageUtil.getMesageByteArray(PTMessageType.CS_NOTICEACK, null),
                        null);*/
                sendConnectValidate(msgData.getMsgId());
                break;
        }

        if (msg.type > 0) {
            PTRecMessage receiveMsg = new PTRecMessage(msg.type, data);
            if (mOnSocketResponseListener != null)
                mOnSocketResponseListener.onResponse(receiveMsg);
        }
    }

    /**
     * 发送连接验证
     */
    public void sendConnectValidate(int msgId) {
        CS_NOTICEACK connect = new CS_NOTICEACK();
        connect.setMsgId(msgId);
        PTSenderManager.sharedInstance().sendMsg(MsgPackUtil.Pack(connect, PTMessageType.CS_NOTICEACK));
    }

    /**
     * @param buff
     * @param plength
     */
    void printBuffer(byte[] buff, int plength) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < plength; i++) {
            sb.append(buff[i] & 0xff);
            if (i < plength - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        PTLoger.d(sb.toString());
    }

    /**
     * @param buffer
     */
    void analyzeRecData(ByteBuffer buffer) {
        while (buffer.remaining() > 0) {
            if (bodylen < 0) {
                // enough to bulid header
                if (buffer.remaining() >= MESSAGE_LENGTH_HEAD) {
                    buffer.get(head, 0, 6);
                    bodylen = PTMessageUtil.getPacketBodySize(head);
                } else {// not enough to bulid a header
                    break;
                }
            } else if (bodylen > 0) {
                // enough to bulid a body
//                if (buffer.remaining() >= bodylen) {
                byte[] body = new byte[buffer.remaining()];
                buffer.get(body, 0, buffer.remaining());
                bodylen = -1;
                handlePTMessage(new PTMessage(PTMessageUtil.getMessageType(head), bodylen, body));
//                } else { // not enough to bulid a body
//                    break;
//                }
            } else if (bodylen == 0) {// only header
                handlePTMessage(new PTMessage(PTMessageUtil.getMessageType(head), 0, null));
                bodylen = -1;
            }
        }
    }

    public void readSocket() {
        Selector selector = PTTCPClient.instance().getSelector();
        if (selector == null) return;
        try {
            while (selector.select() > 0 && !Thread.interrupted()) {
                for (SelectionKey sk : selector.selectedKeys()) {
                    if (sk.isReadable()) {
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1000);
                        try {
                            int read = sc.read(buffer);
                            if (read == -1)
                                break;
                            if (read > 0) {
                                printBuffer(buffer.array(), read);
                            }
                        } catch (SocketException e) {
                            PTLoger.e("socket exception");
                            PTTCPClient.instance().setConnectState(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        buffer.flip();
                        analyzeRecData(buffer);
                        try {
                            sk.interestOps(SelectionKey.OP_READ);
                            selector.selectedKeys().remove(sk);
                        } catch (CancelledKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClosedSelectorException e2) {
        }
    }
}
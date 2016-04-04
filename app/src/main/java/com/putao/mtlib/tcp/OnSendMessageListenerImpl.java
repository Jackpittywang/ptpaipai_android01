package com.putao.mtlib.tcp;

import com.putao.mtlib.util.PTLoger;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月28日 下午4:50:40
 *
 */
public class OnSendMessageListenerImpl implements OnSendMessageListener {

	@Override
	public void onSend(int result) {
		// TODO Auto-generated method stub
		PTLoger.d("send result:" + ((result == PTSendMsgHandle.SEND_SUCCESS) ? "success" : "failed"));
	}

}

package com.putao.mtlib.tcp;

public interface ISocketResponse<T> {
	public void onResponse(T response);
}

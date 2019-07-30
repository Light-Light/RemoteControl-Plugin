package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;

import com.qing_guang.RemoteControl.util.ExceptionHandler;

/**
 * ����Ŀͻ����쳣������
 * @author Qing_Guang
 *
 */
public abstract class AbstractClientExceptionHandler implements ExceptionHandler<IOException>{

	/**
	 * �ͻ���
	 */
	protected RemoteControlClient client;
	
	/**
	 * ���ô˴���������Ӧ�Ŀͻ���
	 * @param client
	 */
	public final void setClient(RemoteControlClient client) {
		this.client = client;
	}
	
	/**
	 * ��¡һ�����������
	 * @see java.lang.Object#clone()
	 */
	public abstract AbstractClientExceptionHandler clone();
	
}

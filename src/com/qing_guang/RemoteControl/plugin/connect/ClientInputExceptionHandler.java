package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.util.logging.Logger;

import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;

/**
 * Ĭ�ϵĿͻ������ݽ����쳣
 * @author Qing_Guang
 *
 */
public class ClientInputExceptionHandler extends AbstractClientExceptionHandler{
	
	private Logger logger;
	
	/**
	 * ����һ��logger���½�һ���쳣���������
	 */
	public ClientInputExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see com.qing_guang.RemoteControl.util.ExceptionHandler#handle()
	 */
	public void handle(IOException exc) {
		if(client != null && client.isRunning()) {
			client.getServer().disconn(client, DisconnectionCause.INPUT_ERROR);
			logger.warning("��һ���ͻ��������������쳣�ѶϿ�����");
			logger.warning("�û���: " + client.getUname());
			logger.warning("�����Ǳ�����Ϣ:");
			exc.printStackTrace();
		}
	}

	/**
	 * @see AbstractClientExceptionHandler#clone()
	 */
	public AbstractClientExceptionHandler clone() {
		return new ClientInputExceptionHandler(logger);
	}
	
}
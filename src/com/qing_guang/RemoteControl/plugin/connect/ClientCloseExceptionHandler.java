package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.util.logging.Logger;

import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;

/**
 * Ĭ�ϵĿͻ��˹ر��쳣������
 * @author A
 *
 */
public class ClientCloseExceptionHandler extends AbstractClientExceptionHandler{

	private Logger logger;
	
	/**
	 * ����һ��logger���½�һ���쳣���������
	 */
	public ClientCloseExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see com.qing_guang.RemoteControl.util.ExceptionHandler#handle()
	 */
	public void handle(IOException exc) {
		if(client != null && client.isRunning()) {
			client.getServer().disconn(client, DisconnectionCause.INPUT_ERROR);
			logger.warning("��һ���ͻ��˶Ͽ�����ʱ�����쳣");
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

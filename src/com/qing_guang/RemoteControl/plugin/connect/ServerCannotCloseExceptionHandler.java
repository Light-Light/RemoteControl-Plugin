package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.util.logging.Logger;

import com.qing_guang.RemoteControl.util.ExceptionHandler;

/**
 * Ĭ�ϵķ������޷��رյ��쳣������
 * @author Qing_Guang
 *
 */
public class ServerCannotCloseExceptionHandler implements ExceptionHandler<IOException>{

	private Logger logger;
	
	/**
	 * ����һ��logger���½�һ���쳣���������
	 */
	public ServerCannotCloseExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see com.qing_guang.RemoteControl.util.ExceptionHandler#handle()
	 */
	public void handle(IOException exc) {
		
		logger.warning("�������޷������ر�!�������Ĵ�����־��������!");
		exc.printStackTrace();
		
	}

}

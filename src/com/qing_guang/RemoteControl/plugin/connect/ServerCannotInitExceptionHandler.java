package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.util.logging.Logger;

import com.qing_guang.RemoteControl.util.ExceptionHandler;

/**
 * Ĭ�ϵķ������޷��������쳣������
 * @author Qing_Guang
 *
 */
public class ServerCannotInitExceptionHandler implements ExceptionHandler<IOException>{
	
	private Logger logger;
	
	/**
	 * ����һ��logger���½�һ���쳣���������
	 */
	public ServerCannotInitExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	/**
	 * @see com.qing_guang.RemoteControl.util.ExceptionHandler#handle()
	 */
	public void handle(IOException exc) {
		
		logger.warning("��������Ϊ�˿ڱ�ռ�û�������ԭ���޷�����,��ȷ�Ϻ�������ָ�����¿���");
		logger.warning("�����Ǵ�����־");
		exc.printStackTrace();
		
	}

}

package com.qing_guang.RemoteControl.plugin.setout;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * ��̨��Ϣ��¼��
 * @author Qing_Guang
 *
 */
public final class Recorder {

	/**
	 * Ĭ�ϵĺ�̨��Ϣ������
	 */
	public final static LogAppender APPENDER = new LogAppender();
	/**
	 * �����Ψһʵ��������
	 */
	public final static Recorder INSTANCE = new Recorder();
	
	/**
	 * ���ô˿շ����Գ�ʼ�� APPENDER �� INSTANCE �ֶ�
	 */
	public static void init() {}
	
	//���췽��������
	private Recorder() {
		
		if(INSTANCE != null) {
			throw new IllegalAccessError();
		}
		
		Logger logger = (Logger)LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
		logger.addAppender(APPENDER);
		
	}
	
	/**
	 * ��ȡ�����ڻ�������ĺ�̨��Ϣ
	 * @return �����ڻ�������ĺ�̨��Ϣ
	 */
	public List<String> getAll(){
		List<String> list = new ArrayList<>();
		synchronized (LogAppender.BUFFER) {
			for(int i = 0;i < LogAppender.BUFFER.size();i++) {
				list.add(LogAppender.BUFFER.get());
			}
			LogAppender.BUFFER.insertToStart();
		}
		return list;
	}
	
}

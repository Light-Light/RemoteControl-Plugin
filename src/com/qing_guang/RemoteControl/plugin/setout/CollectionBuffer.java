package com.qing_guang.RemoteControl.plugin.setout;

import com.qing_guang.RemoteControl.util.Buffer;

/**
 * ʵ��һ�����ƿ����ɴ�С�Ļ�����
 * @author Qing_Guang
 * @param <E> ���������ɵ���������
 * @see com.qing_guang.RemoteControl.util.Buffer
 */
public class CollectionBuffer<E> extends Buffer<E>{
	
	private int size;
	
	/**
	 * �½�һ��ָ�������Ļ�����
	 * @param size ������С
	 * @throws IllegalArgumentException ������Ĵ�СС��0ʱ�׳�
	 */
	public CollectionBuffer(int size) throws IllegalArgumentException{
		if(size < 0) {
			throw new IllegalArgumentException("The buffer size can't less than 0");
		}
		this.size = size;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void add(E e) {
		super.add(e);
		if(size() > size) {
			insertTo(insertWhere() + 1);
			clear(1);
		}
	}
	
}

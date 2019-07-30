package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * ������̨������Ϣʱ������
 * @author Qing_Guang
 *
 */
public class NewBackstageInfoEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private String content;
	
	/**
	 * �½����¼�����
	 * @param content ����Ϣ����
	 */
	public NewBackstageInfoEvent(String content) {
		this.content = content;
	}
	
	/**
	 * ����Ϣ����
	 */
	public String getContent() {
		return content;
	}

}

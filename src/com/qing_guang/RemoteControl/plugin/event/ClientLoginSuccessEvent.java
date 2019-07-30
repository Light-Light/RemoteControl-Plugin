package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.qing_guang.RemoteControl.plugin.connect.RemoteControlClient;

/**
 * ��һ���ͻ��˳ɹ���½ʱ������
 * @author Qing_Guang
 *
 */
public class ClientLoginSuccessEvent extends Event{
	
	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private RemoteControlClient client;
	
	/**
	 * �½����¼�����
	 * @param client �ɹ���½�Ŀͻ���
	 */
	public ClientLoginSuccessEvent(RemoteControlClient client) {
		this.client = client;
	}
	
	/**
	 * �ɹ���½�Ŀͻ���
	 */
	public RemoteControlClient getClient() {
		return client;
	}
	
}

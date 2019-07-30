package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;

/**
 * ���ͻ��˱�����ʱ������
 * @author Qing_Guang
 *
 */
public class ClientDisconnectEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private String uname;
	private DisconnectionCause cause;
	private boolean success;
	
	/**
	 * �½����¼�����
	 * @param uname �����ߵĿͻ��˵��û���
	 * @param cause ����ԭ��
	 * @param success �����Ƿ�ɹ�
	 */
	public ClientDisconnectEvent(String uname,DisconnectionCause cause,boolean success) {
		this.uname = uname;
		this.cause = cause;
		this.success = success;
	}
	
	/**
	 * �û���
	 */
	public String getUname() {
		return uname;
	}
	
	/**
	 * ����ԭ��
	 */
	public DisconnectionCause getCause() {
		return cause;
	}
	
	/**
	 * �Ƿ�ɹ�
	 */
	public boolean isSuccess() {
		return success;
	}
	
}

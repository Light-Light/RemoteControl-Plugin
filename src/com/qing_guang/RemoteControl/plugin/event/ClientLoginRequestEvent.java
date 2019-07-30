package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * ���ͻ��˷��͵�½����ʱ������
 * @author Qing_Guang
 *
 */
public class ClientLoginRequestEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private String uname;
	private String pwd;
	private boolean isCancelled;
	
	/**
	 * �½����¼�����
	 * @param uname �����½���û���
	 * @param pwd �����½������(δ����md5����)
	 */
	public ClientLoginRequestEvent(String uname,String pwd) {
		this.uname = uname;
		this.pwd = pwd;
	}
	
	/**
	 * �û���
	 */
	public String getUname() {
		return uname;
	}
	
	/**
	 * ����(δ����md5����)
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @see org.bukkit.event.Cancellable#isCancelled()
	 */
	public boolean isCancelled() {
		return isCancelled;
	}

	/**
	 * @see org.bukkit.event.Cancellable#setCancelled(boolean)
	 */
	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}
	
}

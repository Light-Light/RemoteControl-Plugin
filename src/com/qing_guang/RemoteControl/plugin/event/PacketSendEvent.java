package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.qing_guang.RemoteControl.packet.Packet;

/**
 * ��һ�����ݰ���Ҫ����ӵ����������ʱ������
 * @author Qing_Guangy
 *
 */
public class PacketSendEvent extends Event implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private Packet<?> pkt;
	private boolean encrypt;
	private boolean rsa_or_aes;
	private boolean isCancelled;

	/**
	 * �½����¼�����
	 * @param pkt ��Ҫ����ӵ���������������ݰ�
	 * @param encrypt �Ƿ����
	 * @param rsa_or_aes ���͵ļ����㷨,��Ϊtrue��ʹ��rsa�㷨����,����ʹ��aes�㷨����
	 */
	public PacketSendEvent(Packet<?> pkt, boolean encrypt, boolean rsa_or_aes) {
		super();
		this.pkt = pkt;
		this.encrypt = encrypt;
		this.rsa_or_aes = rsa_or_aes;
	}

	/**
	 * ��Ҫ����ӵ���������������ݰ�
	 */
	public Packet<?> getPkt() {
		return pkt;
	}

	/**
	 * �Ƿ����
	 */
	public boolean isEncrypt() {
		return encrypt;
	}

	/**
	 * �����Ƿ����
	 */
	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 * ���͵ļ����㷨,��Ϊtrue��ʹ��rsa�㷨����,����ʹ��aes�㷨����
	 */
	public boolean encryptAlg() {
		return rsa_or_aes;
	}

	/**
	 * ���÷��͵ļ����㷨,��Ϊtrue��ʹ��rsa�㷨����,����ʹ��aes�㷨����
	 */
	public void setEncryptAlg(boolean rsa_or_aes) {
		this.rsa_or_aes = rsa_or_aes;
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

package com.qing_guang.RemoteControl.plugin.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.qing_guang.RemoteControl.packet.Packet;
import com.qing_guang.RemoteControl.plugin.connect.RemoteControlClient;

/**
 * ��һ���ͻ��˽��յ����ݰ�ʱ������
 * @author Qing_Guang
 *
 */
public class PacketReceiveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private Packet<?> pkt;
	private RemoteControlClient client;

	/**
	 * �½����¼�����
	 * @param pkt ���յ������ݰ�
	 * @param client ���յ����ݰ��Ŀͻ���
	 */
	public PacketReceiveEvent(Packet<?> pkt, RemoteControlClient client) {
		super();
		this.pkt = pkt;
		this.client = client;
	}

	/**
	 * ���յ������ݰ�
	 */
	public Packet<?> getPkt() {
		return pkt;
	}

	/**
	 * ���յ����ݰ��Ŀͻ���
	 */
	public RemoteControlClient getClient() {
		return client;
	}
	
}

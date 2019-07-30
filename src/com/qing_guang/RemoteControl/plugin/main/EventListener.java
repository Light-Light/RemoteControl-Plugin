package com.qing_guang.RemoteControl.plugin.main;

import java.util.logging.Logger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.qing_guang.RemoteControl.packet.server.BackstageInfoPacket;
import com.qing_guang.RemoteControl.packet.server.OnlineModeChangePacket;
import com.qing_guang.RemoteControl.packet.server.OnlineModeChangePacket.Type;
import com.qing_guang.RemoteControl.plugin.connect.RemoteControlClient;
import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;
import com.qing_guang.RemoteControl.plugin.event.ClientDisconnectEvent;
import com.qing_guang.RemoteControl.plugin.event.ClientLoginSuccessEvent;
import com.qing_guang.RemoteControl.plugin.event.NewBackstageInfoEvent;

/**
 * �¼�������
 * @author Qing_Guang
 *
 */
public class EventListener implements Listener{
	
	//Ĭ�ϵĿͻ��˳ɹ���½������
	@EventHandler
	public void success(ClientLoginSuccessEvent event) {
		
		Logger logger = JavaPlugin.getPlugin(Main.class).getLogger();
		logger.info("��һ���ͻ����Ѵ������Ӳ���½");
		logger.info("�û���: " + event.getClient().getUname());
		
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.CLIENT,true,event.getClient().getUname()), false);
		}
		
	}
	
	//Ĭ�ϵĿ���̨����Ϣ������
	//��ϲ�㷢���˲ʵ�23333
	@EventHandler
	public void ignb_new_bkstg_info(NewBackstageInfoEvent event) {
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new BackstageInfoPacket(event.getContent()), false);
		}
	}
	
	//Ĭ�ϵĿͻ��˶��߼�����
	@EventHandler
	public void disconn(ClientDisconnectEvent event) {
		
		if(event.getCause() != DisconnectionCause.INPUT_ERROR && event.getCause() != DisconnectionCause.OUTPUT_ERROR && event.getCause() != DisconnectionCause.SERVER_CLOSE) {
			
			Logger logger = JavaPlugin.getPlugin(Main.class).getLogger();
			logger.info("��һ���ͻ����ѶϿ�����");
			logger.info("�û���: " + event.getUname());
			logger.info("�Ͽ�ԭ��: " + event.getCause());
			logger.info("�Ƿ�ɹ�: " + (event.isSuccess() ? "��" : "��"));
			
		}
		
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.CLIENT,false,event.getUname()), false);
		}
		
	}
	
	//�������������
	@EventHandler
	public void onEnable(PluginEnableEvent event) {
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.PLUGIN, true, event.getPlugin().getName()),false);
		}
	}
	
	//����رռ�����
	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.PLUGIN, false, event.getPlugin().getName()),false);
		}
	}
	
	//��Ҽ��������
	@EventHandler
	public void join(PlayerJoinEvent event) {
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.PLAYER,true,event.getPlayer().getName()), false);
		}
	}
	
	//����˳�������
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		for(RemoteControlClient client : Main.server.getClients()) {
			client.addPacketWillSend(new OnlineModeChangePacket(Type.CLIENT,false,event.getPlayer().getName()), false);
		}
	}

}

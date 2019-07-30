package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.qing_guang.RemoteControl.lib.main.Main;
import com.qing_guang.RemoteControl.packet.server.ManualDisconnectedPacket;
import com.qing_guang.RemoteControl.packet.server.ServerClosePacket;
import com.qing_guang.RemoteControl.plugin.event.ClientDisconnectEvent;
import com.qing_guang.RemoteControl.util.ExceptionHandler;

/**
 * Ĭ�ϵķ�����ʵ����
 * @author Qing_Guang
 *
 */
public class RemoteControlServer extends Thread{

	private int port;
	private int max_client;
	private boolean isRunning;
	private ServerSocket server;
	private RemoteControlClient client_temp;
	private ExceptionHandler<IOException> server_cant_init;
	private ExceptionHandler<IOException> server_cant_stop;
	private AbstractClientExceptionHandler client_write_exc;
	private AbstractClientExceptionHandler client_read_exc;
	private AbstractClientExceptionHandler client_close_exc;
	Map<String,RemoteControlClient> logged;
	
	/**
	 * ����һ��Ĭ�ϵķ�����
	 * @param port ʹ�õĶ˿�
	 * @param max_client �������Ŀͻ�������
	 * @param client_temp �ͻ��˵�ģ��
	 * @param server_cant_init �������޷��������쳣������
	 * @param server_cant_stop �������޷��رյ��쳣������
	 * @param client_write_exc �ͻ�����������쳣������(ģ��)
	 * @param client_read_exc �ͻ��˽��������쳣������(ģ��)
	 * @param client_close_exc �޷��رտͻ��˵��쳣������(ģ��)
	 */
	public RemoteControlServer(int port,int max_client,RemoteControlClient client_temp
			,ExceptionHandler<IOException> server_cant_init,ExceptionHandler<IOException> server_cant_stop
			,AbstractClientExceptionHandler client_write_exc,AbstractClientExceptionHandler client_read_exc
			,AbstractClientExceptionHandler client_close_exc) {
		this.port = port;
		this.max_client = max_client;
		this.client_temp = client_temp;
		this.server_cant_init = server_cant_init;
		this.server_cant_stop = server_cant_stop;
		this.client_write_exc = client_write_exc;
		this.client_read_exc = client_read_exc;
		this.client_close_exc = client_close_exc;
		logged = new LinkedHashMap<>();
	}
	
	/**
	 * ��ʼ���з�����
	 */
	public void run(){
		
		try {
			
			server = new ServerSocket(port);
			isRunning = true;
			
		}catch(IOException e) {
			server_cant_init.handle(e);
		}
			
		while(isRunning()) {
			
			try {
			
				if(logged.size() >= max_client) {
					continue;
				}
				
				Socket c = server.accept();
				RemoteControlClient client = client_temp.clone();
				client.init(c, client_write_exc.clone(), client_read_exc.clone(), this);
				client.start();
				
				System.out.println(123);
			
			}catch (IOException e){
			}
			
		}
		
	}
	
	/**
	 * �������Ƿ�����
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * �رշ�����
	 */
	public void disable() {
		
		isRunning = false;
		
		try {
			
			new Socket("localhost",port).close();
			server.close();
			
			for(String uname : logged.keySet()) {
				RemoteControlClient client = logged.get(uname);
				client.addPacketWillSend(new ServerClosePacket(),false);
				disconn(client,DisconnectionCause.SERVER_CLOSE);
			}
			
			logged.clear();
			
		} catch (IOException e) {
			server_cant_stop.handle(e);
		}
	}
	
	/**
	 * ����һ���ͻ���
	 * @param uname �ͻ��˵��û���
	 * @param cause ����ԭ��
	 * @throws IllegalArgumentException �����û���û�б���½ʱ�׳�
	 * @see #disconn(RemoteControlClient,DisconnectionCause)
	 */
	public void disconn(String uname,DisconnectionCause cause) throws IllegalArgumentException{
		if(getClient(uname) == null) {
			throw new IllegalArgumentException("Client not online");
		}
		disconn(getClient(uname),cause);
	}
	
	/**
	 * ����һ���ͻ���
	 * @param client �ͻ���
	 * @param cause ����ԭ��
	 */
	public void disconn(RemoteControlClient client,DisconnectionCause cause) {
		
		new Thread(() -> {
			
			boolean success = false;
			
			if(cause == DisconnectionCause.MANUAL) {
				try {
					client.addPacketWillSend(new ManualDisconnectedPacket(),false);
					waitForSendOver(client);
					client.disconn();
					success = true;
				} catch (IOException exc) {
					AbstractClientExceptionHandler client_close_exc = this.client_close_exc.clone();
					client_close_exc.setClient(client);
					client_close_exc.handle(exc);
					success = false;
				}
			}else if(cause == DisconnectionCause.CLIENT_EXIT){
				try {
					client.disconn();
					success = true;
				}catch(IOException exc) {
					AbstractClientExceptionHandler client_close_exc = this.client_close_exc.clone();
					client_close_exc.setClient(client);
					client_close_exc.handle(exc);
					success = false;
				}
			}else if(cause == DisconnectionCause.SERVER_CLOSE){
				try {
					client.addPacketWillSend(new ServerClosePacket(),false);
					waitForSendOver(client);
					client.disconn();
					success = true;
				}catch(IOException exc) {
					success = false;
				}
			}else if(cause == DisconnectionCause.INPUT_ERROR || cause == DisconnectionCause.OUTPUT_ERROR) {
				try {
					client.disconn();
					success = true;
				}catch(IOException exc) {
				}
			}
			
			logged.remove(client.getUname());
			
			boolean suc = success;
			Bukkit.getScheduler().callSyncMethod(JavaPlugin.getPlugin(Main.class), () -> {
				Bukkit.getPluginManager().callEvent(new ClientDisconnectEvent(client.getUname(),cause,suc));
				return null;
			});
			
		}).start();
		
	}
	
	/**
	 * �ͻ����Ƿ�����
	 * @param uname �ͻ��˵��û���
	 */
	public boolean isOnline(String uname) {
		return logged.containsKey(uname);
	}
	
	/**
	 * ��ȡ���еĿͻ���
	 */
	public Collection<RemoteControlClient> getClients(){
		return logged.values();
	}
	
	/**
	 * ��ȡ�ͻ���
	 * @param uname �ͻ��˵��û���
	 */
	public RemoteControlClient getClient(String uname) {
		return logged.get(uname);
	}
	
	/**
	 * ���ÿͻ��˵�ģ��
	 */
	public void setClientTemp(RemoteControlClient client_temp) {
		this.client_temp = client_temp;
	}
	
	/**
	 * ���÷������޷��رյ��쳣������
	 */
	public void setServerCantStopExcHandler(ExceptionHandler<IOException> server_cant_stop) {
		this.server_cant_stop = server_cant_stop;
	}
	
	/**
	 * ���ÿͻ�����������쳣������(ģ��)
	 */
	public void setClientWriteExcHandlerTemp(AbstractClientExceptionHandler client_write_exc) {
		this.client_write_exc = client_write_exc;
	}
	
	/**
	 * �ͻ��˽��������쳣������(ģ��)
	 */
	public void setClientReadExcHandlerTemp(AbstractClientExceptionHandler client_read_exc) {
		this.client_read_exc = client_read_exc;
	}
	
	/**
	 * �޷��رտͻ��˵��쳣������(ģ��)
	 */
	public void setClientCloseExcHandlerTemp(AbstractClientExceptionHandler client_close_exc) {
		this.client_close_exc = client_close_exc;
	}
	
	private void waitForSendOver(RemoteControlClient client) {
		while(!client.noSendTask()) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ����ԭ��
	 * @author Qing_Guang
	 *
	 */
	public enum DisconnectionCause{
		
		/**
		 * �ֶ�
		 */
		MANUAL,
		
		/**
		 * �ͻ����˳�
		 */
		CLIENT_EXIT,
		
		/**
		 * �������ر�
		 */
		SERVER_CLOSE,
		
		/**
		 * �����쳣
		 */
		INPUT_ERROR,
		
		/**
		 * ����쳣
		 */
		OUTPUT_ERROR
	}
	
}

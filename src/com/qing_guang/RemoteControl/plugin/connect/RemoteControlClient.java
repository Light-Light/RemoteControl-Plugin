package com.qing_guang.RemoteControl.plugin.connect;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.qing_guang.RemoteControl.packet.Packet;
import com.qing_guang.RemoteControl.packet.client.ClientExitPacket;
import com.qing_guang.RemoteControl.packet.client.ClientVerifyPacket;
import com.qing_guang.RemoteControl.packet.client.CommandLinePacket;
import com.qing_guang.RemoteControl.packet.client.LoginAccountPacket;
import com.qing_guang.RemoteControl.packet.server.AESKeyPacket;
import com.qing_guang.RemoteControl.packet.server.BackstageInfoPacket;
import com.qing_guang.RemoteControl.packet.server.EncryptRequirePacket;
import com.qing_guang.RemoteControl.packet.server.OnlineModeChangePacket;
import com.qing_guang.RemoteControl.packet.server.OnlineModeChangePacket.Type;
import com.qing_guang.RemoteControl.packet.server.PluginInfoPacket;
import com.qing_guang.RemoteControl.packet.server.RefuseOperatePacket;
import com.qing_guang.RemoteControl.packet.server.ServerInfoPacket;
import com.qing_guang.RemoteControl.packet.server.ServerVerifyPacket;
import com.qing_guang.RemoteControl.packet.server.SuccessfulLoginPacket;
import com.qing_guang.RemoteControl.packet.server.WorldInfoPacket;
import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;
import com.qing_guang.RemoteControl.plugin.event.ClientLoginRequestEvent;
import com.qing_guang.RemoteControl.plugin.event.ClientLoginSuccessEvent;
import com.qing_guang.RemoteControl.plugin.event.PacketSendEvent;
import com.qing_guang.RemoteControl.plugin.main.Main;
import com.qing_guang.RemoteControl.plugin.setout.Recorder;
import com.qing_guang.RemoteControl.util.CommunicateEncryptUtil;
import com.qing_guang.RemoteControl.util.channel.ConnectChannel;
import com.qing_guang.RemoteControl.util.channel.WriteChannel;

/**
 * Ĭ�ϵĿͻ���ʵ����
 * @author Qing_Guang
 *
 */
public class RemoteControlClient extends Thread implements Cloneable{

	private boolean isRunning;
	private String version;
	private String uname;
	private ConnectChannel channel;
	private AbstractClientExceptionHandler client_write_exc;
	private AbstractClientExceptionHandler client_read_exc;
	private RemoteControlServer server;
	
	/**
	 * �ͻ������е�������
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		channel.start();
		isRunning = true;
		long timeout = 30000;
		
		try {
			
			ClientVerifyPacket cvp = inst(readPacket(false,false,timeout),ClientVerifyPacket.class,true);
			if(cvp.getVersion() == null) {
				throw new UnsupportedOperationException();
			}
			
			addPacketWillSend(new ServerVerifyPacket(JavaPlugin.getPlugin(Main.class).getDescription().getVersion()));
			addPacketWillSend(new EncryptRequirePacket());
			com.qing_guang.RemoteControl.packet.client.RSAPublicKeyPacket pp = inst(readPacket(false,false,timeout),com.qing_guang.RemoteControl.packet.client.RSAPublicKeyPacket.class,true);
			PublicKey pubkey = null;
			if(pp.getPubkey() == null || pp.getCharset() == null || (pubkey = CommunicateEncryptUtil.loadPublicKey(pp.getPubkey())) == null) {
				throw new UnsupportedOperationException();
			}else{
				KeyPair pair = CommunicateEncryptUtil.buildRSAKeyPair(512);
				String aes_key = CommunicateEncryptUtil.randomAESKey();
				channel.getWriteChannel().setAESKey(aes_key);
				channel.getWriteChannel().setRSAKey(pubkey);
				channel.getReadChannel().setAESKey(aes_key);
				channel.getReadChannel().setRSAKey(pair.getPrivate());
				com.qing_guang.RemoteControl.packet.server.RSAPublicKeyPacket rpk = new com.qing_guang.RemoteControl.packet.server.RSAPublicKeyPacket(pair.getPublic().getEncoded(),Charset.defaultCharset().displayName());
				addPacketWillSend(rpk);
				addPacketWillSend(new AESKeyPacket(aes_key),true);
			}
			
			timeout += 1000;
			LoginAccountPacket lap = inst(readPacket(true,false,timeout),LoginAccountPacket.class,true);
			if(Main.ACCOUNTS.containsKey(lap.getUname()) && Main.ACCOUNTS.get(lap.getUname()).getPwd().equals(CommunicateEncryptUtil.getMD5String(lap.getPwd()))) {
				if(!server.isOnline(lap.getUname())) {
					ClientLoginRequestEvent event = new ClientLoginRequestEvent(lap.getUname(),lap.getPwd());
					Bukkit.getPluginManager().callEvent(event);
					if(!event.isCancelled()) {
						SuccessfulLoginPacket pkt = new SuccessfulLoginPacket();
						addPacketWillSend(pkt,false);
						register(lap.getUname());
						action();
					}else {
						addPacketWillSend(new RefuseOperatePacket("Another Plugin or Console already cancelled the login request"),false);
						throw new UnsupportedOperationException();
					}
				}else {
					addPacketWillSend(new SuccessfulLoginPacket(SuccessfulLoginPacket.LoginFailureReason.ALREADY_LOGGED),false);
				}
			}else {
				addPacketWillSend(new SuccessfulLoginPacket(SuccessfulLoginPacket.LoginFailureReason.UNAME_OR_PASSWORD_WRONG),false);
			}
			
		}catch(InterruptedException e){
			try {
				channel.close();
			} catch (IOException e1) {
			}
		}catch(TimeoutException  | UnsupportedClassTypeException | UnsupportedOperationException e) {
			try {
				addPacketWillSend(new RefuseOperatePacket("Incorrect login process"));
				while(!noSendTask()) {
					Thread.sleep(20);
				}
				channel.close();
			} catch (IOException | InterruptedException e1) {
			}
			return;
		}
	}
	
	/**
	 * ��ȡһ�����ݰ�(����)
	 * @param decrypt �Ƿ�ʹ�ý����㷨����
	 * @param rsa_or_aes �����㷨,Ϊtrue��ʹ��rsa����,����ʹ��aes����
	 * @param brk ����Callable.call()����falseʱ,����ֹͣ����������null
	 * @return ��ȡ�������ݰ�,��û��ȡ����brk.call()����falseʱ����null
	 * @throws Exception ��brk.call()�����쳣��Thread.sleep()����InterruptedExceptionʱ�׳�
	 * @see com.qing_guang.RemoteControl.util.channel.ReadChannel#getPacket()
	 * @see com.qing_guang.RemoteControl.util.channel.ReadChannel#getPacket(boolean)
	 * @see java.lang.InterruptedException
	 */
	public Packet<?> readPacket(boolean decrypt,boolean rsa_or_aes,Callable<Boolean> brk) throws Exception {
		try {
			while(isRunning() && (brk != null ? brk.call() : true)) {
				Packet<?> pkt;
				if((pkt = (decrypt ? channel.getReadChannel().getPacket(rsa_or_aes) : channel.getReadChannel().getPacket())) != null) {
					return pkt;
				}
				Thread.sleep(20);
			}
		} catch (UnsupportedEncodingException | InterruptedException e) {
		} catch (Exception e) {
			throw e;
		}
		return null;
	}
	
	/**
	 * ��ȡһ�����ݰ�(����,�����ȴ�ʱ��)
	 * @param decrypt �Ƿ�ʹ�ý����㷨����
	 * @param rsa_or_aes �����㷨,Ϊtrue��ʹ��rsa����,����ʹ��aes����
	 * @param timeout ���ȴ�ʱ��
	 * @return ��ȡ�������ݰ�,��û��ȡ����brk.call()����falseʱ����null
	 * @throws TimeoutException ���ȴ�ʱ������timeoutʱ�׳�
	 * @throws InterruptedException ���java.lang.Thread.join(long)
	 * @see #readPacket(boolean,boolean,Callable)
	 * @see com.qing_guang.RemoteControl.util.channel.ReadChannel#getPacket()
	 * @see com.qing_guang.RemoteControl.util.channel.ReadChannel#getPacket(boolean)
	 * @see java.util.concurrent.TimeoutException
	 * @see java.lang.InterruptedException
	 */
	public Packet<?> readPacket(boolean decrypt,boolean rsa_or_aes,long timeout) throws TimeoutException, InterruptedException{
		
		class AnoThread1{
			Packet<?> pkt;
			boolean success = true;
		}
		AnoThread1 ano = new AnoThread1();
		Thread thread = new Thread(() -> {
			try {
				ano.pkt = readPacket(decrypt,rsa_or_aes,() -> {
					return ano.success;
				});
			} catch (Exception e) {
			}
		});
		thread.start();
		thread.join(timeout);
		if(ano.pkt == null) {
			ano.success = false;
			throw new TimeoutException();
		}
		return ano.pkt;
		
	}
	
	/**
	 * ���һ��������(������)�����ݰ��������������
	 * @param pkt �����͵����ݰ�
	 * @see com.qing_guang.RemoteControl.util.channel.WriteChannel#addPacketWillSend(Packet)
	 */
	public void addPacketWillSend(Packet<?> pkt) {
		
		new Thread(() -> {
			
			PacketSendEvent pse = new PacketSendEvent(pkt, false, false);
			
			Future<Object> future = Bukkit.getScheduler().callSyncMethod(Main.getPlugin(Main.class), () -> {
				Bukkit.getPluginManager().callEvent(pse);
				return 123;
			});
			try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!pse.isCancelled()) {
				if(!pse.isEncrypt()) {
					channel.getWriteChannel().addPacketWillSend(pkt);
				}else {
					channel.getWriteChannel().addPacketWillSend(pkt,pse.encryptAlg());
				}
			}
			
		}).start();
		
	}
	
	/**
	 * ���һ��������(����)�����ݰ��������������
	 * @param pkt �����͵����ݰ�
	 * @param rsa_or_aes ��Ϊtrue��ʹ��rsa�㷨����,����ʹ��aes�㷨����
	 * @see com.qing_guang.RemoteControl.util.channel.WriteChannel#addPacketWillSend(Packet,boolean)
	 */
	public void addPacketWillSend(Packet<?> pkt,boolean rsa_or_aes) {
		
		new Thread(() -> {
			
			PacketSendEvent pse = new PacketSendEvent(pkt, true, rsa_or_aes);
			
			Future<Object> future = Bukkit.getScheduler().callSyncMethod(Main.getPlugin(Main.class), () -> {
				Bukkit.getPluginManager().callEvent(pse);
				return 123;
			});
			
			try {
				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!pse.isCancelled()) {
				if(pse.isEncrypt()) {
					channel.getWriteChannel().addPacketWillSend(pkt,rsa_or_aes);
				}else {
					channel.getWriteChannel().addPacketWillSend(pkt);
				}
			}
			
		}).start();
		
	}
	
	/**
	 * ���ص�ǰ���ͨ���Ƿ���������з�������
	 */
	public boolean noSendTask() {
		WriteChannel wchannel = channel.getWriteChannel();
		return !wchannel.isWriting() && wchannel.getBuffer().insertWhere() == wchannel.getBuffer().size() - 1;
	}
	
	/**
	 * �˿ͻ��˵��û���
	 */
	public String getUname() {
		return uname;
	}
	
	/**
	 * �˿ͻ�������ʹ�õİ汾
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * �����˿ͻ��˵ķ�����
	 */
	public RemoteControlServer getServer() {
		return server;
	}
	
	/**
	 * �Ƿ�������
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	void disconn() throws IOException {
		isRunning = false;
		channel.close();
	}
	
	ConnectChannel getChannel(){
		return channel;
	}
	
	void init(Socket client,AbstractClientExceptionHandler client_write_exc,AbstractClientExceptionHandler client_read_exc,RemoteControlServer server) throws IOException {
		channel = new ConnectChannel(client, client_write_exc, client_read_exc);
		this.client_write_exc = client_write_exc;
		this.client_read_exc = client_read_exc;
		this.server = server;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public RemoteControlClient clone() {
		return new RemoteControlClient();
	}
	
	//ע��
	private void register(String uname) {
		
		this.uname = uname;
		client_write_exc.setClient(client_write_exc.client == null ? this : client_write_exc.client);
		client_read_exc.setClient(client_read_exc.client == null ? this : client_write_exc.client);
		
		addPacketWillSend(new ServerInfoPacket(Main.SERVER_INFO),false);
		
		for(String info : Main.PLUGINS_INFO.keySet()) {
			addPacketWillSend(new PluginInfoPacket(Main.PLUGINS_INFO.get(info)),false);
		}
		
		for(String info : Main.WORLDS_INFO.keySet()) {
			addPacketWillSend(new WorldInfoPacket(Main.WORLDS_INFO.get(info)),false);
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			addPacketWillSend(new OnlineModeChangePacket(Type.PLAYER, true, player.getName()),false);
		}
		
		for(RemoteControlClient client : server.getClients()) {
			addPacketWillSend(new OnlineModeChangePacket(Type.CLIENT, true, client.uname),false);
		}
		
		for(String msg : Recorder.INSTANCE.getAll()) {
			addPacketWillSend(new BackstageInfoPacket(msg),false);
		}
		server.logged.put(uname, this);
		Bukkit.getScheduler().callSyncMethod(JavaPlugin.getPlugin(Main.class), () -> {
			Bukkit.getPluginManager().callEvent(new ClientLoginSuccessEvent(this));
			return null;
		});
		
	}
	
	//����
	private void action() {
		
		Packet<?> pkt = null;
		while(isRunning()) {
			
			try {
				pkt = readPacket(true,false,null);
			} catch (Exception e) {
			}
			if(pkt instanceof CommandLinePacket) {
				CommandLinePacket clp = (CommandLinePacket) pkt;
				Bukkit.getScheduler().callSyncMethod(JavaPlugin.getPlugin(Main.class), () -> {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), clp.getCmdLine());
					return null;
				});
			}else if(pkt instanceof ClientExitPacket) {
				server.disconn(this,DisconnectionCause.CLIENT_EXIT);
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}

	//�жϴ���Ķ����Ƿ�Ϊָ�����ʵ����,����Ƿ��ش˶���(ת��),������Ƿ���null�����׳�UnsupportedClassTypeException�쳣
	@SuppressWarnings("unchecked")
	private static <T> T inst(Object obj,Class<T> clazz,boolean throwexc) throws UnsupportedClassTypeException{
		if(clazz.isInstance(obj)) {
			return (T)obj;
		}
		if(throwexc) {
			throw new UnsupportedClassTypeException();
		}else {
			return null;
		}
	}
	
}

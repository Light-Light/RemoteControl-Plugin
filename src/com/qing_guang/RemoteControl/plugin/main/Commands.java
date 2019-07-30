package com.qing_guang.RemoteControl.plugin.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.qing_guang.RemoteControl.plugin.connect.RemoteControlClient;
import com.qing_guang.RemoteControl.plugin.connect.RemoteControlServer.DisconnectionCause;
import com.qing_guang.RemoteControl.util.CommunicateEncryptUtil;

import net.md_5.bungee.api.ChatColor;

/**
 * ָ�����
 * @author Qing_Guang
 *
 */
public class Commands implements CommandExecutor{

	//rc help
	//rc reload
	//rc remove <uname>
	//rc disconn <uname>
	//rc add <uname> <pwd>
	
	/**
	 * {@inheritDoc}
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		
		if(!sender.hasPermission("rc.use")) {
			sender.sendMessage(ChatColor.RED + "��û��Ȩ��ʹ�ô�ָ��!");
		}
		
		if(args.length == 1) {
			
			if(args[0].equalsIgnoreCase("help")) {
				
				sender.sendMessage(ChatColor.GREEN + "�鿴��ָ��İ�����Ϣ");
				sender.sendMessage(ChatColor.GREEN + "/rc reload ���¼��ر����,��������������������һϵ�в���");
				sender.sendMessage(ChatColor.GREEN + "/rc remove <�û���> ɾ��һ����ע����˺�(���˵�½)");
				sender.sendMessage(ChatColor.GREEN + "/rc disconn <�û���> ǿ������һ�����ڿ��ƵĿͻ���");
				sender.sendMessage(ChatColor.GREEN + "/rc add <�û���> <����> ���һ�����Ե�½���˻�(����û�пͻ��˿��Ƶ�ʱ��ʹ���Ա���ȫ)");
				
			}else if(args[0].equalsIgnoreCase("reload")){
				
				if(!sender.hasPermission("rc.reload")) {
					Main m = JavaPlugin.getPlugin(Main.class);
					Bukkit.getPluginManager().disablePlugin(m);
					Bukkit.getPluginManager().enablePlugin(m);
					sender.sendMessage(ChatColor.GREEN + "��������¼���");
				}else{
					sender.sendMessage(ChatColor.RED + "��û��Ȩ��ʹ�ô�ָ��!");
				}
				
			}else {
				return Bukkit.dispatchCommand(sender, "rc help");
			}
			
		}else if(args.length == 2) {
			
			Account acc = Main.ACCOUNTS.get(args[1]);
			RemoteControlClient client = Main.server.getClient(args[1]);
			
			if(args[0].equalsIgnoreCase("remove")) {
				
				if(sender.hasPermission("rc.acc_ctrl")) {
					if(acc != null && client == null) {
						
						Main.ACCOUNTS.remove(args[1]);
						sender.sendMessage(ChatColor.GREEN + "���˻��ѱ�ע��");
						
					}else if(acc == null) {
						sender.sendMessage(ChatColor.RED + "���˻�û�б�ע��");
					}else {
						sender.sendMessage(ChatColor.RED + "���˻�������");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "��û��Ȩ��ʹ�ô�ָ��!");
				}
				
			}else if(args[0].equalsIgnoreCase("disconn")) {
				
				if(sender.hasPermission("rc.disconn")) {
					if(acc != null && client != null) {
						
						Main.server.disconn(client, DisconnectionCause.MANUAL);
						sender.sendMessage(ChatColor.GREEN + "���˻��ѱ�ǿ������");
						
					}else if(acc == null) {
						sender.sendMessage(ChatColor.RED + "���˻�û�б�ע��");
					}else {
						sender.sendMessage(ChatColor.RED + "���˻���������");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "��û��Ȩ��ʹ�ô�ָ��!");
				}
				
			}else {
				return Bukkit.dispatchCommand(sender, "rc help");
			}
			
		}else if(args.length == 3) {
			
			Account acc = Main.ACCOUNTS.get(args[1]);
//			RemoteControlClient client = Main.server.getClient(args[1]);
			
			if(args[0].equalsIgnoreCase("add")) {
				
				if(sender.hasPermission("rc.acc_ctrl")) {
					if(acc == null) {
						
						Main.ACCOUNTS.put(args[1], new Account(args[1], CommunicateEncryptUtil.getMD5String(args[2])));
						sender.sendMessage(ChatColor.GREEN + "���˻��ѱ�ע��");
						
					}else if(acc != null) {
						sender.sendMessage(ChatColor.RED + "���˻��ѱ�ע��");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "��û��Ȩ��ʹ�ô�ָ��!");
				}
				
			}else {
				return Bukkit.dispatchCommand(sender, "rc help");
			}
			
		}else {
			return Bukkit.dispatchCommand(sender, "rc help");
		}
		
		return true;
		
	}

}

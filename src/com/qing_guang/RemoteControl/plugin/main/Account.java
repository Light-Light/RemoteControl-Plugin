package com.qing_guang.RemoteControl.plugin.main;

/**
 * �˻���
 * @author Qing_Guang
 *
 */
public class Account {

	private String uname;
	private String pwd;
	
	/**
	 * ����һ���˻���
	 * @param uname �û���
	 * @param pwd ����(����md5����)
	 */
	public Account(String uname, String pwd) {
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
	 * ����(����md5����)
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * �ж������˻������Ƿ����
	 * @param obj ��Ҫ�ж���ȵ��˻�����
	 * @return �Ƿ����
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Account obj) {
		return obj.uname.equals(uname) && obj.pwd.equals(pwd);
	}

	/**
	 * @return ת���ַ���֮��ı�������Ϣ
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Account [uname=" + uname + ", pwd=" + pwd + "]";
	}
	
}

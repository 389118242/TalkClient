package com.chatroom;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login extends Thread implements ActionListener {

	private JFrame jf;
	private JTextField serverName;
	private JTextField userName;
	private JButton login;
	private JButton exit;

	@Override
	public void run() {
		jf = new JFrame();
		jf.setBounds(600, 300, 250, 150);
		jf.setLayout(new GridLayout(3, 1));
		JPanel jp1 = new JPanel();
		JLabel jl1 = new JLabel("服务器");
		serverName = new JTextField(9);
		jp1.add(jl1);
		jp1.add(serverName);
		jf.add(jp1);
		JPanel jp2 = new JPanel();
		JLabel jl2 = new JLabel("用户名");
		userName = new JTextField(9);
		jp2.add(jl2);
		jp2.add(userName);
		jf.add(jp2);
		JPanel jp3 = new JPanel();
		login = new JButton("登录");
		exit = new JButton("退出");
		login.addActionListener(this);
		exit.addActionListener(this);
		jp3.add(login);
		jp3.add(exit);
		jf.add(jp3);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		jf.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login) {
			String serverName = this.serverName.getText();
			String userName = this.userName.getText();
			if ("".equals(serverName)) {
				JOptionPane.showMessageDialog(jf, "请输入服务器");
				return;
			} else if ("".equals(userName)) {
				JOptionPane.showMessageDialog(jf, "请输入用户名");
				return;
			}
			try {
				Socket socket = new Socket(serverName, 3927);
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Message mess = new Message();
				mess.setName(userName);
				mess.setType("login");
				oos.writeObject(mess);
				mess = (Message) ois.readObject();
				if ("ok".equals(mess.getContent())) {
					new MyClient(socket,userName, ois, oos).start();
					jf.dispose();
				} else {
					JOptionPane.showMessageDialog(jf, "用户名已存在于聊天室");
				}
			} catch (UnknownHostException e1) {
				JOptionPane.showMessageDialog(jf, "无法连接到" + serverName);
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}else{
			System.exit(0);
		}

	}

	public static void main(String[] args) {
		new Login().start();
	}

}

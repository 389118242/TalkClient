package com.chatroom;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import rung.JTabbedPanelWithClose;

public class MyClient extends Thread implements ActionListener {
	private Socket socket;
	private String name;// �û���
	private ObjectInputStream ois;// ����������
	private ObjectOutputStream oos;// ���������
	private DefaultListModel<String> dlm;
	private JList<String> jlist;
	private JTabbedPanelWithClose jtpwc;
	private JTextField sendMess;
	private Map<Integer, String> tabTitle = new HashMap<>();
	private Map<String, Integer> FtabTitle = new HashMap<>();
	private Map<String, JTextArea> textAreaMap = new HashMap<>();

	public MyClient(Socket socket, String name, ObjectInputStream ois, ObjectOutputStream oos) {
		this.socket = socket;
		this.name = name;
		this.ois = ois;
		this.oos = oos;
	}

	@Override
	public void run() {
		JFrame jf = new JFrame("������---" + name);
		jf.setBounds(270, 87, 900, 600);
		// jf.setDefaultCloseOperation(close());
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					oos.close();
					ois.close();
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		JPanel west = new JPanel();
		west.setLayout(new BorderLayout());
		JLabel westTitle = new JLabel(" �����б� ");
		westTitle.setFont(new Font("����", Font.BOLD, 27));
		dlm = new DefaultListModel<>();
		jlist = new JList<>(dlm);
		jlist.setFont(new Font("����", Font.BOLD, 21));
		west.add(westTitle, BorderLayout.NORTH);
		try {
			Message mess = (Message) ois.readObject();
			mess.getList().remove(name);
			String[] a = new String[mess.getList().size()];
			mess.getList().toArray(a);
			for (String i : a) {
				dlm.addElement(i);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		jtpwc = new JTabbedPanelWithClose();
		jtpwc.setTabTitle(tabTitle);
		jtpwc.setFtabTitle(FtabTitle);
		jtpwc.setTextAreaMap(textAreaMap);
		JTextArea jtaAll = new JTextArea();
		jtaAll.setFont(new Font("����", Font.BOLD, 23));
		jtaAll.setEditable(false);
		jtaAll.setLineWrap(true);// �Զ�����
		jtaAll.setWrapStyleWord(true);// ���з�ʽ
		JScrollPane jsp = new JScrollPane(jtaAll);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// jta.setSelectionEnd(jta.getText().length());// ���ù�����λ��������
		// JScrollBar jsb=jsp.getVerticalScrollBar();
		// jsb.setValue(jsb.getMaximum());
		jtpwc.addTab("ALL", jsp);
		tabTitle.put(jtpwc.getTabCount() - 1, "ALL");
		FtabTitle.put("ALL", jtpwc.getTabCount() - 1);
		textAreaMap.put("ALL", jtaAll);
		jf.getContentPane().add(jtpwc, BorderLayout.CENTER);
		jlist.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String select = jlist.getSelectedValue();
					if (FtabTitle.keySet().contains(select)) {
						jtpwc.setSelectedIndex(FtabTitle.get(select));
					} else {
						addTabToJTPWC(select);
						jtpwc.setSelectedIndex(jtpwc.getTabCount() - 1);
					}
				}
			}
		});
		west.add(jlist, BorderLayout.CENTER);
		jf.getContentPane().add(west, BorderLayout.WEST);

		JPanel foot = new JPanel();
		sendMess = new JTextField(39);
		sendMess.setFont(new Font("����", Font.BOLD, 23));
		JButton sendBut = new JButton("����");
		sendBut.setMargin(new Insets(0, 13, 0, 13));
		sendBut.setFont(new Font("����", Font.BOLD, 19));
		sendMess.addActionListener(this);
		sendBut.addActionListener(this);
		foot.add(sendMess);
		foot.add(sendBut);
		jf.getContentPane().add(foot, BorderLayout.SOUTH);
		jf.setVisible(true);
		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						Message mess = (Message) ois.readObject();
						String fromName = mess.getName();
						String fromText = mess.getContent();
						String fromType = mess.getType();
						JTextArea jta = null;
						if (fromType.startsWith("post")) {
							String gName = fromText.substring(fromText.indexOf("��") + 1, fromText.indexOf("��"));
							if ("postLogin".equals(fromType) && !gName.equals(name)) {
								dlm.addElement(gName);
							} else if ("postLogout".equals(fromType)) {
								dlm.removeElement(gName);
							}
						} else if (!"say".equals(fromType)) {
							if (textAreaMap.keySet().contains(fromName)) {
								jta = textAreaMap.get(fromName);
							} else {
								addTabToJTPWC(fromName);
								jta = textAreaMap.get(fromName);
							}
						}
						if (null == jta) {
							jta = jtaAll;
						}
						if("ERROR".equals(fromType)){
							fromName="ERROR";
						}
						jta.append(fromName + "��" + fromText + "\n");
						jta.setSelectionEnd(jta.getText().length());
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Message mess = new Message();
		mess.setName(name);
		String text = sendMess.getText();
		sendMess.setText("");
		mess.setContent(text);
		int index = jtpwc.getSelectedIndex();
		String title = tabTitle.get(index);
		if ("ALL".equals(title)) {
			mess.setType("say");
		} else {
			mess.setType(title);
		}
		JTextArea jta = textAreaMap.get(title);
		try {
			oos.writeObject(mess);
			jta.append("�ң�" + text + "\n");
			jta.setSelectionEnd(jta.getText().length());
		} catch (IOException e1) {
			jta.append("�ң�" + text + "������ʧ�ܣ�\n");
			e1.printStackTrace();
		}
	}

	public void addTabToJTPWC(String select) {
		JTextArea jta = new JTextArea();
		jta.setFont(new Font("����", Font.BOLD, 23));
		jta.setEditable(false);
		jta.setLineWrap(true);// �Զ�����
		jta.setWrapStyleWord(true);// ���з�ʽ
		JScrollPane jsp = new JScrollPane(jta);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jtpwc.addTabWithClose(select, jsp);
		tabTitle.put(jtpwc.getTabCount() - 1, select);
		FtabTitle.put(select, jtpwc.getTabCount() - 1);
		textAreaMap.put(select, jta);
	}
}

package com.chatroom.client;

import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Client {
	private ClientFrame cf = null;
	private JTextField tf = null;
	private TextArea ta = null;
	private JDialog jd = null;
	private JTextField nameField = null;
	private JTextField addressField = null;
	private JTextField portField = null;
	private String ip = null;
	private int port = 0;
	private String name=null;
	private Socket s = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean connected = false;
	
	/**
	 *界面初始化,并添加事件侦听 
	 */
	public void init() {
		cf = new ClientFrame();
		tf = cf.getTf();
		ta = cf.getTa();
		JLabel label1 = new JLabel("服务器ip地址：");
		addressField = new JTextField();
		addressField.setColumns(7);
		JLabel label2 = new JLabel("用户昵称：");
		nameField = new JTextField();
		nameField.setColumns(5);
		JLabel label3 = new JLabel("端口号：");
		portField = new JTextField();
		portField.setText("8888");
		portField.setColumns(3);
		JButton button = new JButton("确认");
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
					ip=addressField.getText();
					port=Integer.parseInt(portField.getText());
					name = nameField.getText();
					System.out.println(ip+port+name);
					if((ip!=null)&&(port!=0)&&(name!=null)){
						jd.dispose();
						tf.setText("输入内容，按Enter键发送消息");
					}
			}
		});
		
		jd = new JDialog(cf, true);
		jd.setLocation(300, 150);
		jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		jd.setTitle("初始化设置");
		jd.setLayout(new FlowLayout());
		jd.add(label1);
		jd.add(addressField);
		jd.add(label3);
		jd.add(portField);
		jd.add(label2);
		jd.add(nameField);
		jd.add(button);
		jd.pack();
		jd.setVisible(true);
		
		this.connect();
		
		cf.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					dos.writeUTF("CLIENTEXIST");
				} catch (IOException e1) {
					e1.printStackTrace();
				}finally{
					disconnect();
					System.exit(0);
				}
			}
		});
		
		/**
		 * 注册事件，按Enter键发送消息
		 */
		tf.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeUTF(nameField.getText() + ":" + tf.getText());
					dos.flush();
				} catch (IOException e1) {
					disconnect();
				}
				tf.setText("");
			}
		});
	}
	
	/**
	 * 连接服务器
	 */
	public void connect() {
		try {
			s = new Socket(ip, port);
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			connected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 断开连接
	 */
	public void disconnect() {
		connected = false;
		try {
			if (dos != null) {
				dos.close();
			}
			if (dis != null) {
				dis.close();
			}
			if (s != null) {
				s.close();
				s=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 客户端开始接收消息
	 */
	public void start() {
		while (connected) {
			try {
				String receive = dis.readUTF();
				ta.append(receive+"\n");
			} catch (IOException e) {
				disconnect();
			}
		}
	}
	/**
	 * 客户端程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		client.init();
		client.start();
	}
}

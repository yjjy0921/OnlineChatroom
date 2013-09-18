package com.chatroom.server;

import java.awt.TextArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket ss = null;
	private Socket s = null;
	private TextArea text = null;
	private static ExecutorService exeSer = null;
	private String input;
	private boolean bRun = false;
	private static List<ServerProxy> proxys = new ArrayList<ServerProxy>();

	/**
	 * 静态代码块,初始化线程池
	 */
	static {
		exeSer = Executors.newFixedThreadPool(20);
	}

	/**
	 * 构造函数，创建frame,并且得到该frame中文本域的引用
	 * 
	 */
	public Server() {
		ServerFrame serverFrame = new ServerFrame();
		text = serverFrame.getText();
	}

	/**
	 * Server接收client端的socket并且分发给代理类（ServerProxy）来处理通信过程
	 */
	public void start() {
		try {
			FileInputStream fis = new FileInputStream(new File(
					"./src/com/chatroom/server/Server.properties"));
			Properties pro = new Properties();
			pro.load(fis);
			ss = new ServerSocket(Integer.parseInt(pro.getProperty("port")));
			bRun = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (bRun) {
			try {
				s = ss.accept();
				// 将每个client端发来的socket封装成ServerProxy类
				ServerProxy proxy = new ServerProxy(s);
				s = null;
				// 线程同步加锁
				synchronized (proxys) {
					proxys.add(proxy);
				}
				// 主进程执行到这里需要分配一个辅线程来执行ServerProxy里面的业务
				exeSer.submit(proxy);
				System.out.println("A client connected");
			} catch (IOException e) {
				bRun = false;
				e.printStackTrace();
			}
		}
	}

	/**
	 * Server 的代理类，持有ServerSocket接收的socket，与Client进行通信
	 * 
	 */
	public class ServerProxy implements Runnable {
		boolean flag = false;
		Socket socket = null;
		DataInputStream dis = null;
		DataOutputStream dos = null;

		public ServerProxy(Socket s) {
			this.socket = s;
			try {
				dis = new DataInputStream(socket.getInputStream());
				flag = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Socket getSocket() {
			return socket;
		}
		
		/**
		 * 该方法将消息转发给所有代理对象
		 */
		public void dispatch(){
			synchronized (proxys) {
				for (int i = 0; i < proxys.size(); i++) {
					try {
						dos = new DataOutputStream(proxys.get(i)
								.getSocket().getOutputStream());
						dos.writeUTF(input);
						dos.flush();
					} catch (Exception e) {
						try {
							dos.close();
							dis.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						e.printStackTrace();
					}
				}
			}
		}
		
		/**
		 * 接收消息，判断是否为退出消息，若不是，将该消息转发，否则该代理对象将被移除并结束此线程
		 */
		@Override
		public void run() {
			while (flag) {
				try {
					input = dis.readUTF();// 这是一个阻塞式的方法，在这里中断任意一个socket都会出异常
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (!input.equals("CLIENTEXIST")) {
					text.append(input + "\n");
					this.dispatch();
				} else {
					synchronized (proxys) {
						proxys.remove(this);
					}
					flag = false;
				}
			}
		}
	}
	/**
	 * 服务器端程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		new Server().start();
	}
}

package com.chatroom.server;

import java.awt.BorderLayout;
import java.awt.TextArea;

import javax.swing.JFrame;
/**
 * 
 * 服务器界面
 *
 */
public class ServerFrame extends JFrame {
	private TextArea text = null;

	public TextArea getText() {
		return text;
	}

	public ServerFrame() {
		init();
	}
	/**
	 * 初始化界面
	 */
	public void init(){
		this.setTitle("server");
		this.setLocation(200, 200);
		this.setSize(250, 250);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		text = new TextArea();
		text.append("server is built\n");
		this.setLayout(new BorderLayout());
		this.add(text, BorderLayout.CENTER);
		this.setVisible(true);
	}
}

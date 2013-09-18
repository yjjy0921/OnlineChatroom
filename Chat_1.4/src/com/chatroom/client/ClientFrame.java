package com.chatroom.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * 客户端图形界面类
 *
 */
public class ClientFrame extends JFrame {
	private TextArea ta = null;
	private JTextField tf = null;

	public ClientFrame() {
		init();
		regActions();
	}
	/**
	 * 初始化界面
	 */
	public void init(){
		this.setSize(300, 400);
		this.setTitle("ChatRoom Client");
		this.setLocation(280, 100);
		this.setLayout(new BorderLayout());
		ta = new TextArea();
		//ta.setSize(300, 400);
		ta.setColumns(50);
		ta.setRows(20);
		ta.setEditable(false);
		ta.setBackground(new Color(222,230,230));
		this.add(ta,BorderLayout.NORTH);
		tf = new JTextField();
		tf.setColumns(50);
		this.add(tf,BorderLayout.SOUTH);
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
	}
	/**
	 * 为tf注册事件侦听，实现光标移入清空文本框内容
	 */
	public void regActions(){
		tf.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				tf.setText("");
			}
		});
	}
	
	public TextArea getTa() {
		return ta;
	}

	public JTextField getTf() {
		return tf;
	}

	public void setTa(TextArea ta) {
		this.ta = ta;
	}

	public void setTf(JTextField tf) {
		this.tf = tf;
	}

}

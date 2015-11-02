package ChatRoom.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;


import ChatRoom.configs.ServerConfig;
import ChatRoom.configs.UIconfig;
/**
 * �û���������Լ�����socket�����������
 * @author ���
 *
 */
public class Login extends JFrame implements ActionListener{
	private JTextField nameText;
	private JLabel nameLabel;
	private JButton loginButton,cancelButton;
	private String name;
	private Container container;
	private JLabel hostLabel;
	private JTextField hostText;
	private String hostName = "localhost";
	
	BufferedReader in;
	PrintWriter out;
	
	public Login()
	{
		super(UIconfig.LOGIN_TITLE);
		hostLabel = new JLabel("��ַ: ");
		hostText = new JTextField(10);
		hostText.setText(hostName); // ����Ĭ��ֵ
		nameText = new JTextField(UIconfig.NAME_TEXT_LIMIT);
		nameLabel = new JLabel(UIconfig.NAME_LABEL);
		loginButton = new JButton("����");
		cancelButton = new JButton("ȡ��");
		container = this.getContentPane();
		container.setLayout(new BorderLayout());
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(loginButton);
		buttons.add(cancelButton);
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout());
		JPanel hostPanel = new JPanel();
		hostPanel.setLayout(new FlowLayout());
		namePanel.add(nameLabel);
		namePanel.add(nameText);
		hostPanel.add(hostLabel);
		hostPanel.add(hostText);
		container.add("North",namePanel);
		container.add("Center",hostPanel);
		container.add("South", buttons);
		
		loginButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		setSize(200,100);
		setVisible(true);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == loginButton)
		{
			name = nameText.getText();
			if(name != null && !name.equals(""))
			{
				try{
					link();
					MultiChatRoom room = new MultiChatRoom(name);
					room.initControl(in, out);
					loginButton.setEnabled(false);
					this.dispose();
				} catch (Exception ee){
					JOptionPane.showMessageDialog(this, "��½ʧ��", "ʧ��",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(this, "������һ������", "��ʾ",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		else if(e.getSource() == cancelButton)
			System.exit(0);
	}
	
	private void link()
	{
		// ���ӷ�����

		Socket client = null;
		try {
			client = new Socket(hostText.getText(), ServerConfig.DEFAULT_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("2");
		}// ����һ�����׽��ֲ��������ӵ�ָ�� IP ��ַ��ָ���˿ںš�

		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.println(nameText.getText() + "&" + (int) Math.random());
		out.flush();
	}
	
	public static void main(String arg[])
	{
		Login l = new Login();
		l.setLocationRelativeTo(null);
	}

}

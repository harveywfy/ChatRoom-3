package ChatRoom.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import ChatRoom.configs.ClientConfig;
import ChatRoom.configs.UIconfig;
import ChatRoom.threads.AcceptFileThread;
import ChatRoom.threads.ClientThread;
import ChatRoom.threads.SendFileThread;
import ChatRoom.ui.MultiChatRoom;

/**
 * 客户端主类，拥有向服务器发送消息，请求刷新列表、发送文件、拒接接收文件、取消发送等方法
 * @author 竺子崴
 *
 */
public class ClientControl {
	private SendFileThread sendthread;// 发送文件线程;
	private AcceptFileThread acceptthread;// 接收文件线程
	private String IP;// 保存本机IP= InetAddress.getLocalHost();//
	private boolean privateTalkFlag = false; // 是否是私聊,默认值为假

	public BufferedReader in;
	public PrintWriter out;
	public String myName;
	private String withWho = "所有人";
	private String outmsg;// 发送的信息
	private String mywords;// 要说的话
	
	private MultiChatRoom room;
	private ClientThread clientThread; 
	
	public ClientControl(String host,MultiChatRoom r)
	{
		room = r;
		myName = host;

		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			IP = addr.getHostAddress().toString();// 获得本机IP

		} catch (Exception e)
		{
			System.out.print("无法获取本地主机");

		}// System.out.println("本机的ip=" + inet.getHostAddress());

	}

	public void initRW(BufferedReader in, PrintWriter out)
	{
		// 获得输入、输出流
		this.in = in;
		this.out = out;
		// 获得我自己的名字
		clientThread = new ClientThread(room,in,myName,this);
		clientThread.start();
	}

	/**
	 * 发送文件
	 * @param fileName
	 */
	public void sendFile(String fileName)
	{
		if (fileName.length() == 0)// 没有获得需要发送的文件
		{
			room.insert("没有文件发送");
		} else
		{ // 发送文件
			sendthread = new SendFileThread(fileName,room,in,out,IP);
			room.setButtonState(UIconfig.SET_SEND_FILE_BU, false);
			room.setButtonState(UIconfig.SET_REFUSE_SEND_FILE_BU, true);
			sendthread.start();

		}
	}
	
	/**
	 * 取消发送文件
	 */
	public void cancelSendFile()
	{
		room.insert("你取消了文件发送");
		out.println("cancelsendfile" + "&" + withWho);
		out.flush();
		room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);
		room.setButtonState(UIconfig.SET_REFUSE_SEND_FILE_BU, false);
	}
	
	/**
	 * 接受文件
	 * @param filePath
	 * @param filemsg
	 */
	public void acceptFile(String filePath,String filemsg)
	{
		String[] acpfile = filemsg.split("&");
		acceptthread = new AcceptFileThread(filemsg,room);
		acceptthread.ipport(acpfile[3], (Integer.parseInt(acpfile[4])),filePath);
		
		acceptthread.start();
	}
	
	/**
	 * 拒接发送文件
	 * @param filemsg
	 */
	public void refuseFile(String filemsg)
	{
		String[] acpfile = filemsg.split("&");
  	  	out.println("refusefile" + "&" + acpfile[1]);
  	  	out.flush();

  	  	room.insert("你取消了 " + acpfile[1] + " 发来的文件");
  	  	
  	  	
	}
	
	/**
	 * 获得本机IP
	 * @return
	 */
	public static String getIP()
	{
		String IP = null;
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			IP = addr.getHostAddress().toString();// 获得本机IP

		} catch (Exception e)
		{
			System.out.print("无法获取本地主机");
		}
		return IP;
	}
	
	/**
	 * 关闭客服端
	 * @param n
	 */
	public void shutDown(String n)
	{
		try
		{
			out.println("quit");
			out.flush();
		} catch (Exception ee)
		{
			// JOptionPane.showMessageDialog(this, ee, "错误",
			// JOptionPane.ERROR_MESSAGE);
		} finally
		{
			// this.dispose();
			System.exit(0);
		}
	}
	
	/**
	 * 刷新列表
	 */
	public void refreshList()
	{
		try
		{
			out.println("refurbish"); // 发送刷新请求到服务器
			out.flush();
		} catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMsg(String msg)
	{
		try
		{
			mywords = msg;
			if ((mywords.trim()).length() != 0)
			{ // 不能发送空消息也不能都发空格
				if (withWho.equals("所有人"))
				{
					outmsg = mywords;
					// 发送到服务器
					out.println(outmsg);
					out.flush();
					// 显示到我的频道里面
					//room.insert(myName + "：" + mywords);
					// myMsgArea.append(myName+"："+mywords+"\n");
					//myMsgArea.setCaretPosition(myMsgArea.getText().length(
					// ));
				} else
				{ // 对某个人交谈
					outmsg = "withWho" + "&" + "privateFalse" + "&"
							+ withWho + "&" + mywords;
					if (privateTalkFlag)
					{
						outmsg = "withWho" + "&" + "privateTure" + "&"
								+ withWho + "&" + mywords;
						room.insert("您对『" + withWho + "』说: "
								+ mywords);
						//myMsgArea.append("您对『"+withWho+"』说: "+mywords+"\n"
						// );
						// myMsgArea.setCaretPosition(myMsgArea.getText().
						// length());
					} else
					{
						room.insert(myName + " 对『" + withWho + "』说: "
								+ mywords);
						// myMsgArea.append(myName+" 对『"+withWho+"』说: "+
						// mywords+"\n");
					}
					//myMsgArea.setCaretPosition(myMsgArea.getText().length(
					// ));
					// 发送到服务器
					out.println(outmsg);
					out.flush();
				}
			}
		} catch (Exception ee)
		{
			System.out.println(ee);
			// myMsgArea.append("与服务器连接中断,请重新登录！\n");
			room.insert("与服务器连接中断,请重新登录！");

			// myMsgArea.setCaretPosition(myMsgArea.getText().length());
		} finally
		{
			room.cleanInMsgWindow();// 清空输入框
		}
	}

}

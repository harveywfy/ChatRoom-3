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
 * �ͻ������࣬ӵ���������������Ϣ������ˢ���б������ļ����ܽӽ����ļ���ȡ�����͵ȷ���
 * @author ������
 *
 */
public class ClientControl {
	private SendFileThread sendthread;// �����ļ��߳�;
	private AcceptFileThread acceptthread;// �����ļ��߳�
	private String IP;// ���汾��IP= InetAddress.getLocalHost();//
	private boolean privateTalkFlag = false; // �Ƿ���˽��,Ĭ��ֵΪ��

	public BufferedReader in;
	public PrintWriter out;
	public String myName;
	private String withWho = "������";
	private String outmsg;// ���͵���Ϣ
	private String mywords;// Ҫ˵�Ļ�
	
	private MultiChatRoom room;
	private ClientThread clientThread; 
	
	public ClientControl(String host,MultiChatRoom r)
	{
		room = r;
		myName = host;

		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			IP = addr.getHostAddress().toString();// ��ñ���IP

		} catch (Exception e)
		{
			System.out.print("�޷���ȡ��������");

		}// System.out.println("������ip=" + inet.getHostAddress());

	}

	public void initRW(BufferedReader in, PrintWriter out)
	{
		// ������롢�����
		this.in = in;
		this.out = out;
		// ������Լ�������
		clientThread = new ClientThread(room,in,myName,this);
		clientThread.start();
	}

	/**
	 * �����ļ�
	 * @param fileName
	 */
	public void sendFile(String fileName)
	{
		if (fileName.length() == 0)// û�л����Ҫ���͵��ļ�
		{
			room.insert("û���ļ�����");
		} else
		{ // �����ļ�
			sendthread = new SendFileThread(fileName,room,in,out,IP);
			room.setButtonState(UIconfig.SET_SEND_FILE_BU, false);
			room.setButtonState(UIconfig.SET_REFUSE_SEND_FILE_BU, true);
			sendthread.start();

		}
	}
	
	/**
	 * ȡ�������ļ�
	 */
	public void cancelSendFile()
	{
		room.insert("��ȡ�����ļ�����");
		out.println("cancelsendfile" + "&" + withWho);
		out.flush();
		room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);
		room.setButtonState(UIconfig.SET_REFUSE_SEND_FILE_BU, false);
	}
	
	/**
	 * �����ļ�
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
	 * �ܽӷ����ļ�
	 * @param filemsg
	 */
	public void refuseFile(String filemsg)
	{
		String[] acpfile = filemsg.split("&");
  	  	out.println("refusefile" + "&" + acpfile[1]);
  	  	out.flush();

  	  	room.insert("��ȡ���� " + acpfile[1] + " �������ļ�");
  	  	
  	  	
	}
	
	/**
	 * ��ñ���IP
	 * @return
	 */
	public static String getIP()
	{
		String IP = null;
		try
		{
			InetAddress addr = InetAddress.getLocalHost();
			IP = addr.getHostAddress().toString();// ��ñ���IP

		} catch (Exception e)
		{
			System.out.print("�޷���ȡ��������");
		}
		return IP;
	}
	
	/**
	 * �رտͷ���
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
			// JOptionPane.showMessageDialog(this, ee, "����",
			// JOptionPane.ERROR_MESSAGE);
		} finally
		{
			// this.dispose();
			System.exit(0);
		}
	}
	
	/**
	 * ˢ���б�
	 */
	public void refreshList()
	{
		try
		{
			out.println("refurbish"); // ����ˢ�����󵽷�����
			out.flush();
		} catch (Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	/**
	 * ������Ϣ
	 * @param msg
	 */
	public void sendMsg(String msg)
	{
		try
		{
			mywords = msg;
			if ((mywords.trim()).length() != 0)
			{ // ���ܷ��Ϳ���ϢҲ���ܶ����ո�
				if (withWho.equals("������"))
				{
					outmsg = mywords;
					// ���͵�������
					out.println(outmsg);
					out.flush();
					// ��ʾ���ҵ�Ƶ������
					//room.insert(myName + "��" + mywords);
					// myMsgArea.append(myName+"��"+mywords+"\n");
					//myMsgArea.setCaretPosition(myMsgArea.getText().length(
					// ));
				} else
				{ // ��ĳ���˽�̸
					outmsg = "withWho" + "&" + "privateFalse" + "&"
							+ withWho + "&" + mywords;
					if (privateTalkFlag)
					{
						outmsg = "withWho" + "&" + "privateTure" + "&"
								+ withWho + "&" + mywords;
						room.insert("���ԡ�" + withWho + "��˵: "
								+ mywords);
						//myMsgArea.append("���ԡ�"+withWho+"��˵: "+mywords+"\n"
						// );
						// myMsgArea.setCaretPosition(myMsgArea.getText().
						// length());
					} else
					{
						room.insert(myName + " �ԡ�" + withWho + "��˵: "
								+ mywords);
						// myMsgArea.append(myName+" �ԡ�"+withWho+"��˵: "+
						// mywords+"\n");
					}
					//myMsgArea.setCaretPosition(myMsgArea.getText().length(
					// ));
					// ���͵�������
					out.println(outmsg);
					out.flush();
				}
			}
		} catch (Exception ee)
		{
			System.out.println(ee);
			// myMsgArea.append("������������ж�,�����µ�¼��\n");
			room.insert("������������ж�,�����µ�¼��");

			// myMsgArea.setCaretPosition(myMsgArea.getText().length());
		} finally
		{
			room.cleanInMsgWindow();// ��������
		}
	}

}

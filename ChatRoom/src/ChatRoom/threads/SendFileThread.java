package ChatRoom.threads;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;

import ChatRoom.configs.ClientConfig;
import ChatRoom.configs.ServerConfig;
import ChatRoom.configs.UIconfig;
import ChatRoom.ui.MultiChatRoom;

/**
 * �����ļ��߳�
 * @author ������
 *
 */
public class SendFileThread extends Thread// ActionListenerkhhkhѹ��
{

	Socket sendfilesocket; // �����ļ���������
	ServerSocket fileserver; // �����ļ�������
	String filemsg = null; // �����ļ�������

	Boolean sendboolean = true;
	
	private String fileName;
	public BufferedReader in;
	public PrintWriter out;
	public String myName;
	private String withWho = "������";

	private String IP;// ���汾��IP= InetAddress.getLocalHost();//
	private int port;
	private MultiChatRoom room;
	
	public SendFileThread(String file,MultiChatRoom r,BufferedReader i,PrintWriter o,String ip)// ���캯��
	{
		IP = ip;
		in = i;
		out = o;
		fileName = file;
		//System.out.println("fileName:"+file);
		room = r;
	}

	public void run()
	{
		File file = new File(fileName);
		FileInputStream fos = null;

		try
		{
			fos = new FileInputStream(file);
		} catch (IOException e1)
		{
			System.out.print("�����ļ����ļ��쳣");
		}
		// fos = new FileInputStream(file);

		out.println("sendfile" + "&" + withWho + "&"
				+ fileName + "&" + IP + "&" + ClientConfig.PORT + "&"
				+ (int) file.length() / 1000);
		out.flush();

		// ����������������ܿͻ�����
		try
		{
			fileserver = new ServerSocket(ClientConfig.PORT);
		} catch (IOException e1)
		{
			System.out.print("�����ļ����������������쳣");
		}

		// fileserver = new ServerSocket(port);
		// sendfilesocket = fileserver.accept();
		try
		{
			sendfilesocket = fileserver.accept();
		} catch (IOException e1)
		{
			System.out.print("�����ļ����������쳣");
		}

		// ����������������ṩ���ݰ�װ��
		int filetemp = 0;

		OutputStream netOut = null;
		OutputStream doc = null;
		try
		{
			netOut = sendfilesocket.getOutputStream();
			doc = new DataOutputStream(new BufferedOutputStream(netOut));
		} catch (IOException e1)
		{
			System.out.print("�����ļ�����������������ṩ���ݰ�װ���쳣");
		}

		// �����ļ���ȡ������

		byte[] buf = new byte[8000000];
		int num = -1;
		try
		{
			num = fos.read(buf);// ���ļ�
		} catch (IOException e1)
		{
			System.out.print("�����ļ����ļ��쳣");
		}

		// int num = fos.read(buf);// ���ļ�

		while (num != (-1) && sendboolean)
		{// �Ƿ�����ļ�
			filetemp = filetemp + num / 1000;


			// doc.write(buf, 0, num);// ���ļ�����д�����绺����
			// doc.flush();// ˢ�»�����������д���ͻ���
			try
			{
				doc.write(buf, 0, num);// ���ļ�
				doc.flush();
			} catch (IOException e1)
			{
				System.out.print("�����ļ����ļ�����д�����绺�����쳣");
			}

			try
			{
				num = fos.read(buf);// �������ļ��ж�ȡ����
			} catch (IOException e1)
			{
				System.out.print("�����ļ��������ļ��ж�ȡ�����쳣");
			}
		}
		if (num == (-1) && sendboolean)
		{
			room.insert( "�ļ��������");
		} else
		{
			room.insert( "�ļ������ж�");
		}


		try
		{
			fos.close();
			doc.close();
		} catch (IOException e1)
		{
			System.out.print("�����ļ��رն���д�쳣");
		}

		try
		{
			sendfilesocket.close();
			fileserver.close();
		} catch (IOException e1)
		{
			System.out.print("�����ļ��ر����ӻ�������쳣");
		}
		//sendfileArea.setText("");
		//sendfile.setEnabled(true);
		room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);

		return;

	}
}

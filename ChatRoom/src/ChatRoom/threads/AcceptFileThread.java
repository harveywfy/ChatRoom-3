package ChatRoom.threads;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import ChatRoom.ui.MultiChatRoom;

/**
 * �����ļ��߳�
 * @author ������
 *
 */
public class AcceptFileThread extends Thread
{
	private String ip, filepath;
	int port;
	Socket acceptfilesocket; // �����ļ���������

	ServerSocket fileserver; // �����ļ�������
	String filemsg = null; // �����ļ�������

	Boolean acceptboolean = true;
	Boolean sendboolean = true;
	
	private String fileName;
	public BufferedReader in;
	public PrintWriter out;
	public String myName;
	
	private MultiChatRoom room;

	public void ipport(String ipp, int portt, String filepathh)
	{
		ip = ipp;
		port = portt;
		filepath = filepathh;
	}

	public AcceptFileThread(String filemsg,MultiChatRoom r)
	{
		this.filemsg = filemsg;
		room = r;
	}

	public void run()
	{
		File file = new File(filepath);// �ɼӵ�����

		RandomAccessFile raf = null;
		try
		{
			file.createNewFile();
			raf = new RandomAccessFile(file, "rw");
		} catch (IOException e1)
		{
			System.out.print("�����ļ��½��ļ������쳣");
		}

		// ͨ��Socket�����ļ�������

		try
		{
			acceptfilesocket = new Socket(ip, port);
		} catch (IOException e1)
		{
			System.out.print("�����ļ�ͨ��Socket�����ļ��������쳣");
		}

		String[] tem = filemsg.split("&");

		// ����������������ܷ������ļ�����

		InputStream netIn = null;
		InputStream in = null;

		try
		{
			netIn = acceptfilesocket.getInputStream();

			in = new DataInputStream(new BufferedInputStream(netIn));
		} catch (IOException e1)
		{
			System.out.print("�����ļ�����������������ܷ������ļ������쳣");
		}

		// ����������������������

		byte[] buf = new byte[8000000];

		int num = -1;

		try
		{
			num = in.read(buf);
		} catch (IOException e1)
		{
			System.out.print("�����ļ������������������������쳣");
		}

		int temleng = num / 1000;

		while (num != (-1) && acceptboolean)
		{// �Ƿ������������

			temleng = temleng + num / 1000;

			try
			{
				raf.write(buf, 0, num);// ������д���ļ�
				raf.skipBytes(num);// ˳��д�ļ��ֽ�
				num = in.read(buf);// �����������ж�ȡ�ļ�
			} catch (IOException e1)
			{
				System.out.print("�����ļ�������д���ļ�������������ж�ȡ�ļ��쳣");
			}
		}

		if (acceptboolean)
		{
			room.insert("�ļ��������");
		} else
		{
			room.insert("�ļ������ж�");
		}

		try
		{
			in.close();
			raf.close();
		} catch (IOException e1)
		{
			System.out.print("�����ļ��رն�дʧ���쳣");
		}

		try
		{
			acceptfilesocket.close();
		} catch (IOException e1)
		{
			System.out.print("�����ļ��ر������쳣");
		}

		//acceptfile.setEnabled(true);

		return;

	}
}

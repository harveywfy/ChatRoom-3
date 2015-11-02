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
 * 接收文件线程
 * @author 竺子崴
 *
 */
public class AcceptFileThread extends Thread
{
	private String ip, filepath;
	int port;
	Socket acceptfilesocket; // 接收文件传输连接

	ServerSocket fileserver; // 传输文件服务器
	String filemsg = null; // 保存文件输入流

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
		File file = new File(filepath);// 可加弹出框

		RandomAccessFile raf = null;
		try
		{
			file.createNewFile();
			raf = new RandomAccessFile(file, "rw");
		} catch (IOException e1)
		{
			System.out.print("接收文件新建文件并打开异常");
		}

		// 通过Socket连接文件服务器

		try
		{
			acceptfilesocket = new Socket(ip, port);
		} catch (IOException e1)
		{
			System.out.print("接收文件通过Socket连接文件服务器异常");
		}

		String[] tem = filemsg.split("&");

		// 创建网络接受流接受服务器文件数据

		InputStream netIn = null;
		InputStream in = null;

		try
		{
			netIn = acceptfilesocket.getInputStream();

			in = new DataInputStream(new BufferedInputStream(netIn));
		} catch (IOException e1)
		{
			System.out.print("接收文件创建网络接受流接受服务器文件数据异常");
		}

		// 创建缓冲区缓冲网络数据

		byte[] buf = new byte[8000000];

		int num = -1;

		try
		{
			num = in.read(buf);
		} catch (IOException e1)
		{
			System.out.print("接收文件创建缓冲区缓冲网络数据异常");
		}

		int temleng = num / 1000;

		while (num != (-1) && acceptboolean)
		{// 是否读完所有数据

			temleng = temleng + num / 1000;

			try
			{
				raf.write(buf, 0, num);// 将数据写往文件
				raf.skipBytes(num);// 顺序写文件字节
				num = in.read(buf);// 继续从网络中读取文件
			} catch (IOException e1)
			{
				System.out.print("接收文件将数据写往文件或继续从网络中读取文件异常");
			}
		}

		if (acceptboolean)
		{
			room.insert("文件接收完毕");
		} else
		{
			room.insert("文件接收中断");
		}

		try
		{
			in.close();
			raf.close();
		} catch (IOException e1)
		{
			System.out.print("接收文件关闭读写失败异常");
		}

		try
		{
			acceptfilesocket.close();
		} catch (IOException e1)
		{
			System.out.print("接收文件关闭连接异常");
		}

		//acceptfile.setEnabled(true);

		return;

	}
}

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
 * 发送文件线程
 * @author 竺子崴
 *
 */
public class SendFileThread extends Thread// ActionListenerkhhkh压郁
{

	Socket sendfilesocket; // 发送文件传输连接
	ServerSocket fileserver; // 传输文件服务器
	String filemsg = null; // 保存文件输入流

	Boolean sendboolean = true;
	
	private String fileName;
	public BufferedReader in;
	public PrintWriter out;
	public String myName;
	private String withWho = "所有人";

	private String IP;// 保存本机IP= InetAddress.getLocalHost();//
	private int port;
	private MultiChatRoom room;
	
	public SendFileThread(String file,MultiChatRoom r,BufferedReader i,PrintWriter o,String ip)// 构造函数
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
			System.out.print("发送文件打开文件异常");
		}
		// fos = new FileInputStream(file);

		out.println("sendfile" + "&" + withWho + "&"
				+ fileName + "&" + IP + "&" + ClientConfig.PORT + "&"
				+ (int) file.length() / 1000);
		out.flush();

		// 创建网络服务器接受客户请求
		try
		{
			fileserver = new ServerSocket(ClientConfig.PORT);
		} catch (IOException e1)
		{
			System.out.print("发送文件创建服务器错误异常");
		}

		// fileserver = new ServerSocket(port);
		// sendfilesocket = fileserver.accept();
		try
		{
			sendfilesocket = fileserver.accept();
		} catch (IOException e1)
		{
			System.out.print("发送文件监听连接异常");
		}

		// 创建网络输出流并提供数据包装器
		int filetemp = 0;

		OutputStream netOut = null;
		OutputStream doc = null;
		try
		{
			netOut = sendfilesocket.getOutputStream();
			doc = new DataOutputStream(new BufferedOutputStream(netOut));
		} catch (IOException e1)
		{
			System.out.print("发送文件创建网络输出流并提供数据包装器异常");
		}

		// 创建文件读取缓冲区

		byte[] buf = new byte[8000000];
		int num = -1;
		try
		{
			num = fos.read(buf);// 读文件
		} catch (IOException e1)
		{
			System.out.print("发送文件读文件异常");
		}

		// int num = fos.read(buf);// 读文件

		while (num != (-1) && sendboolean)
		{// 是否读完文件
			filetemp = filetemp + num / 1000;


			// doc.write(buf, 0, num);// 把文件数据写出网络缓冲区
			// doc.flush();// 刷新缓冲区把数据写往客户端
			try
			{
				doc.write(buf, 0, num);// 读文件
				doc.flush();
			} catch (IOException e1)
			{
				System.out.print("发送文件把文件数据写出网络缓冲区异常");
			}

			try
			{
				num = fos.read(buf);// 继续从文件中读取数据
			} catch (IOException e1)
			{
				System.out.print("发送文件继续从文件中读取数据异常");
			}
		}
		if (num == (-1) && sendboolean)
		{
			room.insert( "文件发送完毕");
		} else
		{
			room.insert( "文件发送中断");
		}


		try
		{
			fos.close();
			doc.close();
		} catch (IOException e1)
		{
			System.out.print("发送文件关闭读或写异常");
		}

		try
		{
			sendfilesocket.close();
			fileserver.close();
		} catch (IOException e1)
		{
			System.out.print("发送文件关闭连接或服务器异常");
		}
		//sendfileArea.setText("");
		//sendfile.setEnabled(true);
		room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);

		return;

	}
}

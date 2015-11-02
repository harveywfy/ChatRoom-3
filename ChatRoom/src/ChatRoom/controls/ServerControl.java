package ChatRoom.controls;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;

import ChatRoom.configs.ServerConfig;
import ChatRoom.threads.ServerThread;
import ChatRoom.ui.ServerWindow;

/**
 * 服务器的主类，拥有向客户端发送消息、文件、刷新列表请求、添加链接等方法
 * @author 竺子崴
 *
 */
public class ServerControl
{
	static ServerSocket serverSocket;
	static Vector<Socket> connections;// 连接
	static Vector<ServerThread> clients;
	private static int num = -1;
	private static ServerWindow window;

	/**
	 * 
	 * 发送信息给所有的人
	 */
	public static void sendAll(String s)
	{
		if (connections != null)
		{
			for (Enumeration e = connections.elements(); // Enumeration过时的接口，
															// 可以用for each循环
			e.hasMoreElements();)
			{
				try
				{
					PrintWriter pw = new PrintWriter(((Socket) e.nextElement())
							.getOutputStream());
					pw.println(s);
					pw.flush();
				} catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		window.insert(s);
	}

	/**
	 * 
	 * 发送信息给单独一个人
	 */
	public static boolean sendOne(String name, String msg)
	{
		if (clients != null)
		{
			for (Enumeration e = clients.elements(); e.hasMoreElements();)
			{
				ServerThread cp = (ServerThread) e.nextElement();
				if ((cp.getName()).equals(name))
				{
					try
					{
						PrintWriter pw = new PrintWriter((cp.getSocket()).getOutputStream());
						pw.println(msg);
						pw.flush();
						return true; // 返回值为真，找到了这个人可以进行聊天
					} catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
		}
		return false;// 没有找到这个人，应该是此人已经退出了聊天室
	}

	public static void addConnection(Socket s, ServerThread cp)
	{
		if (connections == null)
		{
			connections = new Vector<Socket>();
		}
		connections.addElement(s);

		if (clients == null)
		{
			clients = new Vector<ServerThread>();
		}
		clients.addElement(cp);
	}

	public static void deleteConnection(Socket s, ServerThread cp)//删除连接
			throws IOException
	{
		if (connections != null)
		{
			connections.removeElement(s);
			s.close();
		}
		if (clients != null)
		{
			clients.removeElement(cp);
		}
	}

	public static Vector getClients()
	{
		return clients;
	}

	/**
	 * 
	 * 服务器端在此启动
	 */
	public ServerControl()
	{
		//InetAddress i = new InetAddress();
		
		
		int port = ServerConfig.DEFAULT_PORT;
		try
		{
			serverSocket = new ServerSocket(port);
			InetAddress addr = InetAddress.getLocalHost();
			
			//serverSocket.bind(new InetSocketAddress("192.168.0.100", 6022));
		
			window = new ServerWindow(addr.getHostAddress().toString());
			window.insert("服务器已经启动，正在监听...");
		} catch (IOException e)
		{
			System.out.println("异常");
			System.err.println(e);
			System.exit(1);

		}

		while (true)
		{ // 死循环
			try
			{
				Socket cs = serverSocket.accept();
				ServerThread cp = new ServerThread(cs,window); // 启动一个用户线程
				System.out.println(serverSocket.getInetAddress().getHostName());
				Thread ct = new Thread(cp);
				//Thread ar = new Thread(ct.)
				ct.start();

				addConnection(cs, cp);
				
				if(num == -1)
					num = 1;
				else
					num++;
			} catch (IOException e)
			{
				System.err.println(e);
			}
		}
	}
	
	public static void killOneThread()
	{
		num--;
		if(num <= 0)
		{
			System.out.println("用户全都离开，服务器关闭......");
			System.exit(0);
		}
	}
	
	public static void main(String arg[])
	{
		new ServerControl();
	}
}

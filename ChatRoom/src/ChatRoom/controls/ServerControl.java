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
 * �����������࣬ӵ����ͻ��˷�����Ϣ���ļ���ˢ���б�����������ӵȷ���
 * @author ������
 *
 */
public class ServerControl
{
	static ServerSocket serverSocket;
	static Vector<Socket> connections;// ����
	static Vector<ServerThread> clients;
	private static int num = -1;
	private static ServerWindow window;

	/**
	 * 
	 * ������Ϣ�����е���
	 */
	public static void sendAll(String s)
	{
		if (connections != null)
		{
			for (Enumeration e = connections.elements(); // Enumeration��ʱ�Ľӿڣ�
															// ������for eachѭ��
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
	 * ������Ϣ������һ����
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
						return true; // ����ֵΪ�棬�ҵ�������˿��Խ�������
					} catch (IOException ioe)
					{
						ioe.printStackTrace();
					}
				}
			}
		}
		return false;// û���ҵ�����ˣ�Ӧ���Ǵ����Ѿ��˳���������
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

	public static void deleteConnection(Socket s, ServerThread cp)//ɾ������
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
	 * ���������ڴ�����
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
			window.insert("�������Ѿ����������ڼ���...");
		} catch (IOException e)
		{
			System.out.println("�쳣");
			System.err.println(e);
			System.exit(1);

		}

		while (true)
		{ // ��ѭ��
			try
			{
				Socket cs = serverSocket.accept();
				ServerThread cp = new ServerThread(cs,window); // ����һ���û��߳�
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
			System.out.println("�û�ȫ���뿪���������ر�......");
			System.exit(0);
		}
	}
	
	public static void main(String arg[])
	{
		new ServerControl();
	}
}

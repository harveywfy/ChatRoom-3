package ChatRoom.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import ChatRoom.configs.ServerConfig;
import ChatRoom.controls.ServerControl;
import ChatRoom.ui.ServerWindow;

/**
 * 服务器端为用户提供服务的线程
 * @author 竺子崴
 *
 */
public class ServerThread implements Runnable
{
	/**
	 * 为某个用户服务的一个用户线程
	 */
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private String name = null;
	private String id = null;
	private ServerWindow window;

	public ServerThread(Socket s,ServerWindow w) throws IOException
	{
		window = w;
		this.socket = s;
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));// 得到输入的端口流
		out = new PrintWriter(s.getOutputStream());// 从端口得到一个输出流
		this.updateList();
	}

	public String getName()
	{
		return name;
	}
	
	public String getId()
	{
		return id;
	}

	public Socket getSocket()
	{
		return socket;
	}

	private void updateList()
	{
		// 更新用户列表（即发送当前在聊天室的用户到新来的用户列表中）
		Vector cs = ServerControl.getClients();
		if (cs != null)
		{
			for (Enumeration e = cs.elements(); e.hasMoreElements();)
			{
				ServerThread cp = (ServerThread) e.nextElement();
				String exist_name = cp.getName();
				String exit_id = cp.getId();

				window.insert(ServerConfig.OLD_USER + "&" + exist_name + "&" + exit_id);
				out.println(ServerConfig.OLD_USER + "&" + exist_name + "&" + exit_id);
				out.flush();
			}
		}
	}

	public void run()
	{
		while (name == null)
		{
			try
			{
				String inmsg;
				inmsg = in.readLine();
				ServerControl.sendAll(ServerConfig.NEW_USER + "&" + inmsg); // 发送信息更新用户列表 new & zhangsan

				String[] userInfo;
				userInfo = inmsg.split("&");
				name = userInfo[0];
				id = userInfo[1];
			} catch (IOException ee)
			{
				ee.printStackTrace();
			}
		}

		while (true)
		{
			try
			{
				String line = in.readLine();
				window.insert(line);
				// 处理退出事件(读取信息)
				if (line.equals(ServerConfig.QUIT))
				{
					ServerControl.sendAll("【系统消息】 " + this.name + " 退出了聊天室");
					ServerControl.sendAll(ServerConfig.DELETE_USER + "&" + this.name);
					ServerControl.deleteConnection(socket, this);
					ServerControl.killOneThread();
					return;
					// 处理刷新用户列表请求
				} else if (line.equals(ServerConfig.REFRESH_LIST))
				{
					this.updateList();
					
				} else// 一般消息,又可以分为几种,对大家说, 与某个人交谈,或者私聊
				{
					String[] inmsg = line.split("&");
					if (inmsg[0].compareTo(ServerConfig.CANCEL_SEND_FILE) == 0)
					{
						ServerControl.sendOne(inmsg[1], ServerConfig.CANCEL_SEND_FILE + "&"
								+ this.name);
					} else if (inmsg[0].compareTo(ServerConfig.SEND_FILE) == 0)// 发送文件消息
					{
						// String[] sendfile = line.split("&");
						ServerControl.sendAll(ServerConfig.SEND_FILE + "&"
								+ this.name + "&" + inmsg[2] + "&" + inmsg[3]
								+ "&" + inmsg[4] + "&" + inmsg[5]);
						ServerControl.sendOne(name, ServerConfig.SEND_FILE_SUCCESS);
					} else if (inmsg[0].compareTo(ServerConfig.ACCEPT_FILE) == 0)// 接收文件消息
					{
						// String[] acceptfile = line.split("&");
						ServerControl
								.sendOne(inmsg[1], inmsg[0] + "&" + this.name);
					} else if (inmsg[0].compareTo(ServerConfig.REFUSE_FILE) == 0)// 拒绝接收文件消息
					{
						// String[] refusefile = line.split("&");
						ServerControl
								.sendOne(inmsg[1], inmsg[0] + "&" + this.name);
					} else if (!line.startsWith(ServerConfig.TO_ALL))
					{ // 对所有人说
						ServerControl.sendAll(this.name + ": " + line);
					}

					// String[] inmsg = line.split("&");
					else if (inmsg[1].equals(ServerConfig.PRIVATE_TRUE))
					{
						if (ServerControl.sendOne(inmsg[2], ServerConfig.PRIVATE_TALK + "&"
								+ name + "&" + inmsg[2] + "&" + inmsg[3]))
						{ // success
							out.println(ServerConfig.PRIVATE_TALK + "&" + name + "&"
									+ inmsg[2] + "&" + inmsg[3]);
							// 再发给自己一份
							out.flush(); // 一定要有
						} else
						{
							out.println(inmsg[2] + "已经离开聊天室");
							out.flush(); // 一定要记住要
						}
					} else
					{
						ServerControl.sendAll(ServerConfig.TO_ALL + "&" + name + "&"
								+ inmsg[2] + "&" + inmsg[3]);
					} // 发给所有的人

				}
			} catch (IOException e)
			{
				window.insert(e.toString());
				try
				{
					socket.close();
				} catch (IOException e2)
				{
					e2.printStackTrace();
				}
				return;
			}
		}
	}
}


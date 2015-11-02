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
 * ��������Ϊ�û��ṩ������߳�
 * @author ������
 *
 */
public class ServerThread implements Runnable
{
	/**
	 * Ϊĳ���û������һ���û��߳�
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
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));// �õ�����Ķ˿���
		out = new PrintWriter(s.getOutputStream());// �Ӷ˿ڵõ�һ�������
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
		// �����û��б������͵�ǰ�������ҵ��û����������û��б��У�
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
				ServerControl.sendAll(ServerConfig.NEW_USER + "&" + inmsg); // ������Ϣ�����û��б� new & zhangsan

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
				// �����˳��¼�(��ȡ��Ϣ)
				if (line.equals(ServerConfig.QUIT))
				{
					ServerControl.sendAll("��ϵͳ��Ϣ�� " + this.name + " �˳���������");
					ServerControl.sendAll(ServerConfig.DELETE_USER + "&" + this.name);
					ServerControl.deleteConnection(socket, this);
					ServerControl.killOneThread();
					return;
					// ����ˢ���û��б�����
				} else if (line.equals(ServerConfig.REFRESH_LIST))
				{
					this.updateList();
					
				} else// һ����Ϣ,�ֿ��Է�Ϊ����,�Դ��˵, ��ĳ���˽�̸,����˽��
				{
					String[] inmsg = line.split("&");
					if (inmsg[0].compareTo(ServerConfig.CANCEL_SEND_FILE) == 0)
					{
						ServerControl.sendOne(inmsg[1], ServerConfig.CANCEL_SEND_FILE + "&"
								+ this.name);
					} else if (inmsg[0].compareTo(ServerConfig.SEND_FILE) == 0)// �����ļ���Ϣ
					{
						// String[] sendfile = line.split("&");
						ServerControl.sendAll(ServerConfig.SEND_FILE + "&"
								+ this.name + "&" + inmsg[2] + "&" + inmsg[3]
								+ "&" + inmsg[4] + "&" + inmsg[5]);
						ServerControl.sendOne(name, ServerConfig.SEND_FILE_SUCCESS);
					} else if (inmsg[0].compareTo(ServerConfig.ACCEPT_FILE) == 0)// �����ļ���Ϣ
					{
						// String[] acceptfile = line.split("&");
						ServerControl
								.sendOne(inmsg[1], inmsg[0] + "&" + this.name);
					} else if (inmsg[0].compareTo(ServerConfig.REFUSE_FILE) == 0)// �ܾ������ļ���Ϣ
					{
						// String[] refusefile = line.split("&");
						ServerControl
								.sendOne(inmsg[1], inmsg[0] + "&" + this.name);
					} else if (!line.startsWith(ServerConfig.TO_ALL))
					{ // ��������˵
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
							// �ٷ����Լ�һ��
							out.flush(); // һ��Ҫ��
						} else
						{
							out.println(inmsg[2] + "�Ѿ��뿪������");
							out.flush(); // һ��Ҫ��סҪ
						}
					} else
					{
						ServerControl.sendAll(ServerConfig.TO_ALL + "&" + name + "&"
								+ inmsg[2] + "&" + inmsg[3]);
					} // �������е���

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


package ChatRoom.threads;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ChatRoom.configs.ServerConfig;
import ChatRoom.configs.UIconfig;
import ChatRoom.controls.ClientControl;
import ChatRoom.ui.MultiChatRoom;

/**
 * 用户线程，用于与服务器交互信息
 * @author 竺子崴
 *
 */
public class ClientThread extends Thread{
	private MultiChatRoom room;
	private String filemsg;
	private AcceptFileThread acceptthread;
	private BufferedReader in;
	//private PrintWriter out;
	private String myname;
	private ClientControl control;
	
	public ClientThread(MultiChatRoom r,BufferedReader i,String host,ClientControl c)
	{
		control = c;
		room = r;
		in = i;
		myname = host;
	}
	
	public void run()
	{
		String inmsg;

		while (true)
		{
			try
			{
				if ((inmsg = in.readLine()) != null)
				{

					// 已经在聊天室里的人显示到列表中
					if (inmsg.startsWith(ServerConfig.OLD_USER))
					{
						String[] userInfo = inmsg.split("&");
						room.listAdd(userInfo[1]); // 更新用户列表
					} 
					else if (inmsg.startsWith(ServerConfig.NEW_USER))
					{ // 接收第一次服务器发送欢迎信息
						String[] userInfo = inmsg.split("&");
						room.listAdd(userInfo[1]); // 更新用户列表 new & zhangsan 

						room.insert(userInfo[1] + "上线了");
					}
					else if(inmsg.startsWith(ServerConfig.DELETE_USER))
					{
						String[] userInfo = inmsg.split("&");
						room.listDelete(userInfo[1]);
					}
					else if(inmsg.startsWith(ServerConfig.SEND_FILE_SUCCESS))
					{
						room.setButtonState(UIconfig.SET_REFUSE_SEND_FILE_BU, false);
						room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);
					}
					else if (inmsg != null)// 一般消息
					{ 
						String[] sendfile = inmsg.split("&");
						if (sendfile[0].compareTo(ServerConfig.CANCEL_SEND_FILE) == 0)
						{
							room.insert(sendfile[1] + "取消了文件发送");

						} else if (sendfile[0].compareTo(ServerConfig.SEND_FILE) == 0)
						{ // 如果是传送文件请求
							JFileChooser fc = new JFileChooser();
							//this.acceptfileArea.setText(sendfile[2]);
							//room.insert(sendfile[1] + " 发来文件,点接受按钮开始接受");
							if(!sendfile[1].equals(myname))
							{
								filemsg = inmsg;
								int define =  JOptionPane.showConfirmDialog
										(room, "是否接收来自" + sendfile[1] + "发来的文件？","文件确认",JOptionPane.YES_NO_OPTION);
					              if(define ==0)
					              {
					            	  String extension = sendfile[2].substring(sendfile[2].lastIndexOf("."));
					            	  String filename = sendfile[2].substring(sendfile[2].lastIndexOf("\\"));
					            	  
					            	  System.out.println("运行到了这里,文件后缀名为："+extension);
					            	  fc.setDialogTitle("保存文件");
					            	  //fc.setFileFilter(new MyFilter(extension));
					            	  fc.setSelectedFile(new File(filename)); //设置默认文件名
					            	  //String saveType[] = {"txt","java"};
					            	  //fc.setFileFilter(new FileNameExtensionFilter("TXT & JAVA FILE",saveType));//设置默认保存类型
					            	  int  flag=0;
					            	  try{    
					           				flag=fc.showSaveDialog(room);    
					                    }
					                  catch(HeadlessException head){    

					                      System.out.println("Save File Dialog ERROR!");   
					                   } 
					            	  if(flag==JFileChooser.APPROVE_OPTION)   
					                  {   
					                      //获得你输入要保存的文件   
					                        File receivefile=fc.getSelectedFile();   
					                        String path =receivefile.getPath();
					                        String savefile = path/*+extension*/;
					                        System.out.println("下面进入acceptFile方法！"+savefile);   
					                        control.acceptFile(savefile, inmsg);      
					                  }   
					              }
					              else
					              {
					            	  control.refuseFile(filemsg);
					              }
							}	
							
						} else if (sendfile[0].compareTo(ServerConfig.ACCEPT_FILE) == 0)
						{ // 如果是传送文件请求
							// String[] acceptfile = inmsg.split("&");
							// acceptfileArea.setText(sendfile[2]);
							room.insert( sendfile[1] + " 接收了你发的文件");

						} else if (sendfile[0].compareTo(ServerConfig.REFUSE_FILE) == 0)
						{ // 如果是拒绝文件请求

							room.insert(sendfile[1] + " 拒绝了你发的文件");
							//sendboolean = false;
							//this.sendfile.setEnabled(true);
							room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);
							//this.sendfileArea.setText("");

						} else if (sendfile[0].compareTo(ServerConfig.TO_ALL) == 0)
						{
							if (sendfile[2].equals(myname))
							{ // 如果是发给自己的消息

								room.insert(sendfile[1] + "对『"
										+ sendfile[2] + "』说:" + sendfile[3]);
							} // 显示到我的频道

							room.insert(sendfile[1] + "对『" + sendfile[2]
									+ "』说:" + sendfile[3]);

						} else if (inmsg.startsWith(ServerConfig.PRIVATE_TALK))
						{
							String showmsg[] = inmsg.split("&");
							if (showmsg[1].equals(myname))
							{// 如果接收到的是我自己发送的消息

								room.insert("您对『" + showmsg[2] + "』说: "
										+ showmsg[3]);
								//commonArea.setCaretPosition(commonArea.getText
								// ().length());
							} else
							{ // 接收到的是别人发给我的消息（悄悄话）

								room.insert("『" + showmsg[1] + "』对您说: "
										+ showmsg[3]);
							}
							// myMsgArea.setCaretPosition(myMsgArea.getText().
							// length());
						} else
						{

							room.insert(inmsg);
						}
						//commonArea.setCaretPosition(commonArea.getText().length
						// ());
					}
				}
			} catch (Exception ee)
			{
				ee.printStackTrace();
				room.insert("与服务器中断，请重新登录！");
				// myMsgArea. setCaretPosition(myMsgArea.getText().length());
				// 将输出流，输入流设置为 null
				in = null;
				//out = null;
				return;
			}
		}
	}

}

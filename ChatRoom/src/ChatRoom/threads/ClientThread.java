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
 * �û��̣߳������������������Ϣ
 * @author ������
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

					// �Ѿ����������������ʾ���б���
					if (inmsg.startsWith(ServerConfig.OLD_USER))
					{
						String[] userInfo = inmsg.split("&");
						room.listAdd(userInfo[1]); // �����û��б�
					} 
					else if (inmsg.startsWith(ServerConfig.NEW_USER))
					{ // ���յ�һ�η��������ͻ�ӭ��Ϣ
						String[] userInfo = inmsg.split("&");
						room.listAdd(userInfo[1]); // �����û��б� new & zhangsan 

						room.insert(userInfo[1] + "������");
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
					else if (inmsg != null)// һ����Ϣ
					{ 
						String[] sendfile = inmsg.split("&");
						if (sendfile[0].compareTo(ServerConfig.CANCEL_SEND_FILE) == 0)
						{
							room.insert(sendfile[1] + "ȡ�����ļ�����");

						} else if (sendfile[0].compareTo(ServerConfig.SEND_FILE) == 0)
						{ // ����Ǵ����ļ�����
							JFileChooser fc = new JFileChooser();
							//this.acceptfileArea.setText(sendfile[2]);
							//room.insert(sendfile[1] + " �����ļ�,����ܰ�ť��ʼ����");
							if(!sendfile[1].equals(myname))
							{
								filemsg = inmsg;
								int define =  JOptionPane.showConfirmDialog
										(room, "�Ƿ��������" + sendfile[1] + "�������ļ���","�ļ�ȷ��",JOptionPane.YES_NO_OPTION);
					              if(define ==0)
					              {
					            	  String extension = sendfile[2].substring(sendfile[2].lastIndexOf("."));
					            	  String filename = sendfile[2].substring(sendfile[2].lastIndexOf("\\"));
					            	  
					            	  System.out.println("���е�������,�ļ���׺��Ϊ��"+extension);
					            	  fc.setDialogTitle("�����ļ�");
					            	  //fc.setFileFilter(new MyFilter(extension));
					            	  fc.setSelectedFile(new File(filename)); //����Ĭ���ļ���
					            	  //String saveType[] = {"txt","java"};
					            	  //fc.setFileFilter(new FileNameExtensionFilter("TXT & JAVA FILE",saveType));//����Ĭ�ϱ�������
					            	  int  flag=0;
					            	  try{    
					           				flag=fc.showSaveDialog(room);    
					                    }
					                  catch(HeadlessException head){    

					                      System.out.println("Save File Dialog ERROR!");   
					                   } 
					            	  if(flag==JFileChooser.APPROVE_OPTION)   
					                  {   
					                      //���������Ҫ������ļ�   
					                        File receivefile=fc.getSelectedFile();   
					                        String path =receivefile.getPath();
					                        String savefile = path/*+extension*/;
					                        System.out.println("�������acceptFile������"+savefile);   
					                        control.acceptFile(savefile, inmsg);      
					                  }   
					              }
					              else
					              {
					            	  control.refuseFile(filemsg);
					              }
							}	
							
						} else if (sendfile[0].compareTo(ServerConfig.ACCEPT_FILE) == 0)
						{ // ����Ǵ����ļ�����
							// String[] acceptfile = inmsg.split("&");
							// acceptfileArea.setText(sendfile[2]);
							room.insert( sendfile[1] + " �������㷢���ļ�");

						} else if (sendfile[0].compareTo(ServerConfig.REFUSE_FILE) == 0)
						{ // ����Ǿܾ��ļ�����

							room.insert(sendfile[1] + " �ܾ����㷢���ļ�");
							//sendboolean = false;
							//this.sendfile.setEnabled(true);
							room.setButtonState(UIconfig.SET_SEND_FILE_BU, true);
							//this.sendfileArea.setText("");

						} else if (sendfile[0].compareTo(ServerConfig.TO_ALL) == 0)
						{
							if (sendfile[2].equals(myname))
							{ // ����Ƿ����Լ�����Ϣ

								room.insert(sendfile[1] + "�ԡ�"
										+ sendfile[2] + "��˵:" + sendfile[3]);
							} // ��ʾ���ҵ�Ƶ��

							room.insert(sendfile[1] + "�ԡ�" + sendfile[2]
									+ "��˵:" + sendfile[3]);

						} else if (inmsg.startsWith(ServerConfig.PRIVATE_TALK))
						{
							String showmsg[] = inmsg.split("&");
							if (showmsg[1].equals(myname))
							{// ������յ��������Լ����͵���Ϣ

								room.insert("���ԡ�" + showmsg[2] + "��˵: "
										+ showmsg[3]);
								//commonArea.setCaretPosition(commonArea.getText
								// ().length());
							} else
							{ // ���յ����Ǳ��˷����ҵ���Ϣ�����Ļ���

								room.insert("��" + showmsg[1] + "������˵: "
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
				room.insert("��������жϣ������µ�¼��");
				// myMsgArea. setCaretPosition(myMsgArea.getText().length());
				// �������������������Ϊ null
				in = null;
				//out = null;
				return;
			}
		}
	}

}

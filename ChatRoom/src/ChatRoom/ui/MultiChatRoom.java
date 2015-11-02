package ChatRoom.ui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import ChatRoom.configs.UIconfig;
import ChatRoom.controls.ClientControl;

/**
 * 聊天界面，以及UI控件的对外接口
 * @author陈昊
 * 
 */
public class MultiChatRoom extends JFrame implements ActionListener,ListSelectionListener
{
	private Box leftbox = null;
	private Box rightbox = null;
	private Box leftrightbox = null;

	private JButton sendfile; // 发送文件按钮
	private JButton cancelsendfile; // 取消发送文件按钮


	private static final long serialVersionUID = 1L;
	// ***************菜单栏***********************
	private Box box = null; // 放输入组件的容器
	
			
	// 字体名称;字号大小;文字样式;文字颜色;传送
	private StyledDocument doc = null;

	private JFileChooser jfc;// 文件保存路径选择器
	// private JFrame fr;
	private JTextPane commonArea = null; // 公共发言区
	private JTextArea inMsgField; // 发言输入框
	private JButton sentButton; // 发送消息按钮

	private JList peopleList; // 显示进入聊天室的人名单
	private JButton refurbishButton;// 刷新列表按钮
	private DefaultListModel listModel;// 用户列表

	private String myName;

	private String outmsg;// 发送的信息
	private String mywords;// 要说的话
	private JPanel centerPanel;
	private JScrollPane commonAreaScroll;
	private JScrollPane inMsgFieldScroll;
	
	private String IP;
	private ClientControl control;

	public MultiChatRoom(String host)
	{
		super(host + "的聊天室");
		try
		{  //使用Windows的界面风格
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		IP = ClientControl.getIP();
		
		myName = host;

		// *****聊天室右侧************//
		sendfile = new JButton("发送文件");
		cancelsendfile = new JButton("取消发送");
		cancelsendfile.setEnabled(false);

		leftrightbox = Box.createHorizontalBox(); // 行结构

		Box rightabove = Box.createVerticalBox();
		rightabove.add(leftrightbox);

		// leftrightbox.add(jpb,BorderLayout.CENTER);

		rightbox = Box.createVerticalBox();// 竖结构

		Box rightmiddle = Box.createVerticalBox();

		// *********************聊天室顶层**************//

		JPanel upperPanel = new JPanel();

		box = Box.createVerticalBox(); // 竖结构
		Box box_1 = Box.createHorizontalBox(); // 横结构
		Box box_2 = Box.createVerticalBox(); // 横结构
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(Box.createHorizontalStrut(8));

		upperPanel.add(box, BorderLayout.SOUTH);

		// **************中间聊天室两个窗口***********************//

		Border brd = BorderFactory.createMatteBorder(// 边框修饰色
			2, 2, 2, 1, new Color(125, 161, 253));

		centerPanel = new JPanel(new BorderLayout());

		commonArea = new JTextPane(); // 公共言论区
		commonArea.setBorder(brd);
		commonArea.setEditable(false); // 不可编辑
		commonArea.getScrollableUnitIncrement(new Rectangle(10, 15),
			SwingConstants.VERTICAL, -2);
		commonAreaScroll = new JScrollPane(commonArea);
		commonAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 设置滚动条什么时候出现
		commonAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		commonAreaScroll.setBorder(BorderFactory.createTitledBorder("群聊区"));

		box_2.add(commonAreaScroll);
		box_2.add(Box.createVerticalStrut(2));
		
		JPanel eastPanel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel();// 实现 java.util.Vector API 在发生更改时通知
		// ListDataListener
		peopleList = new JList(listModel);
		peopleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 设置单选还是多选
		peopleList.setBorder(brd);
		JScrollPane ListScrollPane = new JScrollPane(peopleList);
		ListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 设置滚动条什么时候出现
		ListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ListScrollPane.setBorder(BorderFactory.createTitledBorder("在线好友列表"));
		ListScrollPane.setPreferredSize(new Dimension(150, 400));

		refurbishButton = new JButton("刷新列表");
		refurbishButton.addActionListener(this);// 监听刷新按钮
		peopleList.addListSelectionListener(this);

		eastPanel.add(ListScrollPane, BorderLayout.CENTER);
		eastPanel.add(refurbishButton, BorderLayout.SOUTH);
	
		centerPanel.add("Center",box_2);
		centerPanel.add("East",eastPanel);

		// ******************输入发送区***********************
		JPanel centerLowerPanel = new JPanel(new BorderLayout());
		JPanel tempPanel1 = new JPanel(new BorderLayout());
		JPanel tempPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		inMsgField = new JTextArea(3, 2);
		inMsgField.setBorder(brd);
		inMsgField.setBackground(new Color(248, 243, 209));// 设置聊天框体的颜色

		sentButton = new JButton("发送");

		inMsgFieldScroll = new JScrollPane(inMsgField);
		inMsgFieldScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inMsgFieldScroll.setBorder(BorderFactory.createTitledBorder("编辑区"));
		tempPanel1.add(inMsgFieldScroll, BorderLayout.CENTER);

		sentButton.setBackground(Color.WHITE);

		tempPanel2.add(new JLabel("   "));
		tempPanel2.add(sendfile);
		tempPanel2.add(new JLabel("   "));
		tempPanel2.add(cancelsendfile);
		tempPanel2.add(new JLabel("   "));
		tempPanel2.add(sentButton);
		tempPanel2.add(new JLabel(""));

		centerLowerPanel.add(tempPanel1, BorderLayout.CENTER);
		centerLowerPanel.add(tempPanel2, BorderLayout.SOUTH);

		centerPanel.add(centerLowerPanel, BorderLayout.SOUTH);

		// ********************** 最下面的面板(South)*****************//
		JLabel BordBottomLabel = new JLabel();
		Icon BordBottom = new ImageIcon("images\\BordBottom.gif");
		BordBottomLabel.setIcon(BordBottom);

		// ******************组合整个框架**************************//
		leftbox = Box.createVerticalBox();// 竖结构
		// leftbox = Box.createHorizontalStrut(30);
		leftbox.add(upperPanel, BorderLayout.NORTH);
		leftbox.add(centerPanel, BorderLayout.CENTER);
		leftbox.add(BordBottomLabel, BorderLayout.SOUTH);

		this.add(leftbox, BorderLayout.CENTER);
		this.add(rightbox, BorderLayout.EAST);
		//this.setResizable(false);

		sentButton.addActionListener(this);// 监听发送按钮

		sendfile.addActionListener(this);// 监听文件发送按钮

		cancelsendfile.addActionListener(this);// 监听取消发送文件

		inMsgField.requestFocus();
		this.setLocation(450, 50); // 居中
		this.setSize(600, 600);
		
		control = new ClientControl(host,this);
		
		this.addWindowListener(new WindowAdapter()
		{ // 匿名类 监听窗口关闭时间
				public void windowClosing(WindowEvent event)
				{
					control.shutDown(myName);
				}
		});

		this.setVisible(true);
	}
	
	public void initControl(BufferedReader in, PrintWriter out)
	{
		control.initRW(in, out);
	}

	public void actionPerformed(ActionEvent e)
	{

		if (e.getSource() == sendfile)
		{// 发送文件按钮
			FileDialog fileDlg = new FileDialog(this, "选择发送文件", FileDialog.LOAD);
     		
    		fileDlg.setModal(true);
     		fileDlg.setVisible(true);
     		//System.out.println(fileDlg.getDirectory()+fileDlg.getFile());
     		if(fileDlg.getDirectory() != null && fileDlg.getFile() != null)
     		{
     			System.out.println(fileDlg.getDirectory()+fileDlg.getFile());
         		control.sendMsg("发送文件->"+fileDlg.getFile());
         	    String sendFile = fileDlg.getDirectory()+fileDlg.getFile();
         		System.out.println("开始进入sendFile方法！");
         		control.sendFile(sendFile);
     		}
     		
		}

		if (e.getSource() == cancelsendfile)
		{// 取消文件发送按钮
				control.cancelSendFile();
		}

		if (e.getSource() == sentButton)
		{// 如果监听到发送信息按钮被点击
			mywords = inMsgField.getText();
			control.sendMsg(mywords);
		}
		if (e.getSource() == refurbishButton)
		{ // 如果监听到要刷新列表
				listModel.clear(); // 清空列表
				control.refreshList();
		}
	}

	// *********************设置阶段**************************//

	/**
	* 将文本插入JTextPane
	* 
	* @param attrib
	*/
	public void insert(String words)
	{
		JTextPane j = commonArea;
		
		// Date noeTime = new Date();
		// SimpleDateFormat matter = new SimpleDateFormat("HH:mm:ss ");
		int y, mi, d, h, m, s;
		Calendar cal = Calendar.getInstance();
		y = cal.get(Calendar.YEAR);
		mi = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DATE);
		h = cal.get(Calendar.HOUR_OF_DAY);
		m = cal.get(Calendar.MINUTE);
		s = cal.get(Calendar.SECOND);
		String time = y + "." + mi + "." + d + "." + h + ":" + m + ":" + s;
		doc = j.getStyledDocument();
		try
		{ // 插入文本
			doc.insertString(doc.getLength(), time + "---" + words + "\n", new SimpleAttributeSet());
			this.inMsgField.setText("");
			
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}

	}
	/**
	 * 清空输入框
	 */
	public void cleanInMsgWindow()
	{
		inMsgField.setText("");
	}
	
	public void setButtonState(int which,boolean isAble)
	{
		JButton button = null;
		switch(which)
		{
		case UIconfig.SET_REFRESH_LIST_BU:button = refurbishButton;break;
		case UIconfig.SET_SEND_FILE_BU:button = sendfile;break;
		case UIconfig.SET_SEND_MSG_BU:button = sentButton;break;
		case UIconfig.SET_REFUSE_SEND_FILE_BU:button = cancelsendfile;break;
		}
		if(isAble)
			button.setEnabled(true);
		else
			button.setEnabled(false);
	}
	
	/**
	 * 用户列表添加用户
	 * @param text
	 */
	public void listAdd(String text)
	{
		listModel.addElement(text);
	}
	
	/**
	 * 用户列表删去用户
	 * @param name
	 */
	public void listDelete(String name)
	{
		listModel.removeElement(name);
	}

	public void valueChanged(ListSelectionEvent e)
	{// 监听下拉列表的显示内容
		if (e.getSource() == peopleList)
		{
			try
			{
				String select = (String) peopleList.getSelectedValue();
				if (select != null)
				{ // 确保选择非空
					String[] userInfo = select.split("〖");
					String name = userInfo[0].trim(); // 提取名字

					if (!name.equals(myName))
					{ // 不能添加自己


					}
				}
			} catch (Exception ee)
			{
				System.out.println("发生错误 在valueChanged " + ee);
			}
		}
	}
}
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
 * ������棬�Լ�UI�ؼ��Ķ���ӿ�
 * @author���
 * 
 */
public class MultiChatRoom extends JFrame implements ActionListener,ListSelectionListener
{
	private Box leftbox = null;
	private Box rightbox = null;
	private Box leftrightbox = null;

	private JButton sendfile; // �����ļ���ť
	private JButton cancelsendfile; // ȡ�������ļ���ť


	private static final long serialVersionUID = 1L;
	// ***************�˵���***********************
	private Box box = null; // ���������������
	
			
	// ��������;�ֺŴ�С;������ʽ;������ɫ;����
	private StyledDocument doc = null;

	private JFileChooser jfc;// �ļ�����·��ѡ����
	// private JFrame fr;
	private JTextPane commonArea = null; // ����������
	private JTextArea inMsgField; // ���������
	private JButton sentButton; // ������Ϣ��ť

	private JList peopleList; // ��ʾ���������ҵ�������
	private JButton refurbishButton;// ˢ���б�ť
	private DefaultListModel listModel;// �û��б�

	private String myName;

	private String outmsg;// ���͵���Ϣ
	private String mywords;// Ҫ˵�Ļ�
	private JPanel centerPanel;
	private JScrollPane commonAreaScroll;
	private JScrollPane inMsgFieldScroll;
	
	private String IP;
	private ClientControl control;

	public MultiChatRoom(String host)
	{
		super(host + "��������");
		try
		{  //ʹ��Windows�Ľ�����
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		IP = ClientControl.getIP();
		
		myName = host;

		// *****�������Ҳ�************//
		sendfile = new JButton("�����ļ�");
		cancelsendfile = new JButton("ȡ������");
		cancelsendfile.setEnabled(false);

		leftrightbox = Box.createHorizontalBox(); // �нṹ

		Box rightabove = Box.createVerticalBox();
		rightabove.add(leftrightbox);

		// leftrightbox.add(jpb,BorderLayout.CENTER);

		rightbox = Box.createVerticalBox();// ���ṹ

		Box rightmiddle = Box.createVerticalBox();

		// *********************�����Ҷ���**************//

		JPanel upperPanel = new JPanel();

		box = Box.createVerticalBox(); // ���ṹ
		Box box_1 = Box.createHorizontalBox(); // ��ṹ
		Box box_2 = Box.createVerticalBox(); // ��ṹ
		box_1.add(Box.createHorizontalStrut(8));
		box_1.add(Box.createHorizontalStrut(8));

		upperPanel.add(box, BorderLayout.SOUTH);

		// **************�м���������������***********************//

		Border brd = BorderFactory.createMatteBorder(// �߿�����ɫ
			2, 2, 2, 1, new Color(125, 161, 253));

		centerPanel = new JPanel(new BorderLayout());

		commonArea = new JTextPane(); // ����������
		commonArea.setBorder(brd);
		commonArea.setEditable(false); // ���ɱ༭
		commonArea.getScrollableUnitIncrement(new Rectangle(10, 15),
			SwingConstants.VERTICAL, -2);
		commonAreaScroll = new JScrollPane(commonArea);
		commonAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// ���ù�����ʲôʱ�����
		commonAreaScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		commonAreaScroll.setBorder(BorderFactory.createTitledBorder("Ⱥ����"));

		box_2.add(commonAreaScroll);
		box_2.add(Box.createVerticalStrut(2));
		
		JPanel eastPanel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel();// ʵ�� java.util.Vector API �ڷ�������ʱ֪ͨ
		// ListDataListener
		peopleList = new JList(listModel);
		peopleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// ���õ�ѡ���Ƕ�ѡ
		peopleList.setBorder(brd);
		JScrollPane ListScrollPane = new JScrollPane(peopleList);
		ListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// ���ù�����ʲôʱ�����
		ListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		ListScrollPane.setBorder(BorderFactory.createTitledBorder("���ߺ����б�"));
		ListScrollPane.setPreferredSize(new Dimension(150, 400));

		refurbishButton = new JButton("ˢ���б�");
		refurbishButton.addActionListener(this);// ����ˢ�°�ť
		peopleList.addListSelectionListener(this);

		eastPanel.add(ListScrollPane, BorderLayout.CENTER);
		eastPanel.add(refurbishButton, BorderLayout.SOUTH);
	
		centerPanel.add("Center",box_2);
		centerPanel.add("East",eastPanel);

		// ******************���뷢����***********************
		JPanel centerLowerPanel = new JPanel(new BorderLayout());
		JPanel tempPanel1 = new JPanel(new BorderLayout());
		JPanel tempPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		inMsgField = new JTextArea(3, 2);
		inMsgField.setBorder(brd);
		inMsgField.setBackground(new Color(248, 243, 209));// ��������������ɫ

		sentButton = new JButton("����");

		inMsgFieldScroll = new JScrollPane(inMsgField);
		inMsgFieldScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inMsgFieldScroll.setBorder(BorderFactory.createTitledBorder("�༭��"));
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

		// ********************** ����������(South)*****************//
		JLabel BordBottomLabel = new JLabel();
		Icon BordBottom = new ImageIcon("images\\BordBottom.gif");
		BordBottomLabel.setIcon(BordBottom);

		// ******************����������**************************//
		leftbox = Box.createVerticalBox();// ���ṹ
		// leftbox = Box.createHorizontalStrut(30);
		leftbox.add(upperPanel, BorderLayout.NORTH);
		leftbox.add(centerPanel, BorderLayout.CENTER);
		leftbox.add(BordBottomLabel, BorderLayout.SOUTH);

		this.add(leftbox, BorderLayout.CENTER);
		this.add(rightbox, BorderLayout.EAST);
		//this.setResizable(false);

		sentButton.addActionListener(this);// �������Ͱ�ť

		sendfile.addActionListener(this);// �����ļ����Ͱ�ť

		cancelsendfile.addActionListener(this);// ����ȡ�������ļ�

		inMsgField.requestFocus();
		this.setLocation(450, 50); // ����
		this.setSize(600, 600);
		
		control = new ClientControl(host,this);
		
		this.addWindowListener(new WindowAdapter()
		{ // ������ �������ڹر�ʱ��
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
		{// �����ļ���ť
			FileDialog fileDlg = new FileDialog(this, "ѡ�����ļ�", FileDialog.LOAD);
     		
    		fileDlg.setModal(true);
     		fileDlg.setVisible(true);
     		//System.out.println(fileDlg.getDirectory()+fileDlg.getFile());
     		if(fileDlg.getDirectory() != null && fileDlg.getFile() != null)
     		{
     			System.out.println(fileDlg.getDirectory()+fileDlg.getFile());
         		control.sendMsg("�����ļ�->"+fileDlg.getFile());
         	    String sendFile = fileDlg.getDirectory()+fileDlg.getFile();
         		System.out.println("��ʼ����sendFile������");
         		control.sendFile(sendFile);
     		}
     		
		}

		if (e.getSource() == cancelsendfile)
		{// ȡ���ļ����Ͱ�ť
				control.cancelSendFile();
		}

		if (e.getSource() == sentButton)
		{// ���������������Ϣ��ť�����
			mywords = inMsgField.getText();
			control.sendMsg(mywords);
		}
		if (e.getSource() == refurbishButton)
		{ // ���������Ҫˢ���б�
				listModel.clear(); // ����б�
				control.refreshList();
		}
	}

	// *********************���ý׶�**************************//

	/**
	* ���ı�����JTextPane
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
		{ // �����ı�
			doc.insertString(doc.getLength(), time + "---" + words + "\n", new SimpleAttributeSet());
			this.inMsgField.setText("");
			
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}

	}
	/**
	 * ��������
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
	 * �û��б�����û�
	 * @param text
	 */
	public void listAdd(String text)
	{
		listModel.addElement(text);
	}
	
	/**
	 * �û��б�ɾȥ�û�
	 * @param name
	 */
	public void listDelete(String name)
	{
		listModel.removeElement(name);
	}

	public void valueChanged(ListSelectionEvent e)
	{// ���������б����ʾ����
		if (e.getSource() == peopleList)
		{
			try
			{
				String select = (String) peopleList.getSelectedValue();
				if (select != null)
				{ // ȷ��ѡ��ǿ�
					String[] userInfo = select.split("��");
					String name = userInfo[0].trim(); // ��ȡ����

					if (!name.equals(myName))
					{ // ��������Լ�


					}
				}
			} catch (Exception ee)
			{
				System.out.println("�������� ��valueChanged " + ee);
			}
		}
	}
}
package ChatRoom.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
/**
 * 服务器界面
 * @author 陈昊
 *
 */
public class ServerWindow extends JFrame implements ActionListener{
	private JTextPane serverText;
	private JScrollPane serverTextScroll;
	private JButton quit;//关闭服务器按钮
	private JButton clean;//清空信息框
	private Container container;

	public ServerWindow(String hostAddr)
	{
		super("服务器地址："+hostAddr);
		quit = new JButton("关闭服务器");
		clean = new JButton("清空");
		quit.addActionListener(this);
		clean.addActionListener(this);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(clean);
		buttons.add(quit);
		
		Box box_2 = Box.createVerticalBox(); // 横结构
		serverText = new JTextPane(); 
		Border brd = BorderFactory.createMatteBorder(// 边框修饰色
				2, 2, 2, 1, new Color(125, 161, 253));
		serverText.setBorder(brd);
		serverText.setEditable(false); // 不可编辑
		serverText.getScrollableUnitIncrement(new Rectangle(10, 15),SwingConstants.VERTICAL, -2);
		serverTextScroll = new JScrollPane(serverText);
		serverTextScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);// 设置滚动条什么时候出现
		serverTextScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		serverTextScroll.setBorder(BorderFactory.createTitledBorder("服务器信息"));
		serverText.setPreferredSize(new Dimension(300,200));
		box_2.add(serverTextScroll);
		box_2.add(Box.createVerticalStrut(2));
		
		container = this.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(box_2, "North");
		container.add(buttons, "Center");
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		//setSize(300,300);
		setVisible(true);
		pack();
	}
	
	/**
	* 将文本插入JTextPane
	* 
	* @param attrib
	*/
	public void insert(String words)
	{
		JTextPane j = serverText;
		
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
		StyledDocument doc = j.getStyledDocument();
		try
		{ // 插入文本
			doc.insertString(doc.getLength(), time + "---" + words + "\n", new SimpleAttributeSet());
			
		} catch (BadLocationException e)
		{
			e.printStackTrace();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == quit)
		{
			System.exit(0);
		}
		else if(e.getSource() == clean)
		{
			serverText.setText("");
		}
	}
}

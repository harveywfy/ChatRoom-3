package ChatRoom.configs;

public class ServerConfig {
	public static final int DEFAULT_PORT = 6001;//默认服务器的端口
	public static final String DEFAULT_IP = "localhost";//默认的ip
	
	/**
	 * 与服务器交互的标识
	 */
	public static final String SEND_FILE_SUCCESS = "sendFileSuccess";//发送文件成功
	public static final String NEW_USER = "new";//表明为新加用户，客户端刷新列表时要添加
	public static final String OLD_USER = "old";//表明为老用户
	public static final String QUIT = "quit";//用户推出申明
	public static final String DELETE_USER = "DELETE";//从列表删除退出用户
	public static final String REFRESH_LIST = "refurbish";//刷新列表申请
	public static final String CANCEL_SEND_FILE = "cancelsendfile";//取消发送文件申请
	public static final String SEND_FILE = "sendfile";//发送文件申请
	public static final String ACCEPT_FILE = "acceptfile";//接收文件申明
	public static final String REFUSE_FILE = "refusefile";//拒接接收文件申明
	public static final String TO_ALL = "withWho";//对所有人的信息申明
	public static final String PRIVATE_TRUE = "privateTure";//私聊申明
	public static final String PRIVATE_TALK = "privateTalk";//私聊信息申明
}

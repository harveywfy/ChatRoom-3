package ChatRoom.configs;

public class ServerConfig {
	public static final int DEFAULT_PORT = 6001;//Ĭ�Ϸ������Ķ˿�
	public static final String DEFAULT_IP = "localhost";//Ĭ�ϵ�ip
	
	/**
	 * ������������ı�ʶ
	 */
	public static final String SEND_FILE_SUCCESS = "sendFileSuccess";//�����ļ��ɹ�
	public static final String NEW_USER = "new";//����Ϊ�¼��û����ͻ���ˢ���б�ʱҪ���
	public static final String OLD_USER = "old";//����Ϊ���û�
	public static final String QUIT = "quit";//�û��Ƴ�����
	public static final String DELETE_USER = "DELETE";//���б�ɾ���˳��û�
	public static final String REFRESH_LIST = "refurbish";//ˢ���б�����
	public static final String CANCEL_SEND_FILE = "cancelsendfile";//ȡ�������ļ�����
	public static final String SEND_FILE = "sendfile";//�����ļ�����
	public static final String ACCEPT_FILE = "acceptfile";//�����ļ�����
	public static final String REFUSE_FILE = "refusefile";//�ܽӽ����ļ�����
	public static final String TO_ALL = "withWho";//�������˵���Ϣ����
	public static final String PRIVATE_TRUE = "privateTure";//˽������
	public static final String PRIVATE_TALK = "privateTalk";//˽����Ϣ����
}

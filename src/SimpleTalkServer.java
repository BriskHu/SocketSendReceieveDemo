import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ����Socketͨ�ŷ�ʽ�ļ�����������������˳���
 * @author Brisk
 * Socketͨ�ŷ�ʽ��ʾ������
 */
public class SimpleTalkServer
{
	/**
	 * �����Ա������
	 */
	public static Client[] allclient = new Client[20];
	public static int clientnum = 0;
	public static String hint = "";

	/**
	 * �������˳����������������Ҫ���������IP��localhost���˿���8888��
	 * @param args δʹ�����������
	 */
	public static void main(String[] args)
	{
		Frame show = new Frame("Server");
		Panel p = new Panel();
		TextArea input = new TextArea(15, 35);
		
		try {
			ServerSocket server = new ServerSocket(8888);  //����Socket���ӡ�
			hint = "Server is operating.\n";

			input.setText(hint);
			p.add(input);
			show.add(p);
			
			/**
			 * ���÷������˵Ĵ��ڼ��������������������ڹرհ�ťʱ�Ĳ�����
			 */
			show.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					try {
						String message = new String("Server closed.\n");

						for (int i = 0; i < clientnum; i++) {
							SimpleTalkServer.allclient[i].dos.writeUTF(message);  //����DataOutputStream����ת���յ�����Ϣ��
						}
						server.close();
					} catch (IOException e) {
					}

					w.getWindow().dispose();
					System.exit(0);
				}
			});
			show.setSize(300, 300);
			show.setVisible(true);

			while (true) {
				Socket client = server.accept();  //����������ʽ����socket�������ڶ˿ڣ��������ݡ�
				hint = "Client-" + (clientnum + 1) + " has connected." + "\n";
				input.append(hint);

				DataInputStream dis = new DataInputStream(client.getInputStream());
				DataOutputStream dos = new DataOutputStream(client.getOutputStream());

				allclient[clientnum] = new Client(clientnum, input, dis, dos);
				allclient[clientnum].start();
				clientnum++;
			}

		} catch (IOException e) {
		}
	}
}


/**
 * ��������ʵ�ֿͻ�������ת�����߳��ࡣ
 * @author Brisk
 *
 */
class Client extends Thread
{
	int id;
	TextArea clientDisplay = new TextArea(5,30);
	DataOutputStream dos;
	DataInputStream dis;
	
	/**
	 * ���췽������ȡת�������������
	 * @param id �ͻ��˱�ţ��˴��ɳ����Լ��趨��
	 * @param display ��ʾ�ͻ����������ݵ��ı���
	 * @param dis ���տͻ������ݵ���������
	 * @param dos ���͵��ͻ������ݵ��������
	 */
	public Client(int id,TextArea display, DataInputStream dis, DataOutputStream dos)
	{
		this.id = id + 1;
		clientDisplay = display;
		this.dis = dis;
		this.dos = dos;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				String message = dis.readUTF();  //��һ���������������ݡ�
				SimpleTalkServer.hint = "Message from Client-" + id + " as following:\n" + message + "\n";  //�ڷ�����������ʾ���ݡ�

				clientDisplay.append(SimpleTalkServer.hint);
				
				int m = SimpleTalkServer.clientnum;
				
				for(int i=0; i<m; i++)
				{
					SimpleTalkServer.allclient[i].dos.writeUTF(message);  //ת����Ϣ����һ���ͻ��ˡ�
				}
			}
			catch(IOException e)
			{}
		}
	}
}

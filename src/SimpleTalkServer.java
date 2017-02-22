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
 * 基于Socket通信方式的简易聊天软件服务器端程序。
 * @author Brisk
 * Socket通信方式演示范例。
 */
public class SimpleTalkServer
{
	/**
	 * 定义成员变量。
	 */
	public static Client[] allclient = new Client[20];
	public static int clientnum = 0;
	public static String hint = "";

	/**
	 * 服务器端程序的主方法，不需要输入参数。IP是localhost，端口是8888。
	 * @param args 未使用输入参数。
	 */
	public static void main(String[] args)
	{
		Frame show = new Frame("Server");
		Panel p = new Panel();
		TextArea input = new TextArea(15, 35);
		
		try {
			ServerSocket server = new ServerSocket(8888);  //创建Socket链接。
			hint = "Server is operating.\n";

			input.setText(hint);
			p.add(input);
			show.add(p);
			
			/**
			 * 设置服务器端的窗口监听器，用来处理点击窗口关闭按钮时的操作。
			 */
			show.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					try {
						String message = new String("Server closed.\n");

						for (int i = 0; i < clientnum; i++) {
							SimpleTalkServer.allclient[i].dos.writeUTF(message);  //利用DataOutputStream对象转发收到的消息。
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
				Socket client = server.accept();  //利用阻塞方式监听socket链接所在端口，接收数据。
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
 * 服务器端实现客户端内容转发的线程类。
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
	 * 构造方法，获取转发所需的流对象。
	 * @param id 客户端编号，此处由程序自己设定。
	 * @param display 显示客户端往来数据的文本域。
	 * @param dis 接收客户端数据的输入流。
	 * @param dos 发送到客户端数据的输出流。
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
				String message = dis.readUTF();  //从一个服务器接收数据。
				SimpleTalkServer.hint = "Message from Client-" + id + " as following:\n" + message + "\n";  //在服务器窗口显示数据。

				clientDisplay.append(SimpleTalkServer.hint);
				
				int m = SimpleTalkServer.clientnum;
				
				for(int i=0; i<m; i++)
				{
					SimpleTalkServer.allclient[i].dos.writeUTF(message);  //转发消息到另一个客户端。
				}
			}
			catch(IOException e)
			{}
		}
	}
}

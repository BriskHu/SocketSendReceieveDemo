import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 基于Socket通信方式的简易聊天软件的客户端程序。
 * @author Brisk
 * Socket通信方式演示范例。
 */
public class SimpleTalkClient 
{
	/**
	 * 客户端程序的主程序，需要在运行时传入一个参数：客户编号。
	 * @param args 客户编号。由使用者自己设定值。
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		String IP = "139.199.193.77";
		int PORT = 8888;

		if(args.length==3)
		{
			IP = args[1];
			PORT = Integer.parseInt(args[2]);
		}
		else if(args.length==1)
		{
			IP = "139.199.193.77";
			PORT = 8888;
		}
		else
		{
			System.out.println("输入参数个数错误！\n用法：java SimpleTalkClient 自定义的客户编号 [服务器地址 服务器端口号]\n不指定服务器地址和端口号，则使用默认值。默认服务器地址为：139.199.193.77\t默认端口号为：8888");
			System.exit(-1);
		}		

		Socket client = new Socket(IP, PORT);
		DataInputStream dis = new DataInputStream(client.getInputStream());
		final DataOutputStream dos = new DataOutputStream(client.getOutputStream());
		
		Frame show = new Frame("Client-" + args[0]);
		Panel p = new Panel();
		final TextField input = new TextField(34);
		TextArea display = new TextArea(13,35);
		
		p.add(input);
		p.add(display);
		show.add(p);
		
		new receiveThread(dis, display);
		
		/**
		 * 设置输入文本框的输入动作处理程序的监听器。
		 */
		input.addActionListener(new ActionListener()
				{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String message = new String("Client-" + args[0] + ":" +input.getText());
					dos.writeUTF(message); // 利用DataOutputStream对象将输入的信息发送出去。
					display.append("You have send:\n" + message + "\n");
				}
				catch(IOException exception)
				{}
			}
				}
				);
	
		/**
		 * 监听客户端关闭动作的窗口监听器。
		 */
		show.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent w) 
			{
				try
				{
					dos.writeUTF("Client-" + args[0] + ":" + "closed.");
					client.close();  //关闭socket。
				}
				catch(IOException e)
				{}
				
				w.getWindow().dispose();
				System.exit(0);
			}
		});
		
		show.repaint();
		show.setSize(300, 300);
		show.setVisible(true);
	}
}

/**
 * 接收线程类，用来接收socket传过来的数据，并显示在文本域里。
 * @author Brisk
 *
 */
class receiveThread extends Thread
{
	/**
	 * 成员变量定义。
	 */
	DataInputStream dis;
	TextArea message;
	
	public receiveThread(DataInputStream dis, TextArea mm)
	{
		this.dis = dis;
		message = mm;
		this.start();
	}
	
	public void run()
	{
		for(;;)
		{
			try
			{
				String str = new String("You have received:\n" + dis.readUTF() + "\n");
				message.append(str);  // 在文本域中显示接收到的数据。
			}
			catch(IOException e)
			{}
		}
	}
}

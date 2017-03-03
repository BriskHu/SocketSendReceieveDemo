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
 * ����Socketͨ�ŷ�ʽ�ļ�����������Ŀͻ��˳���
 * @author Brisk
 * Socketͨ�ŷ�ʽ��ʾ������
 */
public class SimpleTalkClient 
{
	/**
	 * �ͻ��˳������������Ҫ������ʱ����һ���������ͻ���š�
	 * @param args �ͻ���š���ʹ�����Լ��趨ֵ��
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
			System.out.println("���������������\n�÷���java SimpleTalkClient �Զ���Ŀͻ���� [��������ַ �������˿ں�]\n��ָ����������ַ�Ͷ˿ںţ���ʹ��Ĭ��ֵ��Ĭ�Ϸ�������ַΪ��139.199.193.77\tĬ�϶˿ں�Ϊ��8888");
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
		 * ���������ı�������붯���������ļ�������
		 */
		input.addActionListener(new ActionListener()
				{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					String message = new String("Client-" + args[0] + ":" +input.getText());
					dos.writeUTF(message); // ����DataOutputStream�����������Ϣ���ͳ�ȥ��
					display.append("You have send:\n" + message + "\n");
				}
				catch(IOException exception)
				{}
			}
				}
				);
	
		/**
		 * �����ͻ��˹رն����Ĵ��ڼ�������
		 */
		show.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent w) 
			{
				try
				{
					dos.writeUTF("Client-" + args[0] + ":" + "closed.");
					client.close();  //�ر�socket��
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
 * �����߳��࣬��������socket�����������ݣ�����ʾ���ı����
 * @author Brisk
 *
 */
class receiveThread extends Thread
{
	/**
	 * ��Ա�������塣
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
				message.append(str);  // ���ı�������ʾ���յ������ݡ�
			}
			catch(IOException e)
			{}
		}
	}
}

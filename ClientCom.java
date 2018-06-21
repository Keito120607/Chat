import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientCom extends Thread {
	private static String HOST = "localhost";
	private static int PORT = 3456;
	static Socket socket = null;
	private static String host = null;

	public ClientCom() {
		try {
			socket = new Socket(host, PORT);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			byte[] buf = new byte[1024];
			int size;

			while ((size = socket.getInputStream().read(buf)) > 0) {
				String str = new String(buf, 0, size, "UTF-8");
				if (str.charAt(0) == '0') {
					Client_Talkroom.infoview.append(str.substring(1) + "\n");
				} else {
					Client_Talkroom.view.append(str.substring(1) + "\n");
					Client_Talkroom.textArea.append("");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("READ THREAD SHUTDOWN");

	}

	public static void portSet(int p) {
		PORT = p;
	}

	public static void  hostSet(String h){
		host =h;
	}
}


  import java.net.InetAddress;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
  
  
public abstract class ClientStub extends Thread{
  private static String HOST = "localhost";
  private static int PORT = 3456;
	private Socket socket = null;
	   private InetAddress host = null;

  

  public ClientStub(){

  	try {
      
      host = InetAddress.getByName("10.24.93.213");
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
        onMessage(new String(buf, 0, size, "UTF-8"));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    System.out.println("READ THREAD SHUTDOWN");
  }
  
  public void write(String str) throws IOException {
    socket.getOutputStream().write(str.getBytes("UTF-8"));
  }
  
  protected abstract void onMessage(String str);
}
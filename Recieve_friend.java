import java.util.*;
import java.net.*;
import java.io.*;

public class Recieve_friend extends Server{
    //メンバー承認のポート番号取得
    public static int PORT1 = 3456;
    //何番目に追加されたメンバーなのかを示すcount
    static int count = 0;
    public static Map<Integer,String> map= new HashMap<Integer,String>();

    public ServerSocket s1;
    Server server1;

    public void accept_friend() throws IOException{
        s1 = new ServerSocket(PORT1);
        try{
            Socket socket = s1.accept();
            try{
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                System.out.println("Recieve_accepted start");
                String name = in.readLine();
                System.out.println(name);
                map.put(count, name);

            }finally {
                System.out.println("closing...");
                socket.close();
            }
        }finally {
            s1.close();
        }

    }

}

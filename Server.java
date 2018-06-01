import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static int all_PORT = 8000;

    public static void main(String[] args) throws IOException{
        //クライアントとサーバーの大元の接続に必要なサーバーソケットを生成
        ServerSocket s = new ServerSocket(8000);
        try{
            //クライアントからの接続をまつ
            Socket socket = s.accept();
            try{
                System.out.println("first Connection accepted: " + s);
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                //クライアントからの要求を読み取る
                System.out.println(0);
                String name = in.readLine();
                System.out.println(name);
                System.out.println(1);
                if (name.equals("Recieve_friend")) {
                    System.out.println(4);
                    out.println("1");
                    Recieve_friend r = new Recieve_friend();
                    r.accept_friend();
                }

            }finally {
                System.out.println("closing2...");
                socket.close();
            }
        }finally {
            s.close();
        }
    }

}

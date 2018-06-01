import java.util.*;
import java.net.*;
import java.io.*;

public class Client{

    public static void  main(String[] args) throws IOException{
        //サーバーと通信するためのポート番号を指定
        int PORT = Server.all_PORT;
        InetAddress addr =
                InetAddress.getByName("localhost");
        Socket soc = new Socket(addr, PORT);

        try {
            System.out.println("socket = " + soc);


            BufferedReader in =
                    new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter out =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())),true);

            //アカウント名をサーバーに送信
            out.println("Recieve_friend");
            String p = in.readLine();
            System.out.println("Recieve_friend accepted");
            if (p.equals("1")) {
                System.out.println(2);
                Request_friend r1 = new Request_friend("山本一貴");
                r1.send_friend();
            }


        } finally {
            System.out.println("closing1...");
            soc.close();
        }


    }


}

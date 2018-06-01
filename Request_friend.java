import java.io.*;
import java.util.*;
import java.net.*;

public class Request_friend extends Client{
    //ユーザー名を定義
    static String myname;
    Request_friend(String myname){
        this.myname = myname;
    }
    // ホスト名
    private static String HOST_name = "localhost";
    //サーバーからポート番号割り当て
    //ポート番号
    private static int PORT1 = 3456;
    //ソケット
    private Socket socket = null;

    //ホスト名，ポート番号とユーザー名をセットとしてサーバー側に送信し友達申請
    public void send_friend() throws IOException{
        //ソケットインスタンスを生成し，通信開始
        socket = new Socket(HOST_name, PORT1);

        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
            //アカウント名をサーバーに送信
            out.println(myname);
        } finally {
            System.out.println("closing...");
            socket.close();
        }

    }




}

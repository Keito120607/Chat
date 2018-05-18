
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
import sun.misc.HexDumpEncoder;
 
public class SelectServer extends Thread {
  private static int PORT = 3456;
  private static int BUF_SIZE = 1024;
 
  private List<SocketChannel> channelList = new LinkedList<SocketChannel>();
 
  private Map<SocketChannel, ByteArrayOutputStream> bufferMap = new HashMap<SocketChannel, ByteArrayOutputStream>();
 
  private Selector selector = null;
 
  public static void main(String[] args) {
    new SelectServer().start();
  }
 
  @Override
  public void run() {
    ServerSocketChannel serverChannel = null;
 
    try {
      selector = Selector.open();
      serverChannel = ServerSocketChannel.open();
      serverChannel.configureBlocking(false);
      serverChannel.socket().bind(new InetSocketAddress(PORT));
      serverChannel.register(selector, SelectionKey.OP_ACCEPT);
 
      System.out.println("DAEMON WAKEUP!");
 
      while (selector.select() > 0) {
        Iterator<SelectionKey> keyIt = selector.selectedKeys()
            .iterator();
 
        while (keyIt.hasNext()) {
          SelectionKey key = keyIt.next();
          keyIt.remove();
 
          if (key.isAcceptable()) {
            doAccept((ServerSocketChannel) key.channel());
          } else if (key.isReadable()) {
            doRead((SocketChannel) key.channel());
          } else if (key.isWritable()) {
            doWrite((SocketChannel) key.channel());
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        System.out.println("DAEMON SHUTDOWN!");
        serverChannel.close();
      } catch (IOException ignoreEx) {
        ignoreEx = null;
      }
    }
  }
 
  private void doAccept(ServerSocketChannel daemonChannel) {
    try {
      SocketChannel channel = daemonChannel.accept();
      System.out.println("ACCEPT " + channel);
      channel.configureBlocking(false);
 
      // �~�~ OP_WRITE ��Ď��Ώۂɂ���� CPU���p����100%�ɂȂ� �~�~
      // �������ރ��b�Z�[�W������Ƃ������A���̃`�����l���� OP_WRITE
      // ��Ď�����B
      // channel.register(selector,
      // SelectionKey.OP_READ + SelectionKey.OP_WRITE);
 
      channel.register(selector, SelectionKey.OP_READ);
 
      channelList.add(channel);
 
      String remoteAddr = channel.socket().getRemoteSocketAddress()
          .toString();
      System.out.println("Connected:" + remoteAddr);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
 
  private void doRead(SocketChannel channel) {
    try {
      String remoteAddr = channel.socket().getRemoteSocketAddress()
          .toString();
 
      ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
 
      if (channel.read(buf) > 0) {
        buf.flip();
 
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);
 
        System.out.println("Message From:" + remoteAddr);
 
        HexDumpEncoder hex = new HexDumpEncoder();
        System.out.println(hex.encode(bytes));
 
        for (SocketChannel client : channelList) {
          System.out.println("SEND:" + client);
 
          // �~�~ ������ Channel �� write() ������_�� �~�~
          // client.write(ByteBuffer.wrap(bytes));
 
          // �� �����ł́ABuffer �ɒ��߂Ă����āAChannel �� writable
          // �� �ɂȂ�����A�����o���B
          ByteArrayOutputStream bout = bufferMap.get(client);
          if (bout == null) {
            bout = new ByteArrayOutputStream();
            bufferMap.put(client, bout);
          }
          bout.write(bytes);
 
          // ����`�����l���� Writable �ɂȂ�̂�Ď�����
          client.register(selector, SelectionKey.OP_WRITE);
        }
      }
    } catch (Exception e) {
      // Socket���ؒf���ꂽ
      logout(channel);
    }
  }
 
  private void doWrite(SocketChannel channel) {
    ByteArrayOutputStream bout = bufferMap.get(channel);
    if (bout != null) {
      System.out.println("WRITE CHANNEL");
      try {
        ByteBuffer bbuf = ByteBuffer.wrap(bout.toByteArray());
        int size = channel.write(bbuf);
 
        System.out.println("SEND " + size + "/" + bbuf.limit());
 
        if (bbuf.hasRemaining()) {
          // bbuf��ׂĂ𑗐M������Ȃ������̂ŁA�c���bufferMap�ɏ����߂�
          ByteArrayOutputStream rest = new ByteArrayOutputStream();
          rest.write(bbuf.array(), bbuf.position(), bbuf.remaining());
          bufferMap.put(channel, rest);
          // ����`�����l���� Writable �ɂȂ�̂�Ď���������B
          // ����`�����l�����ؒf���ꂽ���Ƃ�m���邽�߂� Readable �̊Ď���s��
          channel.register(selector, SelectionKey.OP_READ
              + SelectionKey.OP_WRITE);
        } else {
          // bbuf��ׂđ��M���I������̂ŁAbufferMap���񑗐M����폜����
          bufferMap.remove(channel);
          // ����`�����l���� Writable �ɂȂ�̂�Ď�����̂��߂�
          channel.register(selector, SelectionKey.OP_READ);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
 
  private void logout(SocketChannel channel) {
    String remoteAddr = channel.socket().getRemoteSocketAddress()
        .toString();
    System.out.println("LOGOUT :" + remoteAddr);
 
    try {
      channel.finishConnect();
      channel.close();
 
      if (channelList.remove(channel)) {
        System.out.println("A CLIENT REMOVED FROM THE CLIENT LIST");
      } else {
        System.out
            .println("FAILED TO REMOVE A CLIENT FROM THE CLIENT LIST");
      }
    } catch (Exception ignoreEx) {
      System.out.println("CHANNEL CLOSE FAILED");
      ignoreEx.printStackTrace();
      ignoreEx = null;
 
      return;
    }
 
    System.out.println("CHANNEL CLOSE SUCCESS");
  }
}
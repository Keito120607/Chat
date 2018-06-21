import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sun.misc.HexDumpEncoder;

public class SelectServer2 extends Thread {
	private static int PORT = 0;
	private static int BUF_SIZE = 1024;

	private List<SocketChannel> channelList = new LinkedList<SocketChannel>();

	private Map<SocketChannel, ByteArrayOutputStream> bufferMap = new HashMap<SocketChannel, ByteArrayOutputStream>();

	private Selector selector = null;

	public List<String> Talklog = new ArrayList<String>();

	public List<String> Infolist = new ArrayList<String>();

	public static void main(String[] args) {
		portSet(Integer.parseInt(args[0]));
		new SelectServer2().start();
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
				Iterator<SelectionKey> keyIt = selector.selectedKeys().iterator();

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

	private void sendlog(SocketChannel channel, int i) {
		if (Talklog.get(i) != null) {
			try {
				ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

				buf.flip();

				byte[] bytes = new byte[buf.limit()];
				buf.get(bytes);

				HexDumpEncoder hex = new HexDumpEncoder();
				if (i == 0) {
					channel.write(ByteBuffer.wrap(("1" + Talklog.get(i)).getBytes()));
				} else {
					channel.write(ByteBuffer.wrap(Talklog.get(i).getBytes()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendrdinfo(SocketChannel channel) {
		Random rand = new Random();
		int i = rand.nextInt(Infolist.size());
		if (Infolist.get(i) != null) {
			try {
				ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

				buf.flip();

				byte[] bytes = new byte[buf.limit()];
				buf.get(bytes);

				channel.write(ByteBuffer.wrap(Infolist.get(i).getBytes()));
				channel.register(selector, SelectionKey.OP_WRITE);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doAccept(ServerSocketChannel daemonChannel) {
		try {
			SocketChannel channel = daemonChannel.accept();
			System.out.println("ACCEPT " + channel);
			channel.configureBlocking(false);

			// ×× OP_WRITE を監視対象にすると CPU利用率が100%になる ××
			// 書き込むメッセージがあるときだけ、そのチャンネルの OP_WRITE
			// を監視する。
			// channel.register(selector,
			// SelectionKey.OP_READ + SelectionKey.OP_WRITE);

			if (Talklog != null) {
				for (int i = 0; i < Talklog.size(); i++) {
					sendlog(channel, i);
				}
			}

			channel.register(selector, SelectionKey.OP_READ);

			channelList.add(channel);

			String remoteAddr = channel.socket().getRemoteSocketAddress().toString();
			System.out.println("Connected:" + remoteAddr);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void doRead(SocketChannel channel) {
		try {
			String remoteAddr = channel.socket().getRemoteSocketAddress().toString();

			ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);

			int size;

			if ((size = channel.read(buf)) > 0) {
				buf.flip();

				byte[] bytes = new byte[buf.limit()];
				buf.get(bytes);

				System.out.println("Message From:" + remoteAddr);

				HexDumpEncoder hex = new HexDumpEncoder();
				System.out.println(hex.encode(bytes));

				String str = new String(bytes, 0, size, "UTF-8");

				if (str.charAt(0) == '1') {
					for (SocketChannel client : channelList) {
						System.out.println("SEND:" + client);

						// ×× ここで Channel に write() しちゃダメ ××
						// client.write(ByteBuffer.wrap(bytes));

						// ※ ここでは、Buffer に貯めておいて、Channel が writable
						// ※ になったら、書き出す。
						ByteArrayOutputStream bout = bufferMap.get(client);
						if (bout == null) {
							bout = new ByteArrayOutputStream();
							bufferMap.put(client, bout);
						}
						bout.write(bytes);

						// 宛先チャンネルが Writable になるのを監視する
						client.register(selector, SelectionKey.OP_WRITE);
					}
				} else if (str.charAt(0) == '0') {
					Infolist.add(str);

				} else if (str.charAt(0) == '2') {
					sendrdinfo(channel);
					sleep(100);
					for (SocketChannel client : channelList) {
						System.out.println("SEND:" + client);

						// ×× ここで Channel に write() しちゃダメ ××
						// client.write(ByteBuffer.wrap(bytes));

						// ※ ここでは、Buffer に貯めておいて、Channel が writable
						// ※ になったら、書き出す。
						ByteArrayOutputStream bout = bufferMap.get(client);
						if (bout == null) {
							bout = new ByteArrayOutputStream();
							bufferMap.put(client, bout);
						}
						bout.write(bytes);

						// 宛先チャンネルが Writable になるのを監視する
						client.register(selector, SelectionKey.OP_WRITE);
					}
				}

			}
		} catch (Exception e) {
			// Socketが切断された
			logout(channel);
		}
	}

	private void doWrite(SocketChannel channel) {
		ByteArrayOutputStream bout = bufferMap.get(channel);
		if (bout != null) {
			System.out.println("WRITE CHANNEL");
			Talklog.add("(log)" + (bout.toString().substring(1)) + "\n");
			try {
				ByteBuffer bbuf = ByteBuffer.wrap(bout.toByteArray());
				int size = channel.write(bbuf);

				System.out.println("SEND " + size + "/" + bbuf.limit());

				if (bbuf.hasRemaining()) {
					// bbufをすべてを送信しきれなかったので、残りをbufferMapに書き戻す
					ByteArrayOutputStream rest = new ByteArrayOutputStream();
					rest.write(bbuf.array(), bbuf.position(), bbuf.remaining());
					bufferMap.put(channel, rest);
					// 宛先チャンネルが Writable になるのを監視し続ける。
					// 宛先チャンネルが切断されたことを検知するために Readable の監視も行う
					channel.register(selector, SelectionKey.OP_READ + SelectionKey.OP_WRITE);
				} else {
					// bbufをすべて送信し終わったので、bufferMap今回送信分を削除する
					bufferMap.remove(channel);
					// 宛先チャンネルが Writable になるのを監視するのをやめる
					channel.register(selector, SelectionKey.OP_READ);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void logout(SocketChannel channel) {
		String remoteAddr = channel.socket().getRemoteSocketAddress().toString();
		System.out.println("LOGOUT :" + remoteAddr);

		try {
			channel.finishConnect();
			channel.close();

			if (channelList.remove(channel)) {
				System.out.println("A CLIENT REMOVED FROM THE CLIENT LIST");
			} else {
				System.out.println("FAILED TO REMOVE A CLIENT FROM THE CLIENT LIST");
			}
		} catch (Exception ignoreEx) {
			System.out.println("CHANNEL CLOSE FAILED");
			ignoreEx.printStackTrace();
			ignoreEx = null;

			return;
		}

		System.out.println("CHANNEL CLOSE SUCCESS");
	}
	public static void portSet(int a){
		PORT = a;
		return;
	}
}
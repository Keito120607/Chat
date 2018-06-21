import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by kazu on 2018/06/07.
 */
public class Client_Talkroom extends JFrame implements ActionListener {
	public static JPanel panel = new JPanel();
	public static JTextArea textArea;
	public static JTextArea view;
	public static JTextArea infoview;
	public static ClientCom clientCom = new ClientCom();

	static String name = "�����Ȃ��w��";
	static String info = "";
	static int count = 0;

	Client_Talkroom(JPanel p) {
		this.panel = p;
		set_talkroom();
		this.clientCom.start();
	}

	public void set_talkroom() {
		/*
		 * textArea���s���A�񐔂�ݒ肵�Đݒu
		 */
		textArea = new JTextArea(5, 10);
		// setLineWrap���\�b�h�ɂ��A�s�̐܂�Ԃ����\�Ƃ���
		textArea.setLineWrap(true);
		// �X�N���[���o�[���܂�textArea�C���X�^���X����
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// ���M�{�^��
		JButton button = new JButton("���M");
		button.setText("���M");
		button.addActionListener(this);
		button.setActionCommand("send");

		// JTextArea�̃g�[�N���[������
		view = new JTextArea();
		view.setLineWrap(true);
		view.setEditable(false);
		JScrollPane talk_panel = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		infoview = new JTextArea();
		infoview.setLineWrap(true);
		infoview.setEditable(false);
		JScrollPane infoscroll = new JScrollPane(infoview, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		panel.setLayout(null);

		talk_panel.setBounds(0, 0, 700, 200);
		scrollPane.setBounds(0, 200, 500, 100);
		button.setBounds(500, 200, 75, 100);
		infoscroll.setBounds(0, 300, 700, 200);

		panel.add(scrollPane);
		panel.add(button);

		panel.add(talk_panel);
		panel.add(infoscroll);

	}

	public void send_info() {
		/*
		 * �����ȏ����T�[�o�[�ɑ��M
		 */
		try {
			this.clientCom.socket.getOutputStream().write(info.getBytes("UTF-8"));
			// this.clientCom.start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("send")) {
			String str = new String();
			if (count == 4) {
				str = "2" + this.name + ":" + textArea.getText();
				count = -1;
			} else {
				str = "1" + this.name + ":" + textArea.getText();
			}
			System.out.println(str);
			count++;
			try {
				this.clientCom.socket.getOutputStream().write(str.getBytes("UTF-8"));
				this.clientCom.start();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			textArea.setText("");

		}
	}

}

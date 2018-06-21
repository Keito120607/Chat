import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Created by kazu on 2018/06/08.
 */
public class Username extends JFrame implements ActionListener {
	public JPanel all_panel = new JPanel();
	public JPanel name_panel = new JPanel();
	public JPanel talk_panel = new JPanel();
	public JTextArea name_text;
	public JTextArea info_text = new JTextArea(5, 10);

	Username(JPanel all, JPanel p) {
		all_panel = all;
		this.name_panel = p;
		set_username();
	}

	public void set_username() {
		name_panel.setLayout(null);

		JLabel label1 = new JLabel("User_name");
		label1.setBounds(0, 0, 100, 50);
		name_panel.add(label1);
		name_text = new JTextArea();
		name_text.setBounds(100, 0, 400, 50);
		name_panel.add(name_text);

		JLabel label2 = new JLabel("Ç®ìæÇ»èÓïÒÇ1Ç¬ì¸óÕÇµÇƒÇ≠ÇæÇ≥Ç¢");
		label2.setBounds(0, 130, 300, 50);
		name_panel.add(label2);
		info_text.setLineWrap(true);

		JScrollPane info_scroll = new JScrollPane(info_text, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		info_scroll.setBounds(0, 200, 500, 100);
		name_panel.add(info_scroll);

		JButton button = new JButton("éüÇ÷");
		button.addActionListener(this);
		button.setActionCommand("next");
		button.setBounds(500, 200, 100, 100);
		name_panel.add(button);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ClientUI.layout.show(all_panel, "èÓïÒóùçHäwâ»");
		Client_Talkroom talkroom = new Client_Talkroom(ClientUI.view2);

		if (e.getActionCommand().equals("next")) {

			Client_Talkroom.name = name_text.getText();
			talkroom.info = "0" + info_text.getText();
			talkroom.send_info();
			
		}

	}
}
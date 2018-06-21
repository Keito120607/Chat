import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.net.*;
import javax.swing.*;

class ClientUI extends JFrame implements ActionListener{
    JFrame frame = new JFrame("**Chat Application**");
    JPanel p = new JPanel();
    static CardLayout layout = new CardLayout();
    JPanel view0 = new JPanel();
    JPanel view1 = new JPanel();
    static JPanel view2 = new JPanel();

    ClientUI(){

        frame.setBounds(0,0,600,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.getContentPane().add(p);

        //CardLayout�����ɐݒ�
        p.setLayout(layout);

        p.add(view0,"�O���[�v");
        p.add(view1,"���[�U�ݒ�");
        p.add(view2,"��񗝍H�w��");

        layout.show(p,"�O���[�v");

    	view0.setLayout(null);
        JButton button1 = new JButton("��񗝍H�w��");
        button1.setBounds(0,100,200,50);
    	button1.addActionListener(this);
    	view0.add(button1);
		button1.setActionCommand("ce");
        
    	
    	JButton button2 = new JButton("���ʐM�w��");
        button2.setBounds(0,200,200,50);
    	button2.addActionListener(this);
		button2.setActionCommand("cs");
        view0.add(button2);
    	
    	JButton button3 = new JButton("��񗝍H&���ʐM�w��");
        button3.setBounds(0,300,200,50);
    	button3.addActionListener(this);
		button3.setActionCommand("csce");
        view0.add(button3);

    }

    @Override
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        
    	if (e.getActionCommand().equals("ce"))ClientCom.portSet(3456);
    	else if (e.getActionCommand().equals("cs"))ClientCom.portSet(3456);
    	else if (e.getActionCommand().equals("csce"))ClientCom.portSet(3456);
    	
        layout.show(p,"���[�U�ݒ�");
        Username userroom = new Username(p,view1);
	}

    public static void main(String[] args){
    	ClientCom.hostSet(args[0]);
        ClientUI setup = new ClientUI();
    }

}

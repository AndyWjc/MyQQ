package qq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.*;

public class QQMain extends JFrame implements ActionListener, Runnable, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4788045076696120037L;
	
	private Socket s;
	public void setSocket(Socket value){
		s = value;
		//Start Thread
		Thread t = new Thread(this);
		t.start();
	}
	
	JTextField txtMess = new JTextField();
	JComboBox<String> cmbUser = new JComboBox<String>();
	JTextArea txtContent = new JTextArea();
	QQMain(){
		this.setSize(300, 400);
		
		//new component 
		JButton btnSend = new JButton("Send");
		//set scroll bar
		JScrollPane spContent = new JScrollPane(txtContent);
		
		//Register Event Listener
		btnSend.addActionListener(this);

		//Layout the small panel
		JPanel panSmall = new JPanel();
		panSmall.setLayout(new GridLayout(1 , 2));
		
		panSmall.add(cmbUser);
		panSmall.add(btnSend);
		
		//Layout big panel
		JPanel panBig = new JPanel();
		panBig.setLayout(new GridLayout(2,1));
		
		panBig.add(txtMess);
		panBig.add(panSmall);
		
		//Layout window
		this.setLayout(new BorderLayout());
		
		this.add(panBig ,BorderLayout.NORTH);
		this.add(spContent , BorderLayout.CENTER);
		
		//load chat log
		try{
			File f = new File("f:/qqwork/chatlog.qq");
			
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			
			while(br.ready()){
				txtContent.append(br.readLine()+"\n");
			}
		}catch(Exception e1){}
		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//txtMess ---------> txtContent
		txtContent.append(QQLogin.getUser()+":"+txtMess.getText()+"\n");
		//save txtMess to file
		try{
			File f = new File("f:/qqwork/chatlog.qq");
			
			FileWriter fw = new FileWriter(f , true);
			PrintWriter pw = new PrintWriter(fw);
			
			pw.println(QQLogin.getUser()+":"+txtMess.getText());
			
			pw.close();
		}catch(Exception e1){}
		
		//Send information to the server
		try{
			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw,true);
			
			pw.println(cmbUser.getSelectedItem()+"%"+QQLogin.getUser()+":"+txtMess.getText()) ;
		}catch(Exception e1){}
		
		//clear txtMess
		txtMess.setText("");
	}
	//接收线程
	public void run() {
		try{
			InputStream is = s.getInputStream() ;
			InputStreamReader isr = new InputStreamReader(is) ;
			BufferedReader br = new BufferedReader(isr) ;
			
			while(true){
				String message = br.readLine() ;
				String type = message.split("%")[0] ;
				String mess = message.split("%")[1] ;
				if(type.equals("add")){
					cmbUser.addItem(mess) ;
				}
				if(type.equals("exit")){
					cmbUser.removeItem(mess) ;
				}
				if(type.equals("mess")){
					txtContent.append(mess+"\n") ;
				}
			}
		}catch(Exception e){}
	}
	
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		try{
			OutputStream os = s.getOutputStream() ;
			OutputStreamWriter osw = new OutputStreamWriter(os) ;
			PrintWriter pw = new PrintWriter(osw , true) ;
			
			pw.println("{exit}") ;

			//正常退出
			System.exit(0) ;
		}catch(Exception ex){}
	}
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

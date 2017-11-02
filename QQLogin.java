package qq;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import javax.swing.*;

public class QQLogin extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6321584490400025358L;
	JTextField txtUser = new JTextField();
	JPasswordField txtPass = new JPasswordField();
	
	private static String user;
	public static String getUser() {
		return user;
	}
	
	public  QQLogin(){
		this.setSize(250,125);

		//new Component
		JLabel labUser = new JLabel("Username");
		JLabel labPass = new JLabel("Password");
		
		//Login Registration Cancel
		JButton btnLogin = new JButton("Login");
		JButton btnReg = new JButton("Regiser");
		JButton btnCansel = new JButton("Cansel");
		
		//Register Event Listener
		//QQLogin e = new QQLogin();
		btnLogin.addActionListener(this);
		btnReg.addActionListener(this);
		btnCansel.addActionListener(this);
		
		//set input Layout
		JPanel panInput = new JPanel();
		panInput.setLayout(new GridLayout(2 , 2));
		panInput.add(labUser);
		panInput.add(txtUser);
		panInput.add(labPass);
		panInput.add(txtPass);

		//set button Layout
		JPanel panButton = new JPanel();
		panButton.setLayout(new FlowLayout());
		panButton.add(btnLogin);
		panButton.add(btnReg);
		panButton.add(btnCansel);
		
		//set windows Layout
		this.setLayout(new BorderLayout());
		
		this.add(panInput , BorderLayout.CENTER);
		this.add(panButton , BorderLayout.SOUTH);
	}
	
	public static void main(String[] agrs){
		QQLogin w = new QQLogin() ;
		w.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals("Login")){
			try{
				//Send the user name and password to the server side
				user = txtUser.getText() ;
				//String pass = txtPass.getText() ;
				char[] pass = txtPass.getPassword() ;
				String password = new String(pass);
				Socket s =new Socket(InetAddress.getLocalHost(),8000);
				//Socket s =new Socket("123.23.23.5",8000);
				//Socket s =new Socket("192.168.42.59",8000);
				
				OutputStream os = s.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				PrintWriter pw = new PrintWriter(osw,true);
				
				pw.println(user+"%"+password);
				
				//Accept the server to send back the confirmation message
				InputStream is =s.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				String yorn = br.readLine();
				
				if(yorn.equals("ok")){
					QQMain w = new QQMain();
					w.setSocket(s);
					w.setVisible(true);
					this.setVisible(false);
				}else{
					JOptionPane.showMessageDialog(this, "Sorry, username or passord is wrong");
				}
			}catch(Exception  e1){}	
		}
		if(e.getActionCommand().equals("Regiser")){
			System.out.println("The user click on Regiser");
		}
		if(e.getActionCommand().equals("Cansel")){
			System.out.println("The user click on Cansel");
		}
	}
}

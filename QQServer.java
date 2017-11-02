package qq;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;

public class QQServer {

	public static void main(String[] args) throws Exception{
		//Declare the set of Socket that keeps everyone
		HashMap<String,Socket> hm=new HashMap<String,Socket>();
		
		//Server variables 
		ServerSocket ss = null ;
		Socket s = null ;
		try{
			//The server listens on the 8000 port
			ss = new ServerSocket(8000,20,InetAddress.getLocalHost());
			//System.out.println(ss.getInetAddress());
			System.out.println(ss.getLocalSocketAddress());
			//System.out.println(ss.isClosed());
			
			while(true){
				System.out.println("The server is listening on the 8000 port ......");
				s = ss.accept();
				
				MyService t = new MyService();
				t.setSocket(s);
				//��HashMap�����ô�������߳�
				t.setHashMap(hm) ;
				t.start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				ss.close();
				s.close();
			}catch (Exception e) {}
		}
	}
}
class MyService extends Thread {
	private Socket s;
	public void setSocket(Socket s){
		this.s = s ;
	}
	//����HashMap������
	private HashMap<String , Socket> hm ;
	public void setHashMap(HashMap<String, Socket> hm){
		this.hm = hm ;
	}
	Connection cn=null;
	PreparedStatement ps=null;
	ResultSet rs=null;
	public void run() {
		try{
			// �����û���������
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
	
			String uandp = br.readLine();
	
			// ����û���������
			String u = uandp.split("%")[0];
			String p = uandp.split("%")[1];
	
			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw, true);
			
			//�����ݿ�����֤�û����
			Class.forName("org.gjt.mm.mysql.Driver") ;
			cn = DriverManager.getConnection("jdbc:mysql://120.78.139.224:3306/qq","root","123456") ;
			ps = cn.prepareStatement("select * from user where username=? and password=?") ;
			ps.setString(1, u) ;
			ps.setString(2, p) ;
			
			rs = ps.executeQuery() ;
			
			if (rs.next()) {
				// ������ȷ��Ϣ���ͻ���
				pw.println("ok");
	
				//�����˵����ַ��͸��������û�
				for(Socket ts : hm.values()){
					OutputStream tos = ts.getOutputStream() ;
					OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
					PrintWriter tpw = new PrintWriter(tosw , true) ;
								
					tpw.println("add%"+u) ;
				}
							
				//�������˵����ַ��͸��Լ�
				for(String tu : hm.keySet()){
					pw.println("add%"+tu) ;
				}
	
				//�����˵��û�����Socket����HashMap
				hm.put(u, s) ;
				
				//���ϵؽ��տͻ��˷��͹�������Ϣ
				while (true) {
					String message = br.readLine();
					if(message.equals("{exit}")){
						//�����û���HashMap��ɾ����
						hm.remove(u) ;
						//֪ͨ���е��ˣ����û��˳�
						for(Socket ts : hm.values()){
							OutputStream tos = ts.getOutputStream() ;
							OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
							PrintWriter tpw = new PrintWriter(tosw , true) ;
											
							tpw.println("exit%"+u) ;
						}
						return ;
					}					
					
					//ת����Ϣ
					String to = message.split("%")[0] ;
					String mess = message.split("%")[1] ;
					Socket ts = hm.get(to) ;
					OutputStream tos = ts.getOutputStream() ;
					OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
					PrintWriter tpw = new PrintWriter(tosw , true) ;
					
					tpw.println("mess%"+mess) ;
				}
			} else {
				// ���ʹ�����Ϣ���ͻ���
				pw.println("err");
			}
		}catch(Exception e){}
		finally{
			try {
		   		rs.close() ;
			  	ps.close() ;
			   	cn.close() ;
		    }catch (Exception ex) {}
		}		
		
	}
}

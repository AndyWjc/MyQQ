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
				//将HashMap的引用传入服务线程
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
	//接收HashMap的引用
	private HashMap<String , Socket> hm ;
	public void setHashMap(HashMap<String, Socket> hm){
		this.hm = hm ;
	}
	Connection cn=null;
	PreparedStatement ps=null;
	ResultSet rs=null;
	public void run() {
		try{
			// 接受用户名和密码
			InputStream is = s.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
	
			String uandp = br.readLine();
	
			// 拆分用户名和密码
			String u = uandp.split("%")[0];
			String p = uandp.split("%")[1];
	
			OutputStream os = s.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			PrintWriter pw = new PrintWriter(osw, true);
			
			//到数据库中验证用户身份
			Class.forName("org.gjt.mm.mysql.Driver") ;
			cn = DriverManager.getConnection("jdbc:mysql://120.78.139.224:3306/qq","root","123456") ;
			ps = cn.prepareStatement("select * from user where username=? and password=?") ;
			ps.setString(1, u) ;
			ps.setString(2, p) ;
			
			rs = ps.executeQuery() ;
			
			if (rs.next()) {
				// 发送正确信息到客户端
				pw.println("ok");
	
				//将本人的名字发送给其他的用户
				for(Socket ts : hm.values()){
					OutputStream tos = ts.getOutputStream() ;
					OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
					PrintWriter tpw = new PrintWriter(tosw , true) ;
								
					tpw.println("add%"+u) ;
				}
							
				//将其他人的名字发送给自己
				for(String tu : hm.keySet()){
					pw.println("add%"+tu) ;
				}
	
				//将本人的用户名和Socket存入HashMap
				hm.put(u, s) ;
				
				//不断地接收客户端发送过来的信息
				while (true) {
					String message = br.readLine();
					if(message.equals("{exit}")){
						//将该用户从HashMap中删除掉
						hm.remove(u) ;
						//通知所有的人，本用户退出
						for(Socket ts : hm.values()){
							OutputStream tos = ts.getOutputStream() ;
							OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
							PrintWriter tpw = new PrintWriter(tosw , true) ;
											
							tpw.println("exit%"+u) ;
						}
						return ;
					}					
					
					//转发信息
					String to = message.split("%")[0] ;
					String mess = message.split("%")[1] ;
					Socket ts = hm.get(to) ;
					OutputStream tos = ts.getOutputStream() ;
					OutputStreamWriter tosw = new OutputStreamWriter(tos) ;
					PrintWriter tpw = new PrintWriter(tosw , true) ;
					
					tpw.println("mess%"+mess) ;
				}
			} else {
				// 发送错误信息到客户端
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

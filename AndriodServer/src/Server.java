import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {

	ServerSocket ss = null;
	boolean started = false;
	List<User> users = new ArrayList<User>();

	/*
	 * Driver of android server
	 */
	public static void main(String[] args) {
		int port = 15678;
		new Server().start(port);
	}

	/*
	 * Start a server socket and wait for connecting
	 */
	public void start(int port) {

		// create a server socket
		try {
			ss = new ServerSocket(port);
			System.out.println("Server starts!");
			started = true;
		} catch (BindException e) {
			System.out.println("The port has been occupied!");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// main thread
		try {
			while (started) {
				Socket s = ss.accept();
				Client c = new Client(s);
				System.out.println("a client is connected");
				new Thread(c).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class Client implements Runnable {
		private String chatKey = "SLEEKNETGEOCK4stsjeS";
		private Socket s = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean bConnected = false;
		private String sendmsg = null;

		Client(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
				bConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String str) {

			try {
				dos.writeUTF(str + "");
				dos.flush();
			} catch (IOException e) {
				System.out.println("client closed!");
			}
		}

		/*
		 * declare the format of msg
		 */
		public void run() {
			try {
				while (bConnected) {
					String str = dis.readUTF();
//					DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//					String date = "  [" + df.format(new Date()) + "]";
					// 第一种 register报文
					if (str.startsWith(chatKey + "Register")) {
						System.out.println(str);			
						/*
						 * 根据报文消息，将para加入User实例中
						 *  并将该User实例添加到users列表中
						 */
						String []info = str.split("/");
						if(!Exist(info[1], users)){
							String name = info[1];
							String email = info[11];
							int password = Integer.parseInt(info[3]);
							String gender = info[5];
							int age = Integer.parseInt(info[7]);
							long telephone= Long.parseLong(info[9]);
							User c = new User(name, email, password,
									gender, age, telephone);
							users.add(c);
							send("3");
						}
						
						else
							send("1");
					}

					// 第二种 login报文
					/*
					 * Msg Format:
					 *    chatKey + "Login" + "\n" +
					 *    "username" + "/" + "xxxxx" + "/" + "\n" +
					 *    "password" + "/" + "xxxxx" + "/" + "\n"
					 */
					else if (str.startsWith(chatKey + "Login")) {
						System.out.println(users.size());
						System.out.println(str);
						String []info = str.split("/");
						int pwd = findPwdByName(info[1], users);
						//错误，没有找到用户名，返回错误代码“1”，用户未注册
						if(pwd == -1)
							send("1/0/NotRegisterYet");
						
						else {
							//匹配，返回正确代码“3”
							if(pwd == Integer.parseInt(info[3])){
								String UserInfo = "";
								for(User temp : users) {
									UserInfo = UserInfo + temp.name + ","
											+ temp.email + ","
											+ temp.age + ","
											+ temp.gender + ","
											+ temp.Telephone + ","
											+ temp.getScore() + "|";
								}
								send("3/" + users.size() + "/"+ UserInfo);
							}
							
							//错误，返回错误代码“2”，用户名存在，密码错误
							else
								send("2/0/WrongPassword");
						}
					} 
					
					
					// 第三种update报文
					/*
					 * Msg Format:
					 *    chatKey + "Update" + "\n" +
					 *    "username" + "/" + "xxxxx" + "/" + "\n" +
					 *    "UpdateScore" + "/" + "xxxxx" + "/" + "\n"  
					 */
					else if (str.startsWith(chatKey + "Update")) {
						System.out.println(str);
						String []info = str.split("/");
						int score = Integer.parseInt(info[3]);
						for(int i = 0; i < users.size(); i++) {
							if(users.get(i).name.equals(info[1])){
								users.get(i).update(score);	
								break;
							}
						}
						
						// 返回所有User的分数信息
						String s = "";
						for(User u : users){
							s = s + u.name + ","
									+ u.email + ","
									+ u.age + ","
									+ u.gender + ","
									+ u.Telephone + ","
									+ u.getScore() + "|";
						}
						send("1/" + users.size() + "/" + s);
					}
					
					// 不支持的报文格式
					else {
						System.out.println(str);
						send("Error Format!");
					}
				}
			} catch (SocketException e) {
				System.out.println("client is closed!");
			} catch (EOFException e) {
				System.out.println("client is closed!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (dis != null)
						dis.close();
					if (dos != null)
						dos.close();
					if (s != null)
						s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		boolean Exist(String name, List<User> users){
			boolean outcome = false;
			for(User u : users){
				if(u.name.equals(name)){
					outcome = true;
					break;
				}
			}
			return outcome;
		}
		
		int findPwdByName(String name, List<User> users){
			int pwd = -1;
			for(User u : users) {
				if(u.name.equals(name))
					pwd = u.password;	
			}
			return pwd;
		}
	}
}

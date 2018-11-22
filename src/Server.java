import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	public static void main(String[] args) throws Exception {
		ServerThread server = new ServerThread(4343);
		server.start();
	}
}
class ServerThread extends Thread{
	private ServerSocket ss = null;
	private Socket s = null;
	static int count = 0;
	ArrayList<DataOutputStream> out_data=new ArrayList<DataOutputStream>();
	ArrayList<DataInputStream> in_data=new ArrayList<DataInputStream>();
	public ServerThread(int port) throws IOException {
		// TODO Auto-generated constructor stub
		ss = new ServerSocket(port);
//		ss.setSoTimeout(30000);
		System.out.println("waiting for connection on " + ss.getLocalPort());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		read();
		while (true) {
            try {
                s = ss.accept();
                System.out.println("Client"+(count++)+": connect to " + s.getRemoteSocketAddress());
                               
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out_data.add(out);
                System.out.println("out_list size = "+in_data.size());
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                out.flush();
                out.writeUTF("#Tank_"+count);
                for(int i = 0;i<out_data.size();i++)
                {
                        out_data.get(i).flush();
                        out_data.get(i).writeUTF("#Total_"+(count));
                }
                
                DataInputStream in = new DataInputStream(s.getInputStream());
                //System.out.println(in.readUTF());
                in_data.add(in);
                System.out.println("in_list size = "+in_data.size());
            } catch (Exception e) {
                System.out.println(e.toString());
                try {
                    ss.close();
                    System.out.println("ServerSocket Closed");
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
	}
	private void read() {
		// TODO Auto-generated method stub
		Thread t = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					System.out.flush();
					for (int i = 0; i < in_data.size(); i++) {
						try {
							if(in_data.get(i).available()>0){
								String str = in_data.get(i).readUTF();
								String[] token = str.split("_");
								int num = Integer.parseInt(token[0]);
								if(!str.equals("")){
									for (int j = 0; j < out_data.size(); j++) {
										System.out.println("Server Recive: "+str+" From Client"+(i+1));
	                                    System.out.println("Server send: "+token[1]+"|"+token[0]);
	                                    out_data.get(num).flush();
	                                    out_data.get(num).writeUTF("#Create_"+token[1]+"|"+token[0]);
									}
								}
							}
							
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		};
		t.start();
	}
	
}

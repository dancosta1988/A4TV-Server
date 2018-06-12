import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class SetTopBoxServer implements Runnable{
	private PrintWriter out;
	private BufferedReader in;
	private int stbPort;
	private JettyWebServer registedWebServer;
	private String storedMessage = "";
		
	public SetTopBoxServer (int port) {
		
		stbPort = port;
		
	}
	
	public  void RunSetTopBoxServer ()throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(stbPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + stbPort);
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
        	System.err.println("Waiting for Mobile connections to the STB.");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            //System.exit(1);
        }
        System.err.println("Connection Accepted.");
        //out = new PrintWriter(clientSocket.getOutputStream(),true);
        
        out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
        
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        if(storedMessage != ""){
        	sendUIMLToMobileClient(storedMessage);
        	storedMessage = "";
        }
        
        String inputLine;        
        while ((inputLine = in.readLine()) != null) {
             //kkp.processInput(inputLine);
        	System.out.println("Received command from Mobile device: " + inputLine);
        	try {
				registedWebServer.sendResponse (inputLine);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Webserver is not online.");
			}
        }
        System.err.println("Closing Connection.");
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
        RunSetTopBoxServer();
    }
	
	public void register(JettyWebServer webserver){
		registedWebServer = webserver;
	}
	
	public boolean sendUIMLToMobileClient(String uiml){
		
		if(out != null){
			//if(kkp.getSTBStatus() == A4TVKnockKnockProtocol.STB_WAITING){
				out.println(uiml);
			//	kkp.setSTBStatus(A4TVKnockKnockProtocol.STB_SENTUIML);
			//}else
				//return false;
		}else{
			storedMessage = uiml;
		}
		return true;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			RunSetTopBoxServer ();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}

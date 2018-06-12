
import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/jsrA4TV")
public class JettyWebServer {
		
		private Session session;
		private boolean stbServerRunning = false;
		private SetTopBoxServer stbServer;
		private String lastMessage = "";
		private Thread discoveryThread, stbServerThread;
				
		@OnOpen
		public void onOpen(Session session) {
			this.session = session;
			
			
			//thread for broadcast
			if(discoveryThread == null || !discoveryThread.isAlive()) {
				discoveryThread = new Thread(DiscoveryThread.getInstance());
				discoveryThread.start();
			}
			
			
			if(stbServerThread == null || !stbServerThread.isAlive()) {
				stbServer = new SetTopBoxServer(4444);		
				(stbServerThread = new Thread(stbServer)).start();
				stbServer.register(this);
				stbServerRunning = true;
				if(lastMessage != "")
					stbServer.sendUIMLToMobileClient(lastMessage);
			}
			
			
			System.out.println("WebSocket opened: " + session.getId());
			this.session.setMaxIdleTimeout(3600000);
						
			
			
						
		}
		
		@OnMessage
		public void onMessage(String txt, Session session) throws IOException {
			System.out.println("Message received: " + txt);
			//session.getBasicRemote().sendText(txt.toUpperCase());
			stbServer.sendUIMLToMobileClient(txt);
			lastMessage = txt;
			
		}
		
		public String getLastMessage(){
			return lastMessage;
		}
		
		@OnClose
		public void onClose(CloseReason reason, Session session) {
			System.out.println("Closing a WebSocket due to " + reason);
			stbServerRunning = false;
			
		}
		
		public void sendResponse(String text){
			
			try {
				session.getBasicRemote().sendText(text);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Server couldn't send the message: " + e.getMessage());
			}
		}
	}


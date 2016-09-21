import java.net.*;

public class TFTPServer extends UDPParent{ //While this class is the main class, and never dies, it doesn't do much other than get requests and spawn threads

	private DatagramSocket requestListenerSocket; //This is going to be bound to port 69, it's the TFTP request listener and only accepts RRQ/WRQ packets
	
	public TFTPServer(){ 
			try { create the listenerSocket on port 69
				requestListenerSocket=new DatagramSocket(69);
			} catch (SocketException e){
				System.out.println("Socket creation failed");
				e.printStackTrace();
				return;
			}
			requestListenerSocket.setSoTimeout(2000); //this socket times out after 2 seconds to see if we should shut down
	}
	
	
	public static void main(String[] args){
		//TODO listen on requestListenerSocket
		
		while(true){
				boolean packetReceivedFlag=true;
				try{
					DatagramPacket requestPacket=receiveDatagram(requestListenerSocket);
				} catch (SocketTimeoutException e){
					packetReceivedFlag=false; //we timed out and didn't receive a packet, current datagrampacket saved is old
				}
				if (packetReceivedFlag) { //if there's a valid request on requestListenerSocket
					//do stuff
				} else {
					//check if we're supposed to shut down
				}
		}
	}
	
}

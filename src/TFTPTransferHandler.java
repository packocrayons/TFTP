import java.net.*;

public class TFTPTransferHandler extends UDPParent implements Runnable{

	private DatagramSocket transferSocket; //This is the socket that all the TFTP traffic will travel over

	public TFTPTransferHandler(){
			try { //create the listenerSocket on random port
				requestListenerSocket=new DatagramSocket();
			} catch (SocketException e){
				System.out.println("Socket creation failed");
				e.printStackTrace();
				return;
			}
			requestListenerSocket.setSoTimeout(2000); //this socket times out after 2 seconds to see if we should shut down
	}
	
	@Override
	public void run() {
		//This is like the main() method for this server, it's what's called when a thread is spawned	

	}
	
	
}

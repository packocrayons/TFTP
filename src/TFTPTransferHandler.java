import java.net.*;

public class TFTPTransferHandler extends UDPParent implements Runnable{

	private DatagramSocket transferSocket; //This is the socket that all the TFTP traffic will travel over

	public TFTPTransferHandler(){
		
	}
	
	@Override
	public void run() {
		//This is like the main() method for this server, it's what's called when a thread is spawned		
	}
	
	
}

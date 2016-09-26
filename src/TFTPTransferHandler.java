import java.net.*;

public class TFTPTransferHandler extends UDPParent implements Runnable{

	private DatagramSocket transferSocket; //This is the socket that all the TFTP traffic will travel over

	DatagramPacket clientRequestPacket; //this packet is going to be broken down so we know what IP and port to send our response to, this is only in the constructor
	String filename;
	boolean reading; //this is set to true if the client wants to read a file from us

	public TFTPTransferHandler(DatagramPacket receivedPacket){ //swe're going to break down the packet and get info
		clientRequestPacket=receivedPacket;
		try { //create the transferSocket on random port
			transferSocket=new DatagramSocket();
		} catch (SocketException e){
			System.out.println("Socket creation failed");
			e.printStackTrace();
			return;
		}
	}
	
	public boolean sendFile(String filename, SocketAddress clientSocketAddress){ //send the file specified by filename. clientSocketAddress can be sent to the DatagramPacket constructor to send it to the client
		return false; //right now we just fail
	}

	public boolean isReadRequest(DatagramPacket p){ //this should only be called with a valid packet that is a client request
		if (p.getData()[1] == 1){
			return true;
		} else return false;
	}

	@Override
	public void run() {
		//This is like the main() method for this server, it's what's called when a thread is spawned
		if (v()){ //printing datagrams should only be done in verbose mode, but since it's a print function we can't use v(String arg)
			UDPParent.printDatagram(clientRequestPacket);
		}

		if (isReadRequest(clientRequestPacket)){
			//The client wants to read something, we have to send them data.
			sendFile(filename, clientRequestPacket.getSocketAddress()); //a socket address is port and IP address, this can be sent to the datagram 
		} else {
			//The client wants to write a file, all we do is listen and send acknowledgement (I don't know if ack is part of assignment 1?)

		}

	}
	
}

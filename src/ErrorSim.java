import java.net.*;

public class ErrorSim extends UDPParent{
	//we're going to have to pass on all the packets here

	private DatagramSocket port23Socket, clientReplySocket, serverConnectionSocket; //socket that the client sends to, socket that we reply to the client with, socket to talk to the server
	private DatagramPacket passthroughPacket; //the packet to pass through

	public ErrorSim(){
		try{
			port23Socket = new DatagramSocket(23);
			port23Socket.setSoTimeout(5000); //wait for 5 seconds on the client to create requests
			clientReplySocket= new DatagramSocket();
			clientReplySocket.setSoTimeout(2000);
			serverConnectionSocket= new DatagramSocket()
			serverConnectionSocket.setSoTimeout(2000);
		} catch(SocketException e){
			e.printStackTrace();
			System.out.println("Creating one or more sockets failed");
		}
	}

	public static void main(String[] args){
		InetAddress localHost = InetAddress.getLocalHost();
		while(true){ //always do this
			passthroughPacket = receiveDatagram(port23Socket); //listen for the client to generate a request
			passthroughPacket = generateDatagram(passthroughPacket.getData(), localHost, 69); //forward the packet to the server
			sendDatagram()
		}
	}

}

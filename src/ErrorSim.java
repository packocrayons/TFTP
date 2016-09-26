import java.net.*;

public class ErrorSim extends UDPParent{
	//we're going to have to pass on all the packets here

	private DatagramSocket port23Socket, clientReplySocket, serverConnectionSocket; //socket that the client sends to, socket that we reply to the client with, socket to talk to the server
	private DatagramPacket passthroughPacket; //the packet to pass through

	public ErrorSim(){
		try{
			port23Socket = new DatagramSocket(23);
			port23Socket.setSoTimeout(5000); //wait for 5 seconds on the client to create requests
			clientReplySocket= new DatagramSocket(); //this is what the client thinks is the 'ad-hoc' port run from another server thread, but we handle that on the error sim
			clientReplySocket.setSoTimeout(2000);
			serverConnectionSocket= new DatagramSocket(); //this socket is used for all server communication
			serverConnectionSocket.setSoTimeout(2000);
		} catch(SocketException e){
			e.printStackTrace();
			System.out.println("Creating one or more sockets failed");
		}
	}

	public static void main(String[] args){
		ErrorSim errSim = new ErrorSim();
		while(true){ //always do this
			try {
				errSim.v("Waiting to receive a Datagram on port 23, timeout of " + errSim.port23Socket.getSoTimeout());
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			errSim.passthroughPacket = errSim.receiveDatagram(errSim.port23Socket); //listen for the client to generate a request
			errSim.passthroughPacket = errSim.generateDatagram(errSim.passthroughPacket.getData(), errSim.IPAddress, 69); //forward the packet to the server
			errSim.sendDatagram(errSim.passthroughPacket, errSim.serverConnectionSocket);
			//we either forwarded a packet or 
		}
	}

}

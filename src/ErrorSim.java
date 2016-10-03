import java.net.*;

public class ErrorSim extends UDPParent implements Runnable{
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

	public void run(){
		while(true){ //always do this
			try {
				v("Waiting to receive a Datagram on port 23, timeout of " + port23Socket.getSoTimeout());
			} catch (SocketException e) {
				e.printStackTrace();
			}
			passthroughPacket = receiveDatagram(port23Socket); //listen for the client to generate a request
			int clientPort = passthroughPacket.getPort(); //so we know what port to respond on
			passthroughPacket = generateDatagram(passthroughPacket.getData(), IPAddress, 69); //forward the packet to the server
			sendDatagram(passthroughPacket, serverConnectionSocket);
			//we just forwarded an ack packet, go into another loop and do the transfer.

			//The only reason for this loop is because the packets that the client sent are going to a different port than the ack packet from the client
			while (true){ //no condition - this loop will be broken in the catch block of a socket timeout
				passthroughPacket = receiveDatagram(serverConnectionSocket); //the server knows to send here because that's where we sent the 'request' from
				passthroughPacket = generateDatagram(passthroughPacket.getData(), IPAddress, clientPort); //forward the packet to the client

			}
		}
	}

}

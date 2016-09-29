import java.net.*;

public class TFTPTransferHandler extends UDPParent implements Runnable{

	private DatagramSocket transferSocket; //This is the socket that all the TFTP traffic will travel over
	
	private DatagramPacket clientRequestPacket; //this packet is going to be broken down so we know what IP and port to send our response to, this is only in the constructor
	
	boolean reading; //this is set to true if the client wants to read a file from us
	
	

	public TFTPTransferHandler(DatagramPacket receivedPacket){ //we're going to break down the packet and get info
		clientRequestPacket=receivedPacket;
		try { //create the transferSocket on random port
			transferSocket=new DatagramSocket();
		} catch (SocketException e){
			System.out.println("Socket creation failed");
			e.printStackTrace();
			return;
		}
	}

	public boolean isReadRequest(DatagramPacket p){ //this should only be called with a valid packet that is a client request
		if (p.getData()[1] == 1){
			return true;
		} else return false;
	}

	@Override
	public void run() {
		//This is like the main() method for this server, it's what's called when a thread is spawned
		v("TFTPHandler : thread spawned, printing request packet");
		if (v()){ //printing datagrams should only be done in verbose mode, but since it's a print function we can't use v(String arg)
			UDPParent.printDatagram(clientRequestPacket);
		}

		if (isReadRequest(clientRequestPacket)){
			int clientPort = clientRequestPacket.getPort(); //this is actually the port from the intermediate but we don't care, we're just responding to whoever asked us
			byte[] data;
			DatagramPacket dataPacket, ackPacket;
			//data[] = fetch512Bytes //SURVESH/ADAM - this is where your code will be called
			for (int blockNum = 1; data.length > 512, i++){ /*while data[].length is greater than 512*/
				//data[] = fetch512Bytes //SURVESH/ADAM same thing here
				data = generateDataBlock(data, i); //create the block with the block number
				dataPacket = generateDatagram(data, IPAddress, clientPort); //generate the datagram
				if v(){
					UDPParent.print("Data packet generated");
					UDPParent.printDatagram(dataPacket);
				}
				sendDatagram(dataPacket, transferSocket);

				ackPacket = receiveDatagram(transferSocket); //wait for the ACK packet
				if (!validateACKPacket(ackPacket.getData(), blockNum)) return; //ACK was invalid - do something here in iteration 2 
			}

			data = generateDataBlock(data, i); //create the block with the block number
			dataPacket = generateDatagram(data, IPAddress, clientPort); //generate the datagram
			if v(){
				UDPParent.print("Data packet generated");
				UDPParent.printDatagram(dataPacket);										//Do everything one more time after the byte array < 512 bytes
			}
			sendDatagram(dataPacket, transferSocket);

			ackPacket = receiveDatagram(transferSocket); //wait for the ACK packet
			if (!validateACKPacket(ackPacket.getData(), blockNum)) return; //ACK was invalid - do something here in iteration 2 

		} else {
			//The client wants to write a file, all we do is listen and send acknowledgement (I don't know if ack is part of assignment 1?)
			//SOMEBODY fill this with write request code, put 512 byte blocks in a file and send ack packets//moved to TFTP Parent
		}
	}
	
	
	
}

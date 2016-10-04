import java.net.*;
import java.awt.List; 

public class TFTPClient extends UDPParent{

	DatagramSocket clientSocket;

	public TFTPClient(){
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public byte[] createRequestBlock(boolean readRequest, String filename){
		List<byte> returnArray = new ArrayList<byte>();
		returnArray.add(0);
		if (readRequest){
			returnArray.add(2);
		}
		returnArray.add(filename.getBytes());
		returnArray.add(0);
		returnArray.add("octet".getBytes()); //it doesn't matter for this course, therefore we just make an assumption, this can easily be changed by passing it to the function
		returnArray.add(0);
	}

	public static void main(String[] args){
		TFTPClient client = new TFTPClient();
		client.promptRequest();//a gui here for making read/write requests
		if (client.getReadRequest()==true){//read req.
			String filename=client.getReadFileName(); /*= filename from gui*/

			byte[] requestArray = client.createRequestBlock(true, filename);
			DatagramPacket requestPacket = client.generateDatagram(requestArray, client.IPAddress, 23);
			client.sendDatagram(requestPacket, client.clientSocket);
			DatagramPacket serverReceivedPacket = client.receiveDatagram(client.clientSocket); //The server ACK packets are data packets, they just also function as an acknowledge
			//SOMEBODY This needs verbosity in some places, see the server/ transferhandler for how it's done

			for (int blockNum = 1; serverReceivedPacket.getData().length > 512; blockNum++){
				if (!client.validateDataPacket(serverReceivedPacket.getData(), 0)) return; // breaks if it's not valid
				client.writeFile(filename, serverReceivedPacket.getData());
				DatagramPacket ackPacket = client.generateAckDatagram(serverReceivedPacket.getPort(), blockNum); //generate the ack and wait for more data
				client.sendDatagram(ackPacket, client.clientSocket);
			}
			if (!client.validateDataPacket(serverReceivedPacket.getData(), 0)) return; // breaks if it's not valid //do this one more time after the for loop for the packet that's < 512 bytes
			client.writeFile(filename, serverReceivedPacket.getData());
		} else { //write request
			String filename; //ROBERT filename from gui
			byte[] data = client.generateRequestBlock(false, filename);
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
		}
	}
}

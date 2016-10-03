import java.net.*;
import java.awt.List; 
import java.util.ArrayList;


public class TFTPClient extends UDPParent{

	private DatagramSocket clientSocket;

	public TFTPClient(){
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public byte[] createRequestBlock(boolean readRequest, String filename){
		ArrayList<Byte> returnArray = new ArrayList<>();
		returnArray.add(returnArray.size(), (byte)0);
		if (readRequest){
			returnArray.add( (byte)2);
		} else returnArray.add((byte)1);
		byte[] temp = filename.getBytes();
		for (int i = 0; i < temp.length; i++){
			returnArray.add(temp[i]);
		}
		returnArray.add((byte)0);
		temp = "octet".getBytes();
		for (int i = 0; i < temp.length; i++){
			returnArray.add(temp[i]);
		}
		returnArray.add((byte)0);
		byte[] ret = new byte[(returnArray.size())];
		for (int i = 0; i < ret.length; ++i){
			ret[i] = returnArray.get(i);
		}
		return ret;
	}

	public static void main(String[] args){
		TFTPClient client = new TFTPClient();
		client.promptRequest();//a gui here for making read/write requests
		client.prompt();
		if (client.getReadRequest()==true){//read req.
			String filename=client.getReadFileName(); /*= filename from gui*/

			byte[] requestArray = client.createRequestBlock(true, filename);
			DatagramPacket requestPacket = client.generateDatagram(requestArray, client.IPAddress, 23);
			client.sendDatagram(requestPacket, client.clientSocket);
			DatagramPacket serverReceivedPacket = client.receiveDatagram(client.clientSocket); //The server ACK packets are data packets, they just also function as an acknowledge
			

			for (int blockNum = 1; serverReceivedPacket.getData().length > 512; blockNum++){
				if (!client.validateDataPacket(serverReceivedPacket.getData(), 0)){
					return; // breaks if it's not valid
				}
				
				client.writeFile(filename, serverReceivedPacket.getData());
				DatagramPacket ackPacket = client.generateAckDatagram(serverReceivedPacket.getPort(), blockNum); //generate the ack and wait for more data
				client.sendDatagram(ackPacket, client.clientSocket);
			}
			if (!client.validateDataPacket(serverReceivedPacket.getData(), 0)) return; // breaks if it's not valid //do this one more time after the for loop for the packet that's < 512 bytes
			client.writeFile(filename, serverReceivedPacket.getData());
		} else { //write request
			String filename=client.getReadFileName(); /*= filename from gui*/
			byte[] data = client.createRequestBlock(false, filename);
			DatagramPacket dataPacket, ackPacket;
			int blockNum = 0;
			dataPacket = client.generateDatagram(data, client.IPAddress, 23); //send to the intermediate host (we think it's the server)

			client.sendDatagram(dataPacket, client.clientSocket); //send the datagram over our socket
			data= client.readFile(client.getReadFileName());//
			ackPacket = client.receiveDatagram(client.clientSocket); //wait for the server to ack this request
			if (!client.validateACKPacket(ackPacket.getData(), 0)){
				return; //ITERATION2
			}

			int serverPort = ackPacket.getPort();

			//Sorry don't know why that was in there twice

			for (blockNum = 1; data.length > 512; blockNum++){ /*while data[].length is greater than 512*/
				
				data = client.generateDataBlock(data, blockNum); //create the block with the block number
				dataPacket = client.generateDatagram(data, client.IPAddress, serverPort); //generate the datagram
				if (client.v()){
					client.print("Data packet generated");
					UDPParent.printDatagram(dataPacket);
				}
				client.sendDatagram(dataPacket, client.clientSocket);

				ackPacket = client.receiveDatagram(client.clientSocket); //wait for the ACK packet
				if (!client.validateACKPacket(ackPacket.getData(), blockNum)) return; //ACK was invalid - do something here in iteration 2 
				data= client.readFile(client.getReadFileName());//
			}

			data = client.generateDataBlock(data, blockNum); //create the block with the block number
			dataPacket = client.generateDatagram(data, client.IPAddress, serverPort); //generate the datagram
			if (client.v()){
				client.print("Data packet generated");
				UDPParent.printDatagram(dataPacket);										//Do everything one more time after the byte array < 512 bytes
			}
			client.sendDatagram(dataPacket, client.clientSocket);

			ackPacket = client.receiveDatagram(client.clientSocket); //wait for the ACK packet
			if (!client.validateACKPacket(ackPacket.getData(), blockNum)) return; //ACK was invalid - do something here in iteration 2 
		}
	}
}

import java.net.*;
//What up boys, brydon boy u went offfff!!!
public class TFTPClient extends UDPParent{

	DatagramSocket clientSocket

	public TFTPClient(){
		clientSocket = new DatagramSocket();
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
		//ROBERT - there's going to be a gui here for making read/write requests
		if (/*readRequest*/){
			String filename; /*= filename from gui*/

			byte[] requestArray = client.createRequestBlock(true, filename);
			DatagramPacket requestPacket = client.generateDatagram(requestArray, client.ipAddress, 23);
			client.sendDatagram(requestPacket, client.clientSocket);
			DatagramPacket serverReceivedPacket = client.receiveDatagram(client.clientSocket); //The server ACK packets are data packets, they just also function as an acknowledge
			//This needs verbosity in some places, see the server/ transferhandler for how it's done
			if (client.validateDataPacket(serverReceivedPacket.getData(), 0)
			client.writeFile(filename, serverReceivedPacket.getData());

			for (int blockNum = 1; serverReceivedPacket.getData().length > 512, blockNum++){
				DatagramPacket ackPacket = client.generateAckDatagram(serverReceivedPacket.getPort(), blockNum); //generate the ack and wait for more data
				client.sendDatagram(ackPacket, clientSocket);
				
			}
		}
	}
}

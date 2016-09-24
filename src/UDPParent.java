import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPParent { //this class has the majority of the methods for actually working with UDP packets, the client, server (and maybe error sim) will extend these
	
	private boolean verbose;

	public UDPParent(){
		verbose=false
	}

	public void setVerbosity(boolean verb){
		verbose=verb;
	}

	public DatagramPacket generateDatagram(byte[] byteArray, InetAddress IPaddress, int portNumber){ 
		//this method really doesn't do much, however it makes code readable and may do more later
		DatagramPacket packetToSend;
		packetToSend = new DatagramPacket(byteArray, byteArray.length, IPaddress, portNumber);
		return packetToSend;
	}
	
	public boolean sendDatagram(DatagramPacket packetToSend, DatagramSocket socketToUse){ 
		//This just tries to send the packet, unless there's an IOexception it will always return true
		try {
			socketToUse.send(packetToSend);
		} catch (IOException e) {
			System.out.println("Sending the packet failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public DatagramPacket receiveDatagram(DatagramSocket socketToUse){
		//waits (currently indefinitely) to receive a packet on the specified socket
		byte[] buffer = new byte[100];
		DatagramPacket p = new DatagramPacket(buffer, buffer.length);
		try {
			socketToUse.receive(p);
		} catch (IOException e) {
			System.out.println("Receiving from the port failed");
			e.printStackTrace();
		}
		return p;
	}

	public void v(String s){	//This prints the string if the program is in verbose mode
		if (verbose) System.out.println(s); 
	}


	public boolean validateRequestPacket(byte[] byteArray){
		if(byteArray[0] != 0) return false;
		if(byteArray[1] != 1 && byteArray[1] != 2) return false;
		int i = 0;
		for (; (i < byteArray.length) && (byteArray[i] != 0); i++) //exit when you hit the end or you find a zero
		if (i = byteArray.length - 1) return false; //otherwise i points to the zero in the bytearray, if the next char is 0, it's invalid, otherwise we assume it's valid
		if(byteArray[i+1] == 0) return false;
		return true; //packet passed all tests, it is a valid request packet
	}

	public static void printDatagram(DatagramPacket p){
		System.out.println("Datagram exists, metadata follows");                          // All this code is just printing the datagram info
		System.out.println("Packet sent to : " + p.getAddress());
		System.out.println("Sent to port : " + p.getPort());
		System.out.print("Byte array contained in packet: ");
		byte[] buf = p.getData();
		for (int i = 0; i < buf.length; ++i){
			System.out.printf("0x%02X ", buf[i]);
		}
		System.out.println("");
		String data = new String(buf);
		System.out.println("Data as a string: " + data);
	}


}

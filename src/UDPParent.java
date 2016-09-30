import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.*;
import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class UDPParent { //this class has the majority of the methods for actually working with UDP packets, the client, server (and maybe error sim) will extend these
	private byte[] byteFile;
	private boolean verbose;
	private static JTextArea textArea;
	protected InetAddress IPAddress;
	private BufferedInputStream in;
	private BufferedOutputStream out;

	public void print(String arg){//Method used to print to GUI, not required in I1
		textArea.append(arg);
	}

	public UDPParent(){
		verbose = false;
		try {
			IPAddress = InetAddress.getLocalHost(); //Just for now, all the code is running on the same physical address.
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void setVerbosity(boolean verb){
		verbose=verb;
	}

	public byte[] generateDataBlock(byte[] data, int blockNumber){
		byte[] blockNum = new ByteBuffer.allocate(4).putInt(blockNumber).order(BIG_ENDIAN).array(); //turn the int into a big endian byte array
		byte[] dataBlock = new byte[data.length + blockNum.length + 2];
		dataBlock[0] = 0;
		dataBlock[1] = 3; //03 means data
		System.arrayCopy(blockNum, 2, dataBlock, 2, blockNum.length - 2);
		System.arrayCopy(data, 0, dataBlock, 4, dataBlock.length); //copy the arrays into one big array
		return dataBlock;
	}


	public DatagramPacket generateAckDatagram(int portNumber, int blockNumber){
		byte[] blockNum = new ByteBuffer.allocate(4).putInt(blockNumber).order(BIG_ENDIAN).array(); //turn the int into a big endian byte array

		byte[] response = new byte[4];

		response[0] = 0;
		response[1] = 4;
		response[2] = blockNum[2];
		response[3] = blockNum[3]; //there are only two bits so use the two least significant bits

		DatagramPacket packetToSend;
		try {
			packetToSend = new DatagramPacket(response, response.length, InetAddress.getLocalHost(), portNumber); //Send it back to whatever port the intermediate sent to us on
		} catch (UnknownHostException e) {
			System.out.println("DatagramPacket creation failed");
			e.printStackTrace();
			return null;
		}
		return packetToSend;
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
	
	public boolean v(){
		return verbose;
	}
	
	public void createGui(){//for outputting the messages between server and client
		JFrame frame = new JFrame();
		frame.setSize(500,500);
		textArea = new JTextArea();
		
		JScrollPane scroll=new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(500,500));
		
		frame.add(scroll);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		textArea.setEditable(false);
		
	}
	public void prompt(){
		//quiet,verbose
		SwingUtilities.invokeLater(new Runnable() {//being executed in a separate thread, therefore doesn't interfere
			public void run() {
				Object[] choices = {"Quiet","Verbose"};
				Object[] choices2 = {"Test","Normal"};
				Object selectedValue = JOptionPane.showInputDialog(null,"Select one","input",JOptionPane.INFORMATION_MESSAGE,null,choices,choices[0]);
				Object selectedValue2 = JOptionPane.showInputDialog(null,"Select one","input",JOptionPane.INFORMATION_MESSAGE,null,choices2,choices2[0]);	
				if(selectedValue == "Verbose"){
					setVerbosity(true);
				}
				else{
					setVerbosity(false);
				}
				if(selectedValue == "Test"){
						//Do something
				}
				else{//Running in Normal mode
					//do something 
				}
			}//run
		 });//invokeLater
	}//prompt
	
	


	public boolean validateRequestPacket(byte[] byteArray){
		if(byteArray[0] != 0) return false;
		if(byteArray[1] != 1 && byteArray[1] != 2) return false;
		int i = 0;
		for (; (i < byteArray.length) && (byteArray[i] != 0); i++) //exit when you hit the end or you find a zero
		if (i == byteArray.length - 1) return false; //otherwise i points to the zero in the bytearray, if the next char is 0, it's invalid, otherwise we assume it's valid
		if(byteArray[i+1] == 0) return false;
		return true; //packet passed all tests, it is a valid request packet
	}

	public boolean validateACKPacket(byte[] byteArray, int blockNumber){
		byte[] blockNum = new ByteBuffer.allocate(4).putInt(blockNumber).order(BIG_ENDIAN).array(); //turn the int into a big endian byte array
		if (byteArray[0] == 0 && byteArray[1] == 1 && byteArray[2] == blockNum[2] && byteArray[3] == blockNum[3]) return true;
		return false;
	}

	public boolean validateDataPacket(byte[] byteArray, int blockNumber){
		byte[] blockNum = new ByteBuffer.allocate(4).putInt(blockNumber).order(BIG_ENDIAN).array(); //turn the int into a big endian byte array
		if (byteArray[0] == 0 && byteArray[1] == 3 && byteArray[2] == blockNum[2] && byteArray[3] == blockNum[3]){
		return true;//data
		}
		return false;
	}

	public static void printDatagram(DatagramPacket p){
		System.out.println("Datagram exists, metadata follows");// All this code is just printing the datagram info
		System.out.println("Packet sent to : " + p.getAddress());
		System.out.println("Sent to port : " + p.getPort());
		System.out.println("Byte array contained in packet: ");
		byte[] buf = p.getData();
		for (int i = 0; i < buf.length; ++i){
			System.out.printf("0x%02X ", buf[i]); //For now the method will print to console
		}
		
		System.out.println("");
		String data = new String(buf);
		System.out.println("Data as a string: " + data);
	}
	public void readFile(String file){//Param:input file name //Shouldn't this return a byte array of size 512 or less?
		try {
			in = new BufferedInputStream(new FileInputStream(file));
		} 
		catch (FileNotFoundException e) {
			System.out.println("error");
		} 
		byteFile = new byte[512];
		try {
			while(in.read(byteFile, 0, 512) != -1){
			}
		} 
		catch (IOException e) {
			System.out.println("error");
		}
	}
	
	public void writeFile(String file,byte[] contents){//param: Output file name, byte array used to write
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
		} 
		catch (FileNotFoundException e) {
			System.out.println("error");
		}
		
		try {
			out.write(contents,0,512); //writing in lengths of 512
		}
		catch (IOException e) {
			System.out.println("error");
		}
		
	}


}

import java.net.*;
//What up boys, brydon boy u went offfff!!!
public class TFTPClient extends UDPParent{


	public TFTPClient(){

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
}

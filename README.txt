This is a TFTP file transfer program, that sends files between a server and client.

The program is built using UDP packets and a simple protocol to differentiate between reads and writes, acknowledgement packets and request packets.

A read request is sent to port 69, and the server replies with an acknowledgement on a new thread with a port chosen by the JVM.
That port is then used by the server to handle the TFTP transfer.
Each packet is 512 bytes in size, except the last packet, which must be less than 512 bytes, this terminates the transfer.
If the client requests a read, the server sends packets and the client acknowledges them, if the client requests a write, the server sends packets and the client acknowledges.

At the moment, packet loss, duplication, or delay will crash the program. It will currently only function on a perfect network.

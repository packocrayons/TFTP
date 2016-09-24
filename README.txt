This is a TFTP file transfer program, that sends files between a server and client.

The program is built using UDP packets and a simple protocol to differentiate between reads and writes, acknowledgement packets and request packets.

At the moment, packet loss, duplication, or delay will crash the program. It will currently only function on a perfect network.

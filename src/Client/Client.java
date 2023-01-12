package Client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1379;

    public static void main(String[] args) throws IOException
    {
        Scanner input = new Scanner(System.in);
        Socket socket = new Socket("localhost", ServerPort);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        Thread sendMessage = new Thread(() -> {
            while (true) {
                String message = input.nextLine();
                try {
                    dataOutputStream.writeUTF(message);
                } catch (IOException e) {
                    System.exit(0); // not good
                }
            }
        });

        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String message = dataInputStream.readUTF();
                    System.out.println(message);
                } catch (IOException e) {
                    System.exit(0); // not good
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}
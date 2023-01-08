package client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1379;

    public static void main(String[] args) throws IOException
    {
        Scanner input = new Scanner(System.in);

        InetAddress ip = InetAddress.getByName("localhost");
        Socket socket = new Socket(ip, ServerPort);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        Thread sendMessage = new Thread(() -> {
            while (true) {
                String message = input.nextLine();
                try {
                    dataOutputStream.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String massage = dataInputStream.readUTF();
                    System.out.println(massage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }
}

package server;
import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    static Vector<ClientHandler> clients = new Vector<>(); // Because we need a synchronized data structure
    static int clientCount = 0;

    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(1379);
        Socket socket;
        Thread ContestThread = new Thread(new Contest(clients));

        while (true)
        {
            socket = serverSocket.accept();
            System.out.println("Client requested socket: " + socket);

            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            ClientHandler clientHandler = new ClientHandler(socket,"client " + clientCount, dataInputStream, dataOutputStream);

            Thread clientHandlerThread = new Thread(clientHandler);

            System.out.println("Adding this client to active client list.");

            clients.add(clientHandler);
            clientHandlerThread.start();
            clientCount++;
        }
    }
}

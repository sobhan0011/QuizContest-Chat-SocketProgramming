package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable
{
    Scanner input = new Scanner(System.in);
    private final String name;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    Socket socket;
    boolean isloggedIn;

    public ClientHandler(Socket socket, String name, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isloggedIn = true;
    }

    @Override
    public void run() {

        String received;
        while (true)
        {
            try
            {
                received = dataInputStream.readUTF();

                System.out.println(received);

                if (received.equals("logout")) {
                    this.isloggedIn = false;
                    this.socket.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer stringTokenizer = new StringTokenizer(received, "#");
                String MsgToSend = stringTokenizer.nextToken();
                String recipient = stringTokenizer.nextToken();

                // search for the recipient in the connected devices list.
                // clients is the vector storing client of active users
                for (ClientHandler clientHandler : Server.clientHandlers)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (clientHandler.name.equals(recipient) && clientHandler.isloggedIn)
                    {
                        clientHandler.dataOutputStream.writeUTF(this.name + " : " + MsgToSend);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try
        {
            this.dataInputStream.close();
            this.dataOutputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
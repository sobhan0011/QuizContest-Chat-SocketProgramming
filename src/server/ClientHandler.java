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
    Contest contest;

    public ClientHandler(Socket socket, String name, DataInputStream dataInputStream, DataOutputStream dataOutputStream, Contest contest) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isloggedIn = true;
        this.contest = contest;
    }


    public String getName() {
        return name;
    }

    @Override
    public void run() {

        String received, recipient, message, answer;
        while (true)
        {
            try
            {
                received = dataInputStream.readUTF();
                received = received.replaceFirst("^\\s*", "");
                System.out.println(received);

                if (received.trim().equals("logout")) {
                    this.isloggedIn = false;
                    this.socket.close();
                    break;
                }
                if (received.matches())
                {
                   recipient = "";
                   message = "";
                    for (ClientHandler clientHandler : Server.clientHandlers)
                        if (clientHandler.name.equals(recipient) && clientHandler.isloggedIn)
                        {
                            clientHandler.dataOutputStream.writeUTF(this.name + " : " + message);
                            break;
                        }
                }
                else if (received.matches())
                {
                    answer = "";
                    contest.getDataOutputStream().writeUTF(this.name + " : " + answer);
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
package Server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    Socket socket;
    final DataInputStream dataInputStream;
    final DataOutputStream dataOutputStream;
    private final String name;
    boolean isloggedIn;
    DataOutputStream contestDataOutputStream;

    public ClientHandler(Socket socket, String name, DataInputStream dataInputStream, DataOutputStream dataOutputStream, DataOutputStream contestDataOutputStream) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isloggedIn = true;
        this.contestDataOutputStream = contestDataOutputStream;
    }


    public String getName() {
        return name;
    }

    @Override
    public void run() {
        String received, recipient, message;
        while (true)
        {
            try
            {
                received = dataInputStream.readUTF();
                received = received.replaceFirst("^\\s*", "");
                System.out.println(this.name + " typed: " + received);
                if (received.equals("1") || received.equals("2") || received.equals("3") || received.equals("4"))
                    contestDataOutputStream.writeUTF(this.name + ":" + received);
                else if (received.startsWith("msg to"))
                {
                    String temp = received.substring(7);
                    if (temp.contains(":"))
                    {
                        String[] str = temp.split(":");
                        recipient = str[0];
                        message = str[1];
                        for (ClientHandler clientHandler : Server.clientHandlers)
                            if (clientHandler.name.equals(recipient) && clientHandler.isloggedIn) {
                                clientHandler.dataOutputStream.writeUTF(this.name + " : " + message);
                                break;
                            }
                    }
                }

                else if (received.trim().equals("logout")) {
                    this.isloggedIn = false;
                    this.socket.close();
                    break;
                }
            } catch (IOException e) {
                Thread.currentThread().interrupt(); //not good
                e.printStackTrace();
            }
        }
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
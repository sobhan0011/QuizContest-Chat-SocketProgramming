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

    public ClientHandler(Socket socket, String name, DataInputStream dataInputStream, DataOutputStream dataOutputStream, DataOutputStream contestDataOutputStrem) {
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
        this.name = name;
        this.socket = socket;
        this.isloggedIn = true;
        this.contestDataOutputStream = contestDataOutputStrem;
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
                System.out.println(received);

                if (received.trim().equals("logout")) {
                    this.isloggedIn = false;
                    this.socket.close();
                    break;
                }
                if (received.startsWith("msg to"))
                {
                    String temp = received.substring(6);
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
//                else if (received.startsWith("answer"))
//                    if (received.charAt(6) == ':') {
//                        answer = received.substring(7);
//                        contestDataOutputStream.writeUTF(this.name + " : " + answer);
//                    }
            } catch (IOException e) {
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
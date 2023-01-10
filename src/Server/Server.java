package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server
{
    static Vector<ClientHandler> clientHandlers = new Vector<>(); // Because we need a synchronized data structure
    static DataOutputStream contestDataOutputStream;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(1379);
        Socket socket;
        DataOutputStream dataOutputStream;
        DataInputStream dataInputStream;

        JSONArray users = null;
        JSONParser jsonParser = new JSONParser();
        try {
            users = (JSONArray) jsonParser.parse(new FileReader("users.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        assert users != null;
        int clientNumber = users.size() - 1;
        Contest contest = new Contest(clientNumber);
        socket = serverSocket.accept();
        contestDataOutputStream = new DataOutputStream(socket.getOutputStream());
        Thread contestThread = new Thread(contest);
        contestThread.start();

        int usersObjNum = 1;
        JSONObject jsonObject;
        String type;
        int port;

        while (usersObjNum <= clientNumber)
        {
            socket = serverSocket.accept();
            jsonObject = (JSONObject) users.get(usersObjNum);
            type = (String) jsonObject.get("type");
            port = Integer.parseInt(jsonObject.get("port").toString());
            String name = (String) jsonObject.get("name");
            System.out.println("Client " + name + " requested socket: " + socket);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Creating a new handler for this client and adding it to active client list.\n");
            ClientHandler clientHandler = new ClientHandler(socket, name, dataInputStream,
                    dataOutputStream, contestDataOutputStream);
            Thread clientHandlerThread = new Thread(clientHandler);
            clientHandlers.add(clientHandler);
            clientHandlerThread.start();
            usersObjNum++;
        }

        contestThread.join();
    }
}

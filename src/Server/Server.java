package Server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.net.*;

public class Server
{
    static Vector<ClientHandler> clientHandlers = new Vector<>(); // Because we need a synchronized data structure
    static int clientCount = 0;
    private static JSONArray users;
    private static String type;
    private static String name;
    static ServerSocket serverSocket;
    static ClientHandler clientHandler;


    public static void main(String[] args) throws IOException
    {
        int usersObjNum = 0;
        JSONParser jsonParser = new JSONParser();
        try {
            users = (JSONArray) jsonParser.parse(new FileReader("users.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        while(usersObjNum < users.size()) {
            JSONObject obj = (JSONObject) users.get(usersObjNum);
            type = (String) obj.get("type");
            int port = Integer.parseInt(obj.get("port").toString());
            name = (String) obj.get("name");
            if(type.startsWith("host")) {
                serverSocket = new ServerSocket(port);
            }
            usersObjNum++;
        }

        Socket socket;
        Contest contest = new Contest(clientHandlers);
        Thread contestThread = new Thread(contest);
        contestThread.start();
        while (true)
        {
            socket = serverSocket.accept();
            System.out.println("Client requested socket: " + socket);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");
            for (int i = 0; i < name.length(); i++) {
                if(type.startsWith("client"))
                    clientHandler = new ClientHandler(socket, name, dataInputStream, dataOutputStream, contest.getDataOutputStream());
            }

            Thread clientHandlerThread = new Thread(clientHandler);

            System.out.println("Adding this client to active client list.");
            clientHandlers.add(clientHandler);
            clientHandlerThread.start();
            clientCount++;
        }
    }
}

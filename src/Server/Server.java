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
    private static JSONArray users;

    public static void main(String[] args) throws IOException, InterruptedException {
        int usersObjNum = 0;
        JSONParser jsonParser = new JSONParser();
        try {
            users = (JSONArray) jsonParser.parse(new FileReader("users.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        ServerSocket serverSocket = new ServerSocket(1379);
        Socket socket;
        Contest contest = new Contest(clientHandlers, users.size());
        Thread contestThread = new Thread(contest);
        contestThread.start();

        while (usersObjNum < users.size())
        {
            socket = serverSocket.accept();
            JSONObject obj = (JSONObject) users.get(usersObjNum);
//            String type = (String) obj.get("type");
//            int port = Integer.parseInt(obj.get("port").toString());
            String name = (String) obj.get("name");
            System.out.println("Client requested socket: " + socket);
            System.out.println(name);
            if (!name.equals("host-1")) {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                System.out.println("Creating a new handler for this client...");
                ClientHandler clientHandler = new ClientHandler(socket, name, dataInputStream,
                        dataOutputStream, contest.getDataOutputStream());
                Thread clientHandlerThread = new Thread(clientHandler);
                System.out.println("Adding this client to active client list.");
                clientHandlers.add(clientHandler);
                clientHandlerThread.start();
            }
            usersObjNum++;
        }
        contestThread.join();
    }
}

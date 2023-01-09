package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Contest implements Runnable {
    final static int ServerPort = 1379;
    private String question;
    private String options;
    private int currentQuestion = 0;
    private JSONArray contestQuestionAnswers;
    private final Vector<ClientHandler> clients;
    private final ArrayList<QuestionAnswer> questionAnswers;
    private final int clientNumber;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;

    public DataOutputStream getDataOutputStream() {
        return this.dataOutputStream;
    }


    Contest(Vector<ClientHandler> clients, int clientNumber) throws IOException {
        this.clientNumber = clientNumber;
        this.questionAnswers = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try {
            contestQuestionAnswers = (JSONArray) jsonParser.parse(new FileReader("questions.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

//        Scanner input = new Scanner(System.in);

        Socket socket = new Socket("localhost", ServerPort);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());

//        Thread sendMessage = new Thread(() -> {
//            while (true) {
//                String massage = input.nextLine();
//                try {
//                    dataOutputStream.writeUTF(massage);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        Thread readMessage = new Thread(() -> {
//            while (true) {
//                try {
//                    String massage = dataInputStream.readUTF();
//                    System.out.println(massage);
//                } catch (IOException e) {
//
//                    e.printStackTrace();
//                }
//            }
//        });
//        sendMessage.start();
//        readMessage.start();
        this.clients = clients;

    }

    @Override
    public void run() {
        long startTime;
        int[] answers = new int[clientNumber];
        int[] scores = new int[clientNumber];
        while (clients.size() < clientNumber - 1);
        while (currentQuestion < contestQuestionAnswers.size()) {
            nextQuestion();
            String[] optionsSplit = options.split(",");
            String optionsFormat = "\n";
            for (int i = 0; i < 3; i++)
                optionsFormat += "\n" + (i + 1) + ". " + optionsSplit[i].substring(1, optionsSplit[i].length() - 1) + "\n";

            for (int i = 0; i < clientNumber - 1; i++) {
                try {
                    clients.get(i).dataOutputStream.writeUTF(question + optionsFormat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 5000)
            {
                AtomicReference<String> token = null;
                Thread readMessage = new Thread(() -> {
                    while (true)
                    {
                        try {
                            token.set(this.dataInputStream.readUTF());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                if (token != null) {
                    System.out.println(token);
                    String[] str = token.get().split(":");
                    for (int i = 0; i < clients.size(); i++)
                        if (clients.get(i).getName().equals(str[0]) && answers[i] == 0)
                            answers[i] = Integer.parseInt(str[1]);
                }
            }
            System.out.println("ahyes");


            for (int i = 0; i < clientNumber; i++)
                if (answers[i] == questionAnswers.get(currentQuestion).getAnswer())
                    scores[i] += 10;
            String str = resultTable(clients, scores);
            for (int i = 0; i < clientNumber - 1; i++) {
                try {
                    clients.get(i).dataOutputStream.writeUTF(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            currentQuestion++;
        }


    }


    public void nextQuestion() {
        JSONObject obj = (JSONObject) contestQuestionAnswers.get(currentQuestion);
        question = (String) obj.get("question");
        options = obj.get("options").toString();
        options = options.substring(1, options.length() - 1);
        int answer = Integer.parseInt(obj.get("answer").toString());
        questionAnswers.add(new QuestionAnswer(question, options, answer));
    }

    private String resultTable(Vector<ClientHandler> clients, int[] scores) {
        String str = null;
        for (int i = 0; i < clients.size(); i++)
            str += this.clients.get(i).getName() + " : " + scores[i] + ".\n";
        return str;
    }

}
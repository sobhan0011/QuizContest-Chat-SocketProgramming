package Server;

import java.net.Socket;
import java.util.Arrays;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Contest implements Runnable {
    final static int ServerPort = 1379;
    private String question;
    private String options;
    private int answer;
    private int currentQuestion = 0;
    private JSONArray contestQuestionAnswers;
    private final int clientNumber;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private final Socket socket;

    Contest(int clientNumber) throws IOException {
        this.socket = new Socket("localhost", ServerPort);
        this.dataInputStream = new DataInputStream(socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.clientNumber = clientNumber;
        JSONParser jsonParser = new JSONParser();
        try {
            contestQuestionAnswers = (JSONArray) jsonParser.parse(new FileReader("questions.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int[] answers = new int[clientNumber];
        int[] scores = new int[clientNumber];
        while (Server.clientHandlers.size() < clientNumber);
        while (currentQuestion < contestQuestionAnswers.size()) {
            nextQuestion();
            String[] optionsSplit = options.split(",");
            String optionsFormat = "";
            for (int i = 0; i < 3; i++)
                optionsFormat += "\n" + (i + 1) + ". " + optionsSplit[i].substring(1, optionsSplit[i].length() - 1);
            for (int i = 0; i < clientNumber; i++) {
                try {
                    Server.clientHandlers.get(i).dataOutputStream.writeUTF(question + optionsFormat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            Thread readMessage = new Thread(() -> {
                while (true) {
                    try {
                        String message = dataInputStream.readUTF();
                        if (!message.isEmpty())
                        {
                            String[] str = message.split(":");
                            for (int i = 0; i < clientNumber; i++)
                                if (Server.clientHandlers.get(i).getName().equals(str[0]) && answers[i] == 0)
                                    answers[i] = Integer.parseInt(str[1]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readMessage.start();
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 20000);
            readMessage.interrupt();

            for (int i = 0; i < clientNumber; i++)
                if (answers[i] == answer)
                    scores[i] += 10;

            String str = resultTable(scores);
            for (int i = 0; i < clientNumber; i++) {
                try {
                    Server.clientHandlers.get(i).dataOutputStream.writeUTF(str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentQuestion++;
        }
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void nextQuestion() {
        JSONObject obj = (JSONObject) contestQuestionAnswers.get(currentQuestion);
        question = (String) obj.get("question");
        options = obj.get("options").toString();
        options = options.substring(1, options.length() - 1);
        answer = Integer.parseInt(obj.get("answer").toString());
    }

    private String resultTable(int[] scores) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < this.clientNumber; i++)
            str.append(Server.clientHandlers.get(i).getName()).append(" : ").append(scores[i]).append(".\n");
        return str.toString();
    }

}

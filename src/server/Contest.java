package server;

import java.util.ArrayList;
import java.util.Vector;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Contest implements Runnable {

    private int currentQuestion = 0;
    private JSONArray contestQn;
    Vector<ClientHandler> clients;
    ArrayList<QuestionAnswer> questionAnswers;

    Contest(Vector<ClientHandler> clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        StartContest();
        while (clients.size() < 3);
        while(currentQuestion < contestQn.size()) {
            nextQuestion();
            // Time limit and Clients' scores
        }
        currentQuestion++;
    }

    public void StartContest() {
        JSONParser jsonParser = new JSONParser();
        try {
            contestQn = (JSONArray) jsonParser.parse(new FileReader("ContestQuestions.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void nextQuestion() {
        JSONObject obj = (JSONObject) contestQn.get(currentQuestion);
        String question = (String) obj.get("question");
        String options = obj.get("options").toString();
        options = options.substring(1, options.length()-1);
        int answer = Integer.parseInt(obj.get("answer").toString());
        questionAnswers.add(new QuestionAnswer(question, options, answer));
    }

}

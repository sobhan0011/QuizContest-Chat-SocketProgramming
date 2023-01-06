package server;

import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Contest implements Runnable {

    private int currentQuestion = 0;
    private JSONArray questions;
    Vector<ClientHandler> clients;
    QuestionAnswer[] questionAnswers;

    Contest(Vector<ClientHandler> clients, QuestionAnswer[] questionAnswers) {
        this.clients = clients;
        this.questionAnswers = questionAnswers;
    }

    @Override
    public void run() {
        initialGame();
        while (clients.size() < 3);
        while(currentQuestion < questions.size()) {
            sendQuestion();
            // Time limit and Clients' scores
        }
        currentQuestion++;
    }

    public void initialGame(){
        JSONParser jsonParser = new JSONParser();
        try {
            questions = (JSONArray) jsonParser.parse(new FileReader("questions.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void sendQuestion(){
        JSONObject obj = (JSONObject) questions.get(currentQuestion);
        String question = (String) obj.get("question");
        String options = obj.get("options").toString();
        options = options.substring(1, options.length()-1);
        int answer = Integer.parseInt(obj.get("answer").toString());
        new QuestionAnswer(question, options, answer);
    }

}

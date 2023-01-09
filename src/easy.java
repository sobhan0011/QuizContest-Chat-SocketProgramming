import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class easy {
    public static void main(String[] args) {
        JSONParser jsonParser = new JSONParser();
        JSONArray contestQuestionAnswers = null;
        JSONObject obj;
        int answer;
        String question, options;
        try {
            contestQuestionAnswers = (JSONArray) jsonParser.parse(new FileReader("questions.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        for (int currentQuestion = 0; currentQuestion < contestQuestionAnswers.size(); currentQuestion++) {
            obj = (JSONObject) contestQuestionAnswers.get(currentQuestion);
            question = (String) obj.get("question");
            options = obj.get("options").toString();
            options = options.substring(1, options.length() - 1);
            answer = Integer.parseInt(obj.get("answer").toString());
            System.out.println(question);
            System.out.println(options);
            System.out.println(answer);
        }

    }
}

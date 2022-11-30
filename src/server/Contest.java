package server;

import java.util.ArrayList;
import java.util.Vector;

public class Contest implements Runnable {
    Vector<ClientHandler> clients;
    QuestionAnswer[] questionAnswers;
    Contest(Vector<ClientHandler> clients, QuestionAnswer[] questionAnswers) {
        this.clients = clients;
        this.questionAnswers = questionAnswers;
    }
    
    @Override
    public void run() {
        while (clients.size() < 3);
        for (int i = 0; i < questionAnswers.length; i++) {
            
        }
    }
}

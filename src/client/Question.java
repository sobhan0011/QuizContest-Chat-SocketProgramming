package client;

class Question {
    String question;
    String[] options;

    Question(String question, String[] options)
    {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }


    public String[] getOptions() {
        return options;
    }
}

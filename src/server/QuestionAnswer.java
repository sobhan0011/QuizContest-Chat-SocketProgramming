package server;

class QuestionAnswer {
    private String question, options;
    private int answer;

    QuestionAnswer(String question, String options, int answer)
    {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptions() {
        return options;
    }

    public int getAnswer() {
        return answer;
    }
}

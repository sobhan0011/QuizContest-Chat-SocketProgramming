package Server;

class QuestionAnswer {
    private final String question, options;
    private final int answer;

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

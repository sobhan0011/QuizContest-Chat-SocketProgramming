package Client;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class Client extends Application
{
    final static int ServerPort = 1379;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        int WIDTH = 1000;
        int HEIGHT = 450;
        Socket socket = new Socket("localhost", ServerPort);
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        HBox root = new HBox();
        VBox[] columns = {new VBox(), new VBox(), new VBox()};
        for (VBox column : columns) {
            root.getChildren().add(column);
            column.prefWidthProperty().bind(stage.widthProperty().multiply(0.33));
        }

        Button sendButton = new Button("Send");
        ScrollPane scrollPane = new ScrollPane();
        TextArea textArea = new TextArea();
        columns[2].getChildren().addAll(scrollPane, textArea, sendButton);
        scrollPane.prefHeightProperty().bind(stage.heightProperty().multiply(0.7));
        scrollPane.setFitToWidth(true);

        Text messages = new Text("");
        scrollPane.setContent(messages);
        sendButton.setOnAction(actionEvent -> {
            String text = textArea.getText();
            System.out.println("Message Entered:" + text);
            if (!text.isBlank() && !text.matches("-?\\d+(\\.\\d+)?")) {
                try {
                    dataOutputStream.writeUTF(text);
                    messages.setText(messages.getText() + "\n" + "me: " + text);
                    textArea.clear();
                } catch (IOException e) {
                    System.exit(0); // not good
                }
            }
        });

        Text text = new Text();
        ScrollPane scrollPane2 = new ScrollPane();
        scrollPane2.setContent(text);
        scrollPane2.prefHeightProperty().bind(stage.heightProperty().multiply(0.5));
        columns[0].getChildren().add(scrollPane2);
        scrollPane2.setFitToWidth(true);
        Button[] buttons = {new Button("1"), new Button("2"), new Button("3"), new Button("4")};
        HBox buttonsHBox = new HBox();
        for (Button button : buttons) {
            buttonsHBox.getChildren().add(button);
            button.setOnAction(actionEvent -> {
                try {
                    dataOutputStream.writeUTF(button.getText());
                } catch (IOException e) {
                    System.exit(0);
                }
            });
        }

        Text questionOptions = new Text();
        ScrollPane scrollPane3 = new ScrollPane();
        scrollPane3.setContent(questionOptions);
        scrollPane3.prefHeightProperty().bind(stage.heightProperty().multiply(0.6));
        columns[1].getChildren().addAll(scrollPane3, buttonsHBox);
        scrollPane3.setFitToWidth(true);
        File input = new File("data/vs.jpg");
        Image img = new Image(input.toURI().toString());
        BackgroundImage bImg = new BackgroundImage(img,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(WIDTH, HEIGHT, false, false, false, true));
        Background bGround = new Background(bImg);
        root.setBackground(bGround);


        Thread readMessage = new Thread(() -> {
            while (true) {
                try {
                    String message = dataInputStream.readUTF();
                    System.out.println(message);
                    if (message.contains("**************************** Next Question ****************************"))
                        questionOptions.setText(message);
                    else if (message.contains("-----------------------Score Table--------------------------"))
                        text.setText(message);
                    else
                        messages.setText(messages.getText() + "\n" + message);
                } catch (IOException e) {
                    System.exit(0); // not good
                }
            }
        });

        readMessage.start();

        Scene scene = new Scene(root,WIDTH,HEIGHT);
        stage.setTitle("Contest");
        stage.setScene(scene);
        stage.show();
        File input2 = new File("data/style.css");
        scene.getStylesheets().add(input2.toURI().toString());

    }
}

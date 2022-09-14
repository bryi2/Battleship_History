import java.util.HashMap;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
public class serverGUI extends Application {
    server serverConnection;
    ListView<String> listItems, guessList, gameList;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server GUI");
        listItems = new ListView<String>(); // have the list of clients and their connection
        guessList = new ListView<String>(); // have a list of client's guess activity
        gameList = new ListView<String>(); // have a list of client's category and word
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.setScene(mainScene());
        primaryStage.show();
    }
    public Scene mainScene() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(50));
        pane.setBackground(new Background(new BackgroundFill(Color.HONEYDEW, CornerRadii.EMPTY, Insets.EMPTY)));
        Label clients = new Label("Player List");
        VBox cList = new VBox(clients, listItems);
        Label cwLabel = new Label("testing messages");
        VBox cwList = new VBox(cwLabel, gameList);
        HBox first = new HBox(50, cList, cwList);
        Label activityLabel = new Label("Chat Room");
        VBox activityList = new VBox(activityLabel, guessList);

        serverConnection = new server(data -> {
            Platform.runLater(()->{
                listItems.getItems().add(data.toString());
                // these lines make it so the list view scrolls automatically
                int index = listItems.getItems().size();
                listItems.scrollTo(index);
            });

        }, data2 -> {
            Platform.runLater(()->{
                guessList.getItems().add(data2.toString());
                // these lines make it so the list view scrolls automatically
                int index = guessList.getItems().size();
                guessList.scrollTo(index);
            });

        }, data3 -> {
            Platform.runLater(()->{
                gameList.getItems().add(data3.toString());
                // these lines make it so the list view scrolls automatically
                int index = gameList.getItems().size();
                gameList.scrollTo(index);
            });
        });
        pane.setCenter(first);
        first.setAlignment(Pos.CENTER);
        pane.setBottom(activityList);
        return new Scene(pane, 800, 800);
    }
}
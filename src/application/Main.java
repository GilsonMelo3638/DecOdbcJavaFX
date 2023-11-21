package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Main extends Application {
    // A cena principal da aplicação.
    private static Scene mainScene;

    // Método principal que inicia a aplicação JavaFX.
    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o arquivo FXML que define a estrutura da interface gráfica.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
            ScrollPane scrollPane = loader.load();

            // Configura o ScrollPane para ajustar à altura e largura do conteúdo.
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);

            // Cria a cena principal com o conteúdo carregado do arquivo FXML.
            mainScene = new Scene(scrollPane);

            // Configura o palco principal com a cena e exibe a aplicação.
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Sample JavaFX application");
            primaryStage.show();
        } catch (IOException e) {
            // Exibe informações sobre exceções durante o carregamento da interface gráfica.
            e.printStackTrace();
        }
    }

    // Obtém a cena principal da aplicação.
    public static Scene getMainScene() {
        return mainScene;
    }

    // Método principal que inicia a execução da aplicação.
    public static void main(String[] args) {
        launch(args);
    }
}

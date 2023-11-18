package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import Validacoes.TipoDoc;
import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.entities.Agenda;
import model.services.AgendaService;

public class MainViewController implements Initializable {

    private final AgendaService service = new AgendaService();

    @FXML
    private MenuItem menuItemAgenda;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;
    
    @FXML
    private TextField txtDias;

    @FXML
    public void onMenuItemAboutAction() {
        loadView("/gui/About.fxml", x -> {
        });
    }

    @FXML
    public void comboTipoDocChanged() {
        try {
            // Certifique-se de que o serviço não seja nulo antes de usá-lo
            Objects.requireNonNull(service, "O serviço não está inicializado.");

            TipoDoc selectedTipoDoc = comboTipoDoc.getValue(); // ou outra forma de obter o valor selecionado
            int dias = Integer.parseInt(txtDias.getText()); // Obtenha o valor do campo txtDias como um inteiro

            List<Agenda> agendas = service.findAllByTipoDoc(selectedTipoDoc, dias);
            // Atualize a exibição com as novas agendas
        } catch (NumberFormatException e) {
            // Lida com exceção se o valor em txtDias não for um número inteiro válido
            Alerts.showAlert("Error", null, "Please enter a valid number for days.", AlertType.ERROR);
        } catch (Exception e) {
            // Lida com outras exceções
            e.printStackTrace(); // Substitua por uma lógica apropriada
        }
    }


    @FXML
    public void onMenuItemAgendaAction() {
        loadView("/gui/AgendaList.fxml", (AgendaListController controller) -> {
            controller.setAgendaService(service);
            controller.updateTableView();
        });
    }

    @Override
    public void initialize(URL uri, ResourceBundle rb) {
    }

    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            Scene mainScene = Main.getMainScene();
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            Node mainMenu = mainVBox.getChildren().get(0);
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainMenu);
            mainVBox.getChildren().addAll(newVBox.getChildren());

            T controller = loader.getController();
            initializingAction.accept(controller);
        } catch (IOException e) {
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
}

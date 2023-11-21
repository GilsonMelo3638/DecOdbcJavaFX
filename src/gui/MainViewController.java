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

// Controlador principal da aplicação
public class MainViewController implements Initializable {

    // Serviço para manipulação de agendas
    private final AgendaService service = new AgendaService();

    @FXML
    private MenuItem menuItemAgenda;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;

    @FXML
    private TextField txtDias;

    // Método chamado quando a opção "About" no menu é selecionada
    @FXML
    public void onMenuItemAboutAction() {
        // Carrega a view sobre informações
        loadView("/gui/About.fxml", x -> {
            // Nenhuma ação de inicialização necessária
        });
    }

    // Método chamado quando o valor do ComboBox de tipo de documento é alterado
    @FXML
    public void comboTipoDocChanged() {
        try {
            // Certifica-se de que o serviço não seja nulo antes de usá-lo
            Objects.requireNonNull(service, "O serviço não está inicializado.");

            // Obtém o tipo de documento selecionado no ComboBox
            TipoDoc selectedTipoDoc = comboTipoDoc.getValue();
            // Obtém o número de dias do campo txtDias como um inteiro
            int dias = Integer.parseInt(txtDias.getText());

            // Encontra as agendas pelo tipo de documento e número de dias
            List<Agenda> agendas = service.findAllByTipoDoc(selectedTipoDoc, dias);
            // Atualiza a exibição com as novas agendas

        } catch (NumberFormatException e) {
            // Lida com exceção se o valor em txtDias não for um número inteiro válido
            Alerts.showAlert("Error", null, "Please enter a valid number for days.", AlertType.ERROR);
        } catch (Exception e) {
            // Lida com outras exceções
            e.printStackTrace(); // Substitua por uma lógica apropriada
        }
    }

    // Método chamado quando a opção "Agenda" no menu é selecionada
    @FXML
    public void onMenuItemAgendaAction() {
        // Carrega a view da lista de agendas
        loadView("/gui/AgendaList.fxml", (AgendaListController controller) -> {
            // Seta o serviço de agenda e atualiza a tabela
            controller.setAgendaService(service);
            controller.updateTableView();
        });
    }

    // Método de inicialização do controlador
    @Override
    public void initialize(URL uri, ResourceBundle rb) {
        // Nenhuma inicialização necessária por enquanto
    }

    // Método para carregar uma nova view
    private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
        try {
            // Carrega o arquivo FXML da nova view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox newVBox = loader.load();

            // Obtém a cena principal e a caixa de layout principal
            Scene mainScene = Main.getMainScene();
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            // Obtém o menu principal
            Node mainMenu = mainVBox.getChildren().get(0);

            // Limpa a caixa de layout principal e adiciona o menu principal e a nova view
            mainVBox.getChildren().clear();
            mainVBox.getChildren().add(mainMenu);
            mainVBox.getChildren().addAll(newVBox.getChildren());

            // Obtém o controlador da nova view
            T controller = loader.getController();
            // Executa a ação de inicialização no controlador
            initializingAction.accept(controller);
        } catch (IOException e) {
            // Exibe um alerta em caso de erro ao carregar a view
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
}

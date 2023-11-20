package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import Validacoes.SituacaoProcessamento;
import Validacoes.TipoDoc;
import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Agenda;
import model.services.AgendaService;

public class AgendaListController implements Initializable, DataChangeListener {

	private AgendaService service;

	@FXML
	private TableView<Agenda> tableViewAgenda;

	@FXML
	private TableColumn<Agenda, Long> tableColumnCodigo;

	@FXML
	private TableColumn<Agenda, String> tableColumnTipo;

	@FXML
	private TableColumn<Agenda, String> tableColumnInicio;

	@FXML
	private TableColumn<Agenda, String> tableColumnFim;

	@FXML
	private TableColumn<Agenda, String> tableColumnSituacao;

	@FXML
	private TableColumn<Agenda, String> tableColumnArquivo;

	@FXML
	private TableColumn<Agenda, String> tableColumnQuantidade;
	
    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;

	@FXML
	private TableColumn<Agenda, Agenda> tableColumnEDIT;

	@FXML
	private TextField txtCodAgenda;
	
	@FXML
	private TextField txtDias;
	
	@FXML
    private Label txtTotalArquivo;
	
	@FXML
	private TableColumn<Agenda, Agenda> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Agenda> obsList;

    // Método chamado quando o botão "New" é acionado para adicionar um novo
    // agenda.
    @FXML
    public void onBtNewAction(ActionEvent event) {
        Stage parentStage = Utils.currentStage(event);
        Agenda obj = new Agenda();
        createDialogForm(obj, "/gui/AgendaForm.fxml", parentStage);
        // Após adicionar um novo item, atualiza o valor do Label
        calculateAndSetTotalLines();
    }
	

	// Define o serviço de agenda.
	public void setAgendaService(AgendaService service) {
		this.service = service;
	}
	
    // Inicialização do controlador.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
        txtDias.setText("10");
        comboTipoDoc.getItems().setAll(TipoDoc.values());
        // Calcula o total de linhas e define no txtTotalArquivo
        calculateAndSetTotalLines();
    }

    private void calculateAndSetTotalLines() {
        if (obsList != null) {
            int totalLines = obsList.size();
            txtTotalArquivo.setText(String.valueOf(totalLines));
        }
    }

	// Inicializa os nós da tabela.
	private void initializeNodes() {
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("cod_agenda_extracao"));
		tableColumnTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_doc"));
		tableColumnInicio.setCellValueFactory(new PropertyValueFactory<>("par_inicio"));
		tableColumnFim.setCellValueFactory(new PropertyValueFactory<>("par_fim"));
		tableColumnArquivo.setCellValueFactory(new PropertyValueFactory<>("nome_arquivo"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		tableColumnSituacao.setCellValueFactory(new PropertyValueFactory<>("ind_situacao"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAgenda.prefHeightProperty().bind(stage.heightProperty());
	}


    // Atualiza a tabela de agendas.
    public void updateTableView() {
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        List<Agenda> list = service.findAll();
        obsList = FXCollections.observableArrayList(list);
        tableViewAgenda.setItems(obsList);
        initEditButtons();
        initRemoveButtons();
        // Adicione esta linha para calcular e definir o total de linhas
        calculateAndSetTotalLines();
    }

	// Cria um formulário de diálogo para adicionar ou editar um agenda.
	private void createDialogForm(Agenda obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			AgendaFormController controller = loader.getController();
			controller.setAgenda(obj);
			controller.setAgendaService(new AgendaService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Agenda data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@FXML
	public void handleSearch(ActionEvent event) {
		try {
			Long codAgenda = Long.valueOf(txtCodAgenda.getText());
			Agenda agenda = service.findByCodAgenda(codAgenda);
			// Limpa a lista de dados da tabela
			tableViewAgenda.getItems().clear();

			// Verifica se a agenda foi encontrada
			if (agenda != null) {
				// Adiciona o resultado da pesquisa à lista de itens da tabela
				tableViewAgenda.getItems().add(agenda);
			} else {
				// A agenda não foi encontrada, você pode exibir uma mensagem ao usuário
				System.out.println("Agenda não encontrada para o código: " + codAgenda);
			}

		} catch (NumberFormatException e) {
			// Lida com a exceção se o texto não for um número
			// (você pode exibir uma mensagem de erro, por exemplo)
			e.printStackTrace(); // Substitua por uma lógica apropriada
		}
	}


	@FXML
	public void PesquisarTipoDoc(ActionEvent event) {
	    try {
	        TipoDoc tipoDoc = comboTipoDoc.getValue(); // Obtenha o valor selecionado do ComboBox
	        int dias = Integer.parseInt(txtDias.getText()); // Obtenha o valor do campo txtDias como um inteiro
	        List<Agenda> agendas = service.findAllByTipoDoc(tipoDoc, dias);
	        // Limpa a lista de dados da tabela
	        tableViewAgenda.getItems().clear();

	        // Verifica se a lista de agendas não está vazia
	        if (!agendas.isEmpty()) {
	            // Adiciona os resultados da pesquisa à lista de itens da tabela
	            tableViewAgenda.getItems().addAll(agendas);
	        } else {
	            // A lista de agendas está vazia, você pode exibir uma mensagem ao usuário
	            System.out.println("Nenhuma agenda encontrada para o tipo de documento: " + tipoDoc);
	        }

	        // Adicione esta linha para calcular e definir o total de linhas
	        calculateAndSetTotalLines();

	    } catch (Exception e) {
	        // Lida com a exceção (você pode exibir uma mensagem de erro, por exemplo)
	        e.printStackTrace(); // Substitua por uma lógica apropriada
	    } 
	}
	
	// Chamado quando os dados são alterados.
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	

	// Inicializa os botões de edição.
	private void initEditButtons() {
	    tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

	    tableColumnEDIT.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
	        private final Button button = new Button("edit");

	        @Override
	        protected void updateItem(Agenda obj, boolean empty) {
	            super.updateItem(obj, empty);
	            if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
	                setGraphic(null);
	                return;
	            }
	            setGraphic(button);
	            button.setOnAction(event -> createDialogForm(obj, "/gui/AgendaForm.fxml", Utils.currentStage(event)));
	        }
	    });

	    // Defina a largura da coluna aqui ou em outro lugar do seu código
	    tableColumnEDIT.setPrefWidth(60);
	}




	// Inicializa os botões de remoção.
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Agenda obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	    // Defina a largura da coluna aqui ou em outro lugar do seu código
		tableColumnREMOVE.setPrefWidth(120);
	}

	// Remove um agenda após confirmação.
	private void removeEntity(Agenda obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
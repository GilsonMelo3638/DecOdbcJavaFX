package gui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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

// Controlador para a tela de listagem de agendas
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

	@FXML
	private PieChart pieChart;

	@FXML
	private LineChart<String, Number> lineChart;

	@FXML
	private List<Agenda> originalAgendaList;

	// Método chamado quando o botão "New" é acionado para adicionar um novo
	// agenda.
	@FXML
	public void onBtNewAction(ActionEvent event) {
		// Obtém a referência à janela pai
		Stage parentStage = Utils.currentStage(event);
		// Cria uma nova agenda
		Agenda obj = new Agenda();
		// Chama o método para exibir o formulário de diálogo
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
		// Inicializa os componentes da tela
		initializeNodes();
		// Define o valor padrão para o campo de dias
		txtDias.setText("10");
		// Preenche o ComboBox com os valores de TipoDoc
		comboTipoDoc.getItems().setAll(TipoDoc.values());

		// Adiciona um listener para capturar as alterações no comboTipoDoc
		comboTipoDoc.valueProperty().addListener((obs, oldVal, newVal) -> {
			// Atualiza o gráfico de pizza com a lista filtrada
			List<Agenda> filteredList = filterAgendaListByTipo(newVal);
			updateCharts(filteredList);

			System.out.println("AgendaListController initialized");
		});

		// Calcula o total de linhas e define no txtTotalArquivo
		calculateAndSetTotalLines();
	}

	// Método para calcular e definir o total de linhas no txtTotalArquivo
	private void calculateAndSetTotalLines() {
		if (obsList != null) {
			int totalLines = obsList.size();
			// Define o total de linhas no Label
			txtTotalArquivo.setText(String.valueOf(totalLines));
		}
	}

	// Método para filtrar a lista de acordo com o tipo selecionado
	private List<Agenda> filterAgendaListByTipo(TipoDoc tipo) {
		return originalAgendaList.stream().filter(agenda -> agenda.getTipo_doc() == tipo).collect(Collectors.toList());
	}

	// Inicializa os nós da tabela.
	private void initializeNodes() {
		// Associa as colunas da tabela aos atributos da classe Agenda
		tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("cod_agenda_extracao"));
		tableColumnTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_doc"));
		tableColumnInicio.setCellValueFactory(new PropertyValueFactory<>("par_inicio"));
		tableColumnFim.setCellValueFactory(new PropertyValueFactory<>("par_fim"));
		tableColumnArquivo.setCellValueFactory(new PropertyValueFactory<>("nome_arquivo"));
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		tableColumnSituacao.setCellValueFactory(new PropertyValueFactory<>("ind_situacao"));

		// Obtém a referência à janela principal e ajusta a altura da tabela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewAgenda.prefHeightProperty().bind(stage.heightProperty());
	}

	// Atualiza a tabela de agendas.
	public void updateTableView() {
		// Verifica se o serviço está inicializado
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		// Obtém a lista de agendas do serviço
		originalAgendaList = service.findAll();
		// Cria uma lista observável a partir da lista obtida
		obsList = FXCollections.observableArrayList(originalAgendaList);
		// Define a lista observável como itens da tabela
		tableViewAgenda.setItems(obsList);
		// Inicializa os botões de edição e remoção
		initEditButtons();
		initRemoveButtons();
		// Calcula e define o total de linhas
		calculateAndSetTotalLines();
		// Atualiza o gráfico de pizza e o gráfico de linha
		updateCharts(originalAgendaList);
	}

	// Cria um formulário de diálogo para adicionar ou editar uma agenda.
	private void createDialogForm(Agenda obj, String absoluteName, Stage parentStage) {
		try {
			// Carrega o arquivo FXML do formulário
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			// Carrega a pane do formulário
			Pane pane = loader.load();

			// Obtém o controlador do formulário
			AgendaFormController controller = loader.getController();
			// Seta a agenda no controlador
			controller.setAgenda(obj);
			// Seta o serviço de agenda no controlador
			controller.setAgendaService(new AgendaService());
			// Registra o listener de alteração de dados no controlador
			controller.subscribeDataChangeListener(this);
			// Atualiza os dados do formulário
			controller.updateFormData();

			// Cria uma nova janela para exibir o formulário
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Agenda data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			// Exibe um alerta em caso de erro ao carregar a view
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	// Método chamado quando o botão de pesquisa por código é acionado
	@FXML
	public void handleSearch(ActionEvent event) {
		try {
			// Obtém o código da agenda a ser pesquisada
			Long codAgenda = Long.valueOf(txtCodAgenda.getText());
			// Encontra a agenda pelo código
			Agenda agenda = service.findByCodAgenda(codAgenda);
			// Limpa a lista de dados da tabela
			tableViewAgenda.getItems().clear();

			// Verifica se a agenda foi encontrada
			if (agenda != null) {
				// Adiciona o resultado da pesquisa à lista de itens da tabela
				tableViewAgenda.getItems().add(agenda);
			} else {
				// A agenda não foi encontrada, exibe uma mensagem ao usuário
				System.out.println("Agenda não encontrada para o código: " + codAgenda);
			}

		} catch (NumberFormatException e) {
			// Lida com a exceção se o texto não for um número
			// (pode ser substituído por uma lógica apropriada)
			e.printStackTrace();
		}
	}

	// Método chamado quando o botão de pesquisa por tipo de documento é acionado
	@FXML
	public void PesquisarTipoDoc(ActionEvent event) {
		try {
			// Obtém o tipo de documento selecionado no ComboBox
			TipoDoc tipoDoc = comboTipoDoc.getValue();
			// Obtém o número de dias do campo txtDias como um inteiro
			int dias = Integer.parseInt(txtDias.getText());
			// Encontra as agendas pelo tipo de documento e número de dias
			List<Agenda> agendas = service.findAllByTipoDoc(tipoDoc, dias);
			// Limpa a lista de dados da tabela
			tableViewAgenda.getItems().clear();

			// Verifica se a lista de agendas não está vazia
			if (!agendas.isEmpty()) {
				// Adiciona os resultados da pesquisa à lista de itens da tabela
				tableViewAgenda.getItems().addAll(agendas);
			} else {
				// A lista de agendas está vazia, exibe uma mensagem ao usuário
				System.out.println("Nenhuma agenda encontrada para o tipo de documento: " + tipoDoc);
			}

			// Calcula e define o total de linhas
			calculateAndSetTotalLines();

		} catch (Exception e) {
			// Lida com a exceção (pode ser substituído por uma lógica apropriada)
			e.printStackTrace();
		}
	}

	// Chamado quando os dados são alterados.
	@Override
	public void onDataChanged() {
		// Atualiza a tabela ao detectar alterações nos dados
		updateTableView();
	}

	// Inicializa os botões de edição.
	private void initEditButtons() {
		// Define a fábrica de células para a coluna de edição
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnEDIT.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Agenda obj, boolean empty) {
				super.updateItem(obj, empty);
				// Verifica se o objeto não é nulo e está agendado para edição
				if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
					setGraphic(null);
					return;
				}
				// Configura o botão e ação para abrir o formulário de edição
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/AgendaForm.fxml", Utils.currentStage(event)));
			}
		});

		// Define a largura da coluna
		tableColumnEDIT.setPrefWidth(60);
	}

    private void updateCharts(List<Agenda> agendaList) {
        // Calcular a soma total da quantidade
        BigDecimal totalQuantidade = agendaList.stream().map(Agenda::getQuantidade).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        // Preencher os dados do gráfico de pizza
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Criar uma série para o gráfico de linha
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Adicionar as fatias do gráfico de pizza com rótulos (labels) e dados ao
        // gráfico de linha
        for (Agenda agenda : agendaList) {
            BigDecimal quantidade = agenda.getQuantidade();
            double percentage = quantidade.doubleValue() / totalQuantidade.doubleValue();
            String label = String.format("%s (%.2f%%)", agenda.getNome_arquivo(), percentage * 100);

            // Converte a String para LocalDateTime
            LocalDateTime parInicio = LocalDateTime.parse(agenda.getPar_inicio(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Obtém o YearMonth a partir do LocalDateTime
            YearMonth yearMonth = YearMonth.from(parInicio);

         // Adiciona dados ao gráfico de linha
            series.getData().add(new XYChart.Data<>(formatYearMonthDay(parInicio.toLocalDate()), quantidade));

            // Adicionar fatias do gráfico de pizza
            PieChart.Data pieData = new PieChart.Data(label, quantidade.doubleValue());
            pieChartData.add(pieData);
        }

        // Configurar o gráfico de pizza
        pieChart.setData(pieChartData);

        // Configurar o gráfico de linha
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

 
 // Método auxiliar para formatar LocalDate como String
    private String formatYearMonthDay(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }




	// Inicializa os botões de remoção.
	private void initRemoveButtons() {
		// Define a fábrica de células para a coluna de remoção
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnREMOVE.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Agenda obj, boolean empty) {
				super.updateItem(obj, empty);
				// Verifica se o objeto não é nulo e está agendado para remoção
				if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
					setGraphic(null);
					return;
				}
				// Configura o botão e ação para remover a agenda
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});

		// Define a largura da coluna
		tableColumnREMOVE.setPrefWidth(120);
	}

	// Remove uma agenda após confirmação.
	private void removeEntity(Agenda obj) {
		// Exibe um diálogo de confirmação
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			// Verifica se o serviço está inicializado
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				// Remove a agenda do serviço
				service.remove(obj);
				// Atualiza a tabela após a remoção
				updateTableView();
			} catch (DbIntegrityException e) {
				// Exibe um alerta em caso de erro na remoção
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
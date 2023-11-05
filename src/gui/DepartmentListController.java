package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable {

	private DepartmentService service;
	
	// Anotações @FXML para conectar elementos da GUI com o código
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;

	// Método que é executado quando o botão "btNew" é clicado
	@FXML
	private void onBtNewAction() {
		System.out.println("onBtNewAction");
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	// Método para inicializar os elementos da GUI
	private void initializeNodes() {
		// Configura como as colunas da tabela se relacionam com as propriedades do
		// objeto Department
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		// Obtém a instância da janela principal e vincula a altura preferida da tabela
		// à altura da janela
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service wal null");
		}
		List<Department> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartment.setItems(obsList);
	}
	

	// Construtor da classe que implementa a interface Initializable
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
		// Chama o método para inicializar os elementos da interface gráfica (GUI)
		initializeNodes();
	}
}

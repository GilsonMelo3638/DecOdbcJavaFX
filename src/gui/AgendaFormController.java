package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import Validacoes.SituacaoProcessamento;
import Validacoes.TipoDoc;
import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Agenda;
import model.exceptions.ValidationException;
import model.services.AgendaService;

public class AgendaFormController implements Initializable {

    private Agenda entity;

    private AgendaService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtCodigo;

    @FXML
    private ComboBox<TipoDoc> comboTipo;

    @FXML
    private TextField txtInicio;

    @FXML
    private TextField txtFim;

    @FXML
    private ComboBox<SituacaoProcessamento> comboSituacao;

    @FXML
    private Label labelErrorTipo;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    public void setAgenda(Agenda entity) {
        this.entity = entity;
    }

    public void setAgendaService(AgendaService service) {
        this.service = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event) {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        } catch (DbException e) {
            Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
        }
    }

    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    private Agenda getFormData() {
        Agenda obj = new Agenda();
        ValidationException exception = new ValidationException("Validation error");

        obj.setCod_agenda_extracao(Utils.tryParseToInt(txtCodigo.getText()));

        TipoDoc tipoDocumento = comboTipo.getValue();
        obj.setTipo_doc(tipoDocumento);

        // Adicione o código para o novo enumerado
        if (comboSituacao.getValue() == null) {
            exception.addError("situacao", "Field can't be empty");
        } else {
            obj.setInd_situacao(comboSituacao.getValue());
        }

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtCodigo);

        // Inicialize o ComboBox com os valores do Enum TipoDoc
        ObservableList<TipoDoc> tipoDocList = FXCollections.observableArrayList(TipoDoc.values());
        comboTipo.setItems(tipoDocList);

        // Inicialize o ComboBox com os valores do Enum SituacaoProcessamento
        ObservableList<SituacaoProcessamento> situacaoList = FXCollections.observableArrayList(SituacaoProcessamento.values());
        comboSituacao.setItems(situacaoList);
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }

        txtCodigo.setText(String.valueOf(entity.getCod_agenda_extracao()));

        if (entity.getTipo_doc() != null) {
            comboTipo.setValue(entity.getTipo_doc());
        } else {
            comboTipo.getSelectionModel().clearSelection();
        }

        // Adicione a lógica para o campo Par_inicio
        if (entity.getPar_inicio() != null) {
            // Assumindo que o campo de início é do tipo Date ou algo semelhante
            // Você pode precisar converter para a representação de texto desejada
            txtInicio.setText(entity.getPar_inicio().toString());
        } else {
            // Lide com o caso em que o campo de início é nulo
            txtInicio.setText("");
        }

        // Adicione a lógica para o campo Par_fim
        if (entity.getPar_fim() != null) {
            // Assumindo que o campo de início é do tipo Date ou algo semelhante
            // Você pode precisar converter para a representação de texto desejada
            txtFim.setText(entity.getPar_fim().toString());
        } else {
            // Lide com o caso em que o campo de início é nulo
            txtFim.setText("");
        }
    }

    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("name")) {
            labelErrorTipo.setText(errors.get("name"));
        }
    }
}

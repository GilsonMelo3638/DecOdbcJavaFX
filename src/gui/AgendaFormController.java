package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import model.entities.Agenda;
import model.exceptions.ValidationException;
import model.services.AgendaService;

public class AgendaFormController implements Initializable {

    // Atualiza os dados do formulário com a entidade atual.
    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        txtCodigo.setText(String.valueOf(entity.getCod_agenda_extracao()));
        comboTipo.setValue(entity.getTipo_doc());
        // outros campos...

        // Adicione o código para preencher o comboSituacao com o valor da entidade
        comboSituacao.setValue(entity.getInd_situacao());

        // Inicialize os campos txtInicio e txtFim com os valores da entidade
        if (entity.getPar_inicio() != null) {
            txtInicio.setText(entity.getPar_inicio());
        }

        if (entity.getPar_fim() != null) {
            txtFim.setText(entity.getPar_fim());
        }
    }

    // Entidade atual sendo manipulada pelo formulário.
    private Agenda entity;

    // Serviço que fornece operações relacionadas à entidade Agenda.
    private AgendaService service;

    // Lista de ouvintes para eventos de mudança de dados.
    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtInicio;

    @FXML
    private TextField txtFim;

    @FXML
    private ComboBox<TipoDoc> comboTipo;

    @FXML
    private DatePicker datePickerInicio;

    @FXML
    private ComboBox<Integer> comboHoraInicio;

    @FXML
    private DatePicker datePickerFim;

    @FXML
    private ComboBox<Integer> comboHoraFim;

    @FXML
    private ComboBox<SituacaoProcessamento> comboSituacao;
    
    @FXML
    private RadioButton radioAutomatico;

    @FXML
    private RadioButton radioManual;

    @FXML
    private ToggleGroup grupoRadio;

    @FXML
    private Label labelErrorTipo;

    @FXML
    private Button btSave;

    @FXML
    private Button btCancel;

    // Define a entidade a ser manipulada pelo formulário.
    public void setAgenda(Agenda entity) {
        this.entity = entity;
    }

    // Define o serviço para operações relacionadas à entidade Agenda.
    public void setAgendaService(AgendaService service) {
        this.service = service;
    }

    // Inscreve um ouvinte para eventos de mudança de dados.
    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    // Ação do botão "Salvar" no formulário.
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

    // Notifica os ouvintes sobre a mudança de dados.
    private void notifyDataChangeListeners() {
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChanged();
        }
    }

    // Obtém os dados do formulário e retorna uma instância de Agenda.
    private Agenda getFormData() {
        Agenda obj = new Agenda();
        ValidationException exception = new ValidationException("Validation error");

        obj.setCod_agenda_extracao(Utils.tryParseToInt(txtCodigo.getText()));

        TipoDoc tipoDocumento = comboTipo.getValue();
        obj.setTipo_doc(tipoDocumento);

        if (comboSituacao.getValue() == null) {
            exception.addError("situacao", "Field can't be empty");
        } else {
            obj.setInd_situacao(comboSituacao.getValue());
        }

        // Adicionar lógica para tratar txtInicio e txtFim
        String txtInicioValue = txtInicio.getText();
        String txtFimValue = txtFim.getText();

        // Adicione a lógica para validar e definir os valores em obj
        if (isValid(txtInicioValue) && isValid(txtFimValue)) {
            obj.setPar_inicio(txtInicioValue);
            obj.setPar_fim(txtFimValue);
        } else {
            exception.addError("txtInicio", "Invalid value");
            exception.addError("txtFim", "Invalid value");
        }

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return obj;
    }

    // Verifica se um valor é válido (não nulo ou vazio).
    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Ação do botão "Cancelar" no formulário.
    @FXML
    public void onBtCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    // Inicializa o controlador.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeNodes();

        // Adiciona um ouvinte de mudança de valor ao datePickerInicio
        datePickerInicio.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Define a mesma data no datePickerFim quando a data do datePickerInicio for alterada
            datePickerFim.setValue(newValue);
        });

        // Adicionar ouvintes para datePickerInicio e comboHoraInicio
        datePickerInicio.valueProperty().addListener((observable, oldValue, newValue) -> updateTxtDateTime(
                comboHoraInicio, datePickerInicio, txtInicio, "%02d:00:00"
        ));
        comboHoraInicio.valueProperty().addListener((observable, oldValue, newValue) -> updateTxtDateTime(
                comboHoraInicio, datePickerInicio, txtInicio, "%02d:00:00"
        ));

        // Inicialize o ComboBox com os valores do Enum TipoDoc
        ObservableList<TipoDoc> tipoDocList = FXCollections.observableArrayList(TipoDoc.values());
        comboTipo.setItems(tipoDocList);

        // Inicialize o ComboBox com os valores do Enum SituacaoProcessamento
        ObservableList<SituacaoProcessamento> situacaoList = FXCollections.observableArrayList(SituacaoProcessamento.values());
        comboSituacao.setItems(situacaoList);

        // Adia a definição do valor padrão para o evento onShown do ComboBox
        comboSituacao.setOnShown(event -> {
            SituacaoProcessamento valorPadrao = SituacaoProcessamento.AGENDADO;
            if (situacaoList.contains(valorPadrao)) {
                comboSituacao.getSelectionModel().select(valorPadrao);
            }
        });

        // Inicialize os ComboBoxes de hora
        ObservableList<Integer> horas = FXCollections.observableArrayList();

        for (int i = 0; i <= 23; i++) {
            horas.add(i);
        }

        comboHoraInicio.setItems(horas);
        comboHoraInicio.setValue(horas.get(0)); // Define o primeiro valor como padrão

        comboHoraFim.setItems(horas);
        comboHoraFim.setValue(horas.get(horas.size() - 1)); // Define o último valor como padrão

        // Adicionar ouvintes para datePickerFim e comboHoraFim
        datePickerFim.valueProperty().addListener((observable, oldValue, newValue) -> updateTxtDateTime(
                comboHoraFim, datePickerFim, txtFim, "%02d:59:59"
        ));
        comboHoraFim.valueProperty().addListener((observable, oldValue, newValue) -> updateTxtDateTime(
                comboHoraFim, datePickerFim, txtFim, "%02d:59:59"
        ));
        
     // Adiciona um ouvinte de mudança no comboTipo
        comboTipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateComboHoraFimBasedOnTipoDoc(newValue);
            }
        });
        
        // Adicione um ouvinte para o RadioButton radioAutomatico
        radioAutomatico.selectedProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                // Se o radioAutomatico estiver selecionado, habilite o datePickerInicio
                datePickerInicio.setDisable(false);

                // Defina o valor de comboSituacao para AGENDADO
                if (comboSituacao.getItems().contains(SituacaoProcessamento.AGENDADO)) {
                    comboSituacao.getSelectionModel().select(SituacaoProcessamento.AGENDADO);
                }
            } else {
                // Se o radioAutomatico não estiver selecionado, desabilite o datePickerInicio
                datePickerInicio.setDisable(true);
                // Lógica adicional, se necessário
            }
        });

        // Configura os RadioButtons para fazer parte do mesmo grupo
        grupoRadio = new ToggleGroup();
        radioAutomatico.setToggleGroup(grupoRadio);
        radioManual.setToggleGroup(grupoRadio);

        // Define o RadioButton inicial selecionado
        radioAutomatico.setSelected(true);
        
        // Adiciona um ouvinte para o comboTipo
        comboTipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Verifica se o radioAutomatico está selecionado
                if (radioAutomatico.isSelected()) {
                    // Desabilita a edição dos campos
                    txtInicio.setDisable(true);
                    txtFim.setDisable(true);
                    comboHoraInicio.setDisable(false);
                    comboHoraFim.setDisable(true);
                    datePickerFim.setDisable(true);
                    
                    // Limpa o valor de txtInicio
                    datePickerInicio.setValue(null);

                    // Set comboSituacao como AGENDADO
                    if (comboSituacao.getItems().contains(SituacaoProcessamento.AGENDADO)) {
                        comboSituacao.getSelectionModel().select(SituacaoProcessamento.AGENDADO);
                    }
                }
            }
        });
        
        // Adiciona um ouvinte para o comboHoraInicio
        comboHoraInicio.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && radioAutomatico.isSelected() && "hora".equals(comboTipo.getValue().getFrequencia())) {
                // Se frequencia for "hora", ajusta o valor de comboHoraFim para o mesmo valor
                comboHoraFim.setValue(newValue);
            }
        });
        
        // Adiciona um ouvinte para o comboTipo
        comboTipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Verifica se o radioAutomatico está selecionado
                if (radioAutomatico.isSelected()) {
                    // Desabilita a edição dos campos
                    txtInicio.setDisable(true);
                    txtFim.setDisable(true);
                    comboHoraInicio.setDisable(false);
                    comboHoraFim.setDisable(true);

                    // Set comboSituacao como AGENDADO
                    if (comboSituacao.getItems().contains(SituacaoProcessamento.AGENDADO)) {
                        comboSituacao.getSelectionModel().select(SituacaoProcessamento.AGENDADO);
                    }
                } else {
                    // Se radioManual está selecionado, habilita a edição dos campos
                    txtInicio.setDisable(true);
                    txtFim.setDisable(true);
                    datePickerInicio.setDisable(false);
                    datePickerFim.setDisable(false);
                    comboHoraInicio.setDisable(false);
                    comboHoraFim.setDisable(false);

                    // Lógica para configurar comboSituacao conforme necessário para o radioManual
                    // (você pode ajustar conforme necessário)
                    if (comboSituacao.getItems().contains(SituacaoProcessamento.AGENDADO)) {
                        comboSituacao.getSelectionModel().select(SituacaoProcessamento.AGENDADO);
                    }
                }
            }
        });

        
    }
    
    private void updateComboHoraFimBasedOnTipoDoc(TipoDoc tipoDoc) {
        if ("diario".equals(tipoDoc.getFrequencia())) {
            // Se for diário, configure o comboHoraFim para 23:59:59
            comboHoraFim.setValue(23);
        } else if ("hora".equals(tipoDoc.getFrequencia())) {
            // Se for por hora, configure o comboHoraFim para a mesma hora do comboHoraInicio
            comboHoraFim.setValue(comboHoraInicio.getValue());
        }
    }


    // Inicializa os elementos gráficos.
    private void initializeNodes() {
        Constraints.setTextFieldInteger(txtCodigo);

        // Inicialize o ComboBox com os valores do Enum TipoDoc
        ObservableList<TipoDoc> tipoDocList = FXCollections.observableArrayList(TipoDoc.values());
        comboTipo.setItems(tipoDocList);

        // Inicialize o ComboBox com os valores do Enum SituacaoProcessamento
        ObservableList<SituacaoProcessamento> situacaoList = FXCollections
                .observableArrayList(SituacaoProcessamento.values());
        comboSituacao.setItems(situacaoList);

        // Preencher as ComboBox de horas
        ObservableList<Integer> horas = FXCollections.observableArrayList();

        for (int i = 0; i <= 23; i++) {
            horas.add(i);
        }

        setupHoraComboBox(comboHoraInicio, "%02d:00:00");
        setupHoraComboBox(comboHoraFim, "%02d:59:59");

        // Formatar a exibição das horas
        comboHoraInicio.setItems(horas);
        comboHoraFim.setItems(horas);
    }

    // Atualiza o valor da data e hora em um TextField.
    private void updateTxtDateTime(ComboBox<Integer> comboHora, DatePicker datePicker, TextField textField,
            String formatoHora) {
        LocalDate data = datePicker.getValue();
        Integer hora = comboHora.getValue();

        if (data != null && hora != null) {
            LocalDateTime dataHoraFim = LocalDateTime.of(data, LocalTime.of(hora, 0))
                    .withMinute(59)
                    .withSecond(59);

            // Formatar a data e hora como desejado (exemplo: "yyyy-MM-dd HH:mm:ss")
            String formato = "yyyy-MM-dd HH:mm:ss";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            String resultadoFormatadoFim = dataHoraFim.format(formatter);

            // Verifica qual TextField foi passado como argumento e atualiza o valor correspondente
            if (textField == txtInicio) {
                // Se for o txtInicio, atualiza o valor conforme sua lógica original
                String resultadoFormatadoInicio = LocalDateTime.of(data, LocalTime.of(hora, 0))
                        .format(formatter);
                txtInicio.setText(resultadoFormatadoInicio);
            } else if (textField == txtFim) {
                // Se for o txtFim, apenas atualiza a hora, mantendo minutos e segundos
                txtFim.setText(resultadoFormatadoFim);
            }
        } else {
            // Lógica de tratamento para valores nulos, se necessário
            textField.clear(); // Limpar o campo se algum dos valores for nulo
        }
    }

    // Configura a ComboBox de hora com um formato específico.
    private void setupHoraComboBox(ComboBox<Integer> comboBox, String formato) {
        ObservableList<Integer> horas = FXCollections.observableArrayList();

        for (int i = 0; i <= 23; i++) {
            horas.add(i);
        }

        comboBox.setItems(horas);
        comboBox.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer hora) {
                return String.format(formato, hora);
            }

            @Override
            public Integer fromString(String string) {
                // Implemente a conversão inversa, se necessário
                return null;
            }
        });
    }

    // Exibe mensagens de erro no formulário.
    private void setErrorMessages(Map<String, String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("name")) {
            labelErrorTipo.setText(errors.get("name"));
        }
    }
}

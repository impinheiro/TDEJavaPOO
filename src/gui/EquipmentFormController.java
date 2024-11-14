package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import loans.Equipment;
import model.exceptions.ValidationException;
import models.services.EquipmentService;

public class EquipmentFormController implements Initializable {
	private Equipment entity;
	private EquipmentService service;
	private EquipmentListController equipmentController;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setEquipmentController(EquipmentListController equipmentController) {
		this.equipmentController = equipmentController;
	}

	@FXML
	private TextField equipmentId;
	@FXML
	private TextField equipmentName;
	@FXML
	private TextField equipmentPurchase;
	@FXML
	private TextField equipmentWeight;
	@FXML
	private TextField equipmentWidth;
	@FXML
	private TextField equipmentLength;

	@FXML
	private TextArea equipmentHistory;
	@FXML
	private ComboBox<String> conservationStatus;
	@FXML
	private Button saveEquipment;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorPurchase;
	@FXML
	private Label labelErrorWeight;
	@FXML
	private Label labelErrorWidth;
	@FXML
	private Label labelErrorLength;
	@FXML
	private Label labelErrorStatus;

	public void setEquipment(Equipment obj) {
		this.entity = obj;
	}

	public void setEquipmentService(EquipmentService service) {
		this.service = service;
	}

	private Equipment getFormData() throws ParseException {
	    Equipment obj = new Equipment();
	    ValidationException exception = new ValidationException("Validation error");

	    Integer id = Utils.tryParseToInt(equipmentId.getText());
	    if (id != null) {
	        obj.setId(id);
	    }

	    // Validação do nome
	    if (equipmentName.getText() == null || equipmentName.getText().trim().equals("")) {
	        exception.addError("equipmentName", "Nome não pode ser vazio");
	    }
	    obj.setDescription(equipmentName.getText());

	    // Validação da data de compra
	    if (equipmentPurchase.getText() == null || equipmentPurchase.getText().trim().equals("")) {
	        exception.addError("purchase", "Data de compra não pode ser vazia");
	    } else {
	        try {
	            java.util.Date utilDate = sdf.parse(equipmentPurchase.getText());
	            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	            obj.setPurchaseDate(sqlDate);
	        } catch (ParseException e) {
	            exception.addError("purchase", "Formato de data inválido. Use dd/MM/yyyy");
	        }
	    }

	    // Validação do peso
	    if (equipmentWeight.getText() == null || equipmentWeight.getText().trim().equals("")) {
	        exception.addError("weight", "Peso não pode ser vazio");
	    }
	    try {
	        obj.setWeight(Double.parseDouble(equipmentWeight.getText()));
	    } catch (NumberFormatException e) {
	        exception.addError("weight", "Peso deve ser um número válido");
	    }

	    // Validação da largura
	    if (equipmentWidth.getText() == null || equipmentWidth.getText().trim().equals("")) {
	        exception.addError("width", "Largura não pode ser vazia");
	    }
	    try {
	        obj.setWidth(Double.parseDouble(equipmentWidth.getText()));
	    } catch (NumberFormatException e) {
	        exception.addError("width", "Largura deve ser um número válido");
	    }

	    // Validação do comprimento
	    if (equipmentLength.getText() == null || equipmentLength.getText().trim().equals("")) {
	        exception.addError("length", "Comprimento não pode ser vazio");
	    }
	    try {
	        obj.setLength(Double.parseDouble(equipmentLength.getText()));
	    } catch (NumberFormatException e) {
	        exception.addError("length", "Comprimento deve ser um número válido");
	    }

	    // Validação do status de conservação
	    if (conservationStatus.getValue() == null) {
	        exception.addError("status", "Status de conservação não pode ser vazio");
	    }
	    obj.setConditionStatus(conservationStatus.getValue());

	    // Tratamento do histórico
	    if (obj.getId() == null) {
	        // Novo equipamento
	        String history = "Recentemente adicionado";
	        ArrayList<String> historyArray = new ArrayList<>(Arrays.asList(history));
	        obj.setHistory(historyArray);
	    } else {
	        // Equipamento existente, preservar o histórico antigo e adicionar novo
	        String history = equipmentHistory.getText();
	        if (obj.getHistory() == null) {
	            obj.setHistory(new ArrayList<>()); // Inicializa o histórico caso seja null
	        }
	        obj.getHistory().add(history); // Adiciona a nova entrada
	    }

	    if (exception.getErrors().size() > 0) {
	        throw exception;
	    }

	    return obj;
	}

	public void onBtSaveEquipmentAction(ActionEvent event) throws ParseException {
	    if (entity == null) {
	        throw new IllegalStateException("Entity was null");
	    }
	    if (service == null) {
	        throw new IllegalStateException("Service was null");
	    }
	    try {
	        entity = getFormData();

	        // Verifica se a entidade já possui um id para determinar se é atualização ou inserção
	        if (entity.getId() == null) {
	            // Novo equipamento
	            service.saveOrUpdate(entity);
	        } else {
	            // Equipamento já existente, apenas atualizar
	            service.saveOrUpdate(entity);
	        }
	        
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		conservationStatus.setItems(FXCollections.observableArrayList("BOM", "RUIM", "NOVO", "DANIFICADO"));
	}

	public void updateFormData() {
	    if (entity == null) {
	        throw new IllegalStateException("Entity was null");
	    }

	    // Atualizar os campos com os dados da entidade
	    equipmentId.setText(entity.getId() == null ? "" : String.valueOf(entity.getId()));
	    equipmentName.setText(entity.getDescription() == null ? "" : entity.getDescription());
	    equipmentPurchase.setText(entity.getPurchaseDate() == null ? "" : sdf.format(entity.getPurchaseDate()));
	    equipmentWeight.setText(entity.getWeight() == 0.0 ? "" : String.valueOf(entity.getWeight()));
	    equipmentWidth.setText(entity.getWidth() == 0.0 ? "" : String.valueOf(entity.getWidth()));
	    equipmentLength.setText(entity.getLength() == 0.0 ? "" : String.valueOf(entity.getLength()));
	    equipmentHistory.setText(
	        entity.getHistory() != null && !entity.getHistory().isEmpty()
	            ? String.join("/ ", entity.getHistory())
	            : "Novo"
	    );

	    // Lógica para definir o status e bloquear campos conforme o tipo de operação (novo ou update)
	    if (entity.getId() == null) {  // Novo equipamento
	        conservationStatus.setValue("NOVO"); // Define o status como "NOVO"
	        conservationStatus.setDisable(true); // Desabilita o campo para edição
	        equipmentHistory.setDisable(true);
	        // Habilita os campos editáveis para novo equipamento
	        equipmentId.setDisable(false);
	        equipmentName.setDisable(false);
	        equipmentPurchase.setDisable(false);
	        equipmentWeight.setDisable(false);
	        equipmentWidth.setDisable(false);
	        equipmentLength.setDisable(false);
	    } else {  // Equipamento existente
	        // Mantém o status atual e permite edição
	        conservationStatus.setDisable(false);

	        // Desabilita os campos para edição, exceto estado de conservação e histórico
	        equipmentId.setDisable(true);
	        equipmentName.setDisable(true);
	        equipmentPurchase.setDisable(true);
	        equipmentWeight.setDisable(true);
	        equipmentWidth.setDisable(true);
	        equipmentLength.setDisable(true);
	    }

	    // Ajuste do prompt text para a data de compra
	    if (entity.getPurchaseDate() == null) {
	        equipmentPurchase.setPromptText("dia/mes/ano");
	    } else {
	        equipmentPurchase.setPromptText(""); // Limpa o prompt text se já houver uma data
	    }
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("equipmentName")) {
			labelErrorName.setText(errors.get("equipmentName"));
		}
		if (fields.contains("purchase")) {
			labelErrorPurchase.setText(errors.get("purchase"));
		}
		if (fields.contains("weight")) {
			labelErrorWeight.setText(errors.get("weight"));
		}
		if (fields.contains("width")) {
			labelErrorWidth.setText(errors.get("width"));
		}
		if (fields.contains("length")) {
			labelErrorLength.setText(errors.get("length"));
		}
		if (fields.contains("status")) {
			labelErrorStatus.setText(errors.get("status"));
		}
	}
}

package gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import loans.Employee;
import model.exceptions.ValidationException;
import models.services.EmployeeService;

public class EmployeeFormController {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private Employee entity;
	private EmployeeService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	private EmployeeListController employeeController;

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setEmployeeController(EmployeeListController employeeController) {
		this.employeeController = employeeController;
	}

	public void setEmployeeService(EmployeeService service) {
		this.service = service;
	}

	@FXML
	private Button btSaveEmployee;
	@FXML
	private TextField employeeId;
	@FXML
	private TextField employeeName;
	@FXML
	private TextField employeeRole;
	@FXML
	private TextField employeeHire;
	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorRole;
	@FXML
	private Label labelErrorHire;

	public void setEmployee(Employee obj) {
		this.entity = obj;
	}

	public void onButtonAddEmployeeAction(ActionEvent event) throws ParseException {
	    if (entity == null) {
	        throw new IllegalStateException("Entity was null");
	    }
	    if (service == null) {
	        throw new IllegalStateException("Service was null");
	    }

	    // Verifica se o funcionário já existe
	    if (service.alreadyExists(employeeName.getText())) {
	        Alerts.showAlert("Erro", null, "Funcionário já existente com o nome: " + employeeName.getText(), AlertType.ERROR);
	        return; // Interrompe o processo de salvamento
	    }

	    try {
	        entity = getFormData();
	        service.saveOrUpdate(entity);
	        notifyDataChangeListeners();
	        Alerts.showAlert(null, "O empregado " + entity.getName() + " foi salvo com sucesso", null,
	                AlertType.INFORMATION);
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

	private Employee getFormData() throws ParseException {
	    Employee obj = new Employee();
	    ValidationException exception = new ValidationException("Validation error");

	    // Validação do campo ID
	    Integer id = Utils.tryParseToInt(employeeId.getText());
	    if (id != null) {
	        obj.setId(id); // Define o ID apenas se ele for válido
	    }

	    // Validação do campo Nome
	    if (employeeName.getText() == null || employeeName.getText().trim().equals("")) {
	        exception.addError("name", "Não pode ser vazio");
	    }
	    obj.setName(employeeName.getText());

	    // Validação do campo Role
	    if (employeeRole.getText() == null || employeeRole.getText().trim().equals("")) {
	        exception.addError("role", "Não pode ser vazio");
	    }
	    obj.setRole(employeeRole.getText());

	    // Validação e formatação da Data de Admissão
	    if (employeeHire.getText() == null || employeeHire.getText().trim().equals("")) {
	        exception.addError("admissionDate", "Não pode ser vazio");
	    } else {
	        try {
	            Date admissionDate = sdf.parse(employeeHire.getText());
	            obj.setAdmission(new java.sql.Date(admissionDate.getTime()));
	        } catch (ParseException e) {
	            exception.addError("admissionDateFormat", "Formato de data inválido. O formato correto é dd/MM/yyyy.");
	        }
	    }

	    if (exception.getErrors().size() > 0) {
	        throw exception;
	    }

	    return obj;
	}

	public void updateFormData() {
	    if (entity == null) {
	        throw new IllegalStateException("Entity was null");
	    }

	    employeeId.setText(entity.getId() == null ? "" : String.valueOf(entity.getId())); // Carrega o ID do funcionário
	    employeeName.setText(entity.getName());
	    employeeRole.setText(entity.getRole());
	    employeeHire.setText(entity.getAdmission() == null ? "" : sdf.format(entity.getAdmission()));
	    if (entity.getId() != null) {
	        employeeName.setDisable(true);
	        employeeHire.setDisable(true);
	    } else {
	        employeeName.setDisable(false);
	        employeeHire.setDisable(false);
	    }
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		if (fields.contains("role")) {
			labelErrorRole.setText(errors.get("role"));
		}
		if (fields.contains("admissionDate")) {
			labelErrorHire.setText(errors.get("admissionDate"));
		} else if (fields.contains("admissionDateFormat")) {
			labelErrorHire.setText(errors.get("admissionDateHireFormat"));
		}
	}

}

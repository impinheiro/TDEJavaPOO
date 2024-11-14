package gui;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import loans.Employee;
import loans.Equipment;
import loans.Loans;
import model.exceptions.ValidationException;
import models.services.EmployeeService;
import models.services.EquipmentService;
import models.services.LoanService;

public class LoanFormController implements Initializable {
	private Loans entity;
	private LoanService service;
	private LoanListController loansController;
	private EmployeeService employeeService = new EmployeeService();
	private EquipmentService equipmentService = new EquipmentService();
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setLoansController(LoanListController controller) {
		this.loansController = controller;
	}

	@FXML
	private TextField loanId;
	@FXML
	private TextField loanCheckout;
	@FXML
	private TextField loanReturn;
	@FXML
	private TextField loanEmployee;
	@FXML
	private TextField loanEquipment;
	@FXML
	private TextArea loanNotes;
	@FXML
	private Button btSave;
	@FXML
	private Label labelErrorCheckout;
	@FXML
	private Label labelErrorEmployee;
	@FXML
	private Label labelErrorEquipment;

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
			Alerts.showAlert("Error saving the object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Loans getFormData() {
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    Loans obj = new Loans();
	    ValidationException exception = new ValidationException("Validation error");

	    // Verificar se o empréstimo está sendo devolvido
	    boolean isReturn = loanReturn.getText() != null && !loanReturn.getText().isEmpty();

	    // Validação do empregado
	    Employee employee = null;
	    try {
	        employee = employeeService.findById(Utils.tryParseToInt(loanEmployee.getText()));
	    } catch (NumberFormatException e) {
	        exception.addError("employee", "Funcionário inválido.");
	    }
	    if (employee == null) {
	        exception.addError("employee", "Funcionário é obrigatório.");
	    }
	    obj.setEmployee(employee);

	    // Validação do equipamento
	    Equipment equipment = null;
	    try {
	        equipment = equipmentService.findById(Utils.tryParseToInt(loanEquipment.getText()));
	    } catch (NumberFormatException e) {
	        exception.addError("equipment", "Equipamento inválido.");
	    }
	    if (equipment == null) {
	        exception.addError("equipment", "Equipamento é obrigatório.");
	    } else {
	        // Verifica se o equipamento já foi emprestado e não foi devolvido, mas apenas se não for uma devolução
	        if (!isReturn && service.isEquipmentAlreadyLoaned(equipment)) {
	            exception.addError("equipment", "Equipamento já está emprestado e não foi devolvido.");
	        }
	    }
	    obj.setEquipment(equipment);

	    // Validação da data de checkout
	    String checkoutText = loanCheckout.getText();
	    if (checkoutText == null || checkoutText.trim().isEmpty()) {
	        exception.addError("checkout", "Data de saída é obrigatória.");
	    }
	    try {
	        obj.setDeparture(LocalDateTime.parse(checkoutText, dtf));
	    } catch (Exception e) {
	        exception.addError("checkout", "Formato de data inválido. Use dd/MM/yyyy HH:mm.");
	    }

	    // Se for uma devolução, registra a data de retorno
	    String returnText = loanReturn.getText();
	    if (isReturn) {
	        try {
	            obj.setReturnTime(LocalDateTime.parse(returnText, dtf));
	        } catch (Exception e) {
	            exception.addError("return", "Formato de data de retorno inválido. Use dd/MM/yyyy HH:mm.");
	        }
	    }

	    obj.setNotes(loanNotes.getText());

	    // Lançar exceção de validação se houver erros
	    if (exception.getErrors().size() > 0) {
	        throw exception;
	    }

	    // Se o empréstimo já existe, apenas atualize os dados existentes (não crie um novo)
	    if (entity.getId() != null) {
	        obj.setId(entity.getId());
	    }

	    return obj;
	}

	public void setLoan(Loans loan) {
		if (loan == null) {
			this.entity = new Loans(); // Inicializa um novo objeto Loans
		} else {
			this.entity = loan;
		}
	}

	public void setLoanService(LoanService service) {
		this.service = service;
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}

	}

	public void updateFormData() {
	    if (entity == null) {
	        throw new IllegalStateException("Entity was null");
	    }

	    // Get the employee and equipment data
	    Employee employee = entity.getEmployee();
	    Equipment equipment = entity.getEquipment();

	    // Use DateTimeFormatter to format the dates
	    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    loanId.setText(entity.getId() == null ? "" : String.valueOf(entity.getId()));
	    // Format and set the dates to the text fields
	    loanCheckout.setText(entity.getDeparture() == null ? "" : entity.getDeparture().format(dtf));
	    loanReturn.setText(entity.getReturnTime() == null ? "" : entity.getReturnTime().format(dtf));

	    loanEmployee.setText(employee == null ? "" : String.valueOf(employee.getId()));
	    loanEquipment.setText(equipment == null ? "" : String.valueOf(equipment.getId()));
	    loanNotes.setText(entity.getNotes() == null ? "" : entity.getNotes());

	    // Logic to enable/disable fields based on the situation
	    if (entity.getId() == null) {
	        // New loan
	        loanReturn.setDisable(true); // Disable return date field for new loan
	    } else {
	        // Existing loan - Update
	        loanCheckout.setDisable(true);  // Disable checkout field for update
	        loanEmployee.setDisable(true);  // Disable employee field for update
	        loanEquipment.setDisable(true); // Disable equipment field for update
	    }
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	public void setEmployeeService(EmployeeService employeeService2) {
		this.employeeService = employeeService;

	}

	public void setEquipmentService(EquipmentService equipmentService) {
		this.equipmentService = equipmentService;
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		labelErrorCheckout.setText(fields.contains("checkout") ? errors.get("checkout") : "");
		labelErrorEmployee.setText(fields.contains("employee") ? errors.get("employee") : "");
		labelErrorEquipment.setText(fields.contains("equipment") ? errors.get("equipment") : "");
	}

}

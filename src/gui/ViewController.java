package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import models.services.EmployeeService;
import models.services.EquipmentService;
import models.services.LoanService;

public class ViewController implements Initializable {

	@FXML
	private MenuItem menuItemRegisterEmployee;

	@FXML
	private MenuItem menuItemRegisterEquipment;

	@FXML
	private MenuItem menuItemRegisterLoan;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemRegisterEmployee() {
		loadView("/gui/Employee.fxml", (EmployeeListController controller) ->{
			controller.setEmployeeService(new EmployeeService());
			controller.updateTableView();
		});
//		employeeDao.insert(null); 

	}

	@FXML
	public void onMenuItemRegisterEquipment() {
		loadView("/gui/Equipment.fxml", (EquipmentListController controller) -> {
			controller.setEquipmentService(new EquipmentService());
			controller.updateTableView(false);
		});
	}

	@FXML
	public void onMenuItemRegisterLoan() {
		loadView("/gui/Loans.fxml", (LoanListController controller) -> {
			controller.setLoanService(new LoanService());
			controller.updateTableView();
		});
	}

	public void onMenuItemAbout() {
		loadView("/gui/About.fxml", x -> {
		});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

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
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
//import dao.loan.DaoFactory;
//import dao.loan.EmployeeDao;
//import dao.loan.EquipmentDao;
//import dao.loan.LoansDao;
//import javafx.fxml.FXML;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.Button;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public class ViewController {
//	
//	EquipmentDao equipmentDao = DaoFactory.createEquipmentDao(); 
//	EmployeeDao employeeDao = DaoFactory.createEmployeeDao(); 
//	LoansDao loansDao = DaoFactory.createLoansDao();
//	private Button btnExit;
//
//	public VBox createMenu(Stage stage) {
//		VBox menu = new VBox();
//		menu.setSpacing(10);
//
//		Button btnRegisterLoan = new Button("Registrar novo empréstimo");
//		btnRegisterLoan.setOnAction(e -> registerLoan());
//
//		Button btnRegisterEmployee = new Button("Registrar funcionário");
//		btnRegisterEmployee.setOnAction(e -> registerEmployee());
//
//		Button btnRegisterEquipment = new Button("Registrar novo equipamento");
//		btnRegisterEquipment.setOnAction(e -> registerEquipment());
//
//		Button btnViewLoans = new Button("Ver empréstimos acontecendo");
//		btnViewLoans.setOnAction(e -> viewLoans());
//
//		Button btnReturnEquipment = new Button("Registrar retorno de equipamento");
//		btnReturnEquipment.setOnAction(e -> returnEquipment());
//
//		Button btnUpdateEquipment = new Button("Atualizar equipamento");
//		btnUpdateEquipment.setOnAction(e -> updateEquipment());
//
//		Button btnShowHistory = new Button("Mostrar histórico do equipamento");
//		btnShowHistory.setOnAction(e -> showHistoryById());
//
//		Button btnListEquipments = new Button("Listar equipamentos");
//		btnListEquipments.setOnAction(e -> listEquipments());
//
//		Button btnListEmployees = new Button("Listar funcionários");
//		btnListEmployees.setOnAction(e -> listEmployees());
//
//		menu.getChildren().addAll(btnRegisterLoan, btnRegisterEmployee, btnRegisterEquipment, btnViewLoans,
//				btnReturnEquipment, btnUpdateEquipment, btnShowHistory, btnListEquipments, btnListEmployees);
//
//		return menu;
//	}
//
//	@FXML
//	private void registerLoan() {
//		showAlert("Registrar Empréstimo", "Função para registrar um novo empréstimo.");
//	}
//
//	@FXML
//	private void registerEmployee() {
//		showAlert("Registrar Funcionário", "Função para registrar um novo funcionário.");
//	}
//
//	@FXML
//	private void registerEquipment() {
//		showAlert("Registrar Equipamento", "Função para registrar um novo equipamento.");
//	}
//
//	@FXML
//	private void viewLoans() {
//		showAlert("Ver Empréstimos", "Função para listar os empréstimos em andamento.");
//	}
//
//	@FXML
//	private void returnEquipment() {
//		showAlert("Registrar Retorno", "Função para registrar o retorno de um equipamento.");
//	}
//
//	@FXML
//	private void updateEquipment() {
//		showAlert("Atualizar Equipamento", "Função para atualizar as informações de um equipamento.");
//	}
//
//	@FXML
//	private void showHistoryById() {
//		showAlert("Mostrar Histórico", "Função para mostrar o histórico de um equipamento específico.");
//	}
//
//	@FXML
//	private void listEquipments() {
//		showAlert("Listar Equipamentos", "Função para listar todos os equipamentos.");
//	}
//
//	@FXML
//	private void listEmployees() {
//		showAlert("Listar Funcionários", employeesDao.listAll());
//	}
//
//	private void showAlert(String title, String content) {
//		Alert alert = new Alert(AlertType.INFORMATION);
//		alert.setTitle(title);
//		alert.setHeaderText(null);
//		alert.setContentText(content);
//		alert.showAndWait();
//	}
//
//	@FXML
//	private void handleExit() {
//		Stage stage = (Stage) btnExit.getScene().getWindow();
//		stage.close();
//	}
//}

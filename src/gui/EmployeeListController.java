package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loans.Employee;
import models.services.EmployeeService;

public class EmployeeListController implements Initializable, DataChangeListener {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private Employee entity;

	private EmployeeService service;

	@FXML
	private TableView<Employee> tableViewEmployee;

	@FXML
	private TableColumn<Employee, Integer> tableColumnId;

	@FXML
	private TableColumn<Employee, String> tableColumnName;

	@FXML
	private TableColumn<Employee, String> tableColumnRole;

	@FXML
	private TableColumn<Employee, Date> tableColumnAdmission;

	@FXML
	private TableColumn<Employee, Employee> tableColumnEDIT;

	@FXML
	private Button btNewEmployee;

	@FXML
	private Button addEmployee;

	@FXML
	private Button saveEmployee;

	private ObservableList<Employee> obsList;

	public void setEmployeeService(EmployeeService employeeService) {
		this.service = employeeService;

	}

	public void onBtNewEmployeeAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Employee obj = new Employee();
		createDialogForm(obj, "/gui/EmployeeForm.fxml", parentStage);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeNodes();

	}

	public void updateTableView() {
		List<Employee> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewEmployee.setItems(obsList);
		initEditButtons();
	}

	private void createDialogForm(Employee obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			// Obter o controlador do formulário
			EmployeeFormController controller = loader.getController();
			controller.setEmployee(obj);
			controller.setEmployeeController(this);
			controller.setEmployeeService(new EmployeeService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Informe os dados do empregado");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
		tableColumnAdmission.setCellValueFactory(new PropertyValueFactory<>("admission"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewEmployee.prefHeightProperty().bind(stage.heightProperty());

	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Employee, Employee>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Employee obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/gui/EmployeeForm.fxml", Utils.currentStage(event)));
			}
		});
	}

}

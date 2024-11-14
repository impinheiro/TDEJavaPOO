package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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
import loans.Loans;
import models.services.LoanService;

public class LoanListController implements Initializable, DataChangeListener {

	private LoanService service;
	private Loans entity;

	@FXML
	private TableView<Loans> tableViewLoans;

	@FXML
	private Button btNewLoan;

	@FXML
	private TableColumn<Loans, Integer> tableColumnId;

	@FXML
	private TableColumn<Loans, LocalDateTime> tableColumnCheckout;

//	@FXML
//	private TableColumn<Loans, LocalDateTime> tableColumnReturn;

	@FXML
	private TableColumn<Loans, String> tableColumnEmployeeName;

	@FXML
	private TableColumn<Loans, String> tableColumnEquipmentName;

	@FXML
	private TableColumn<Loans, String> tableColumnNotes;
	@FXML
	private TableColumn<Loans, Loans> tableColumnEDIT;

	private ObservableList<Loans> obsList;

	public void onBtNewLoanAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Loans obj = new Loans();
		createDialogForm(obj, "/gui/LoanFormController.fxml", parentStage);

	}

	private void createDialogForm(Loans obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			LoanFormController controller = loader.getController();
			controller.setLoansController(this);
			controller.setLoanService(new LoanService());
			controller.setLoan(obj);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Informe os dados do emprestimo");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void updateTableView() {
		List<Loans> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewLoans.setItems(obsList);
		initEditButtons();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnCheckout.setCellValueFactory(new PropertyValueFactory<>("departure"));
//		tableColumnReturn.setCellValueFactory(new PropertyValueFactory<>("returnTime"));
		tableColumnEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
		tableColumnEquipmentName.setCellValueFactory(new PropertyValueFactory<>("equipmentName"));
		tableColumnNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewLoans.prefHeightProperty().bind(stage.heightProperty());

	}

	public void setLoanService(LoanService service) {
		this.service = service;
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Loans, Loans>() {
			private final Button button = new Button("Registrar devolução");

			@Override
			protected void updateItem(Loans obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/LoanFormController.fxml", Utils.currentStage(event)));
			}
		});
	}

}

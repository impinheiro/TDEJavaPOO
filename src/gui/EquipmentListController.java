package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
import loans.Equipment;
import models.services.EquipmentService;

public class EquipmentListController implements Initializable,DataChangeListener {

	private EquipmentService service;
	@FXML
	private TableView<Equipment> tableViewEquipment;

	@FXML
	private TableColumn<Equipment, Integer> tableColumnId;
	@FXML
	private TableColumn<Equipment, String> tableColumnName;
	@FXML
	private TableColumn<Equipment, Date> tableColumnPurchaseDate;
	@FXML
	private TableColumn<Equipment, Double> tableColumnWeight;
	@FXML
	private TableColumn<Equipment, Double> tableColumnWidth;
	@FXML
	private TableColumn<Equipment, Double> tableColumnLength;
	@FXML
	private TableColumn<Equipment, ArrayList<String>> tableColumnHistory;
	@FXML
	private TableColumn<Equipment, String> tableColumnStatus;
	@FXML
	private TableColumn<Equipment, Equipment> tableColumnEDIT;
	@FXML
	private Button btNewEquiment;
	@FXML
	private Button btUpdateEquipent;

	private ObservableList<Equipment> obsList;

	public void setEquipmentService(EquipmentService service) {
		this.service = service;
	}

	public void onBtNewEquipmentAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Equipment obj = new Equipment();
		createDialogForm(obj, "/gui/EquipmentForm.fxml", parentStage);

	}

	public void onBtUpdateEquipmentAction() {

	}

	public void updateTableView() {
		List<Equipment> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewEquipment.setItems(obsList);
		initEditButtons();

	}
	@Override
	public void onDataChanged() {
		updateTableView();

	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	private void createDialogForm(Equipment obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			
			EquipmentFormController controller = loader.getController();
			controller.setEquipmentController(this);
			controller.setEquipmentService(new EquipmentService());
			controller.setEquipment(obj);
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
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("description"));
		tableColumnPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
		tableColumnWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
		tableColumnWidth.setCellValueFactory(new PropertyValueFactory<>("width"));
		tableColumnLength.setCellValueFactory(new PropertyValueFactory<>("length"));
		tableColumnHistory.setCellValueFactory(new PropertyValueFactory<>("history"));
		tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("conditionStatus"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewEquipment.prefHeightProperty().bind(stage.heightProperty());

	}
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Equipment, Equipment>() {
		private final Button button = new Button("Editar");
		@Override
		protected void updateItem(Equipment obj, boolean empty) {
		super.updateItem(obj, empty);
		if (obj == null) {
		setGraphic(null);
		return;
		}
		setGraphic(button);
		button.setOnAction(
		event -> createDialogForm(
		obj, "/gui/EquipmentForm.fxml",Utils.currentStage(event)));
		}
		});
		}

}

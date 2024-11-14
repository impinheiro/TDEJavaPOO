package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loans.Equipment;
import model.exceptions.EquipmentInLoanException;
import models.services.EquipmentService;

public class EquipmentListController implements Initializable, DataChangeListener {

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
	private TableColumn<Equipment, Equipment> tableColumnREMOVE;
	@FXML
	private Button btNewEquipment;
	@FXML
	private Button btUpdateEquipment;
	@FXML
	private CheckBox checkBoxAvailable;

	private ObservableList<Equipment> obsList;

	public void setEquipmentService(EquipmentService service) {
		this.service = service;
	}

	@FXML
	public void onBtNewEquipmentAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Equipment obj = new Equipment();
		createDialogForm(obj, "/gui/EquipmentForm.fxml", parentStage);
	}

	public void onCheckBoxAvailableSelected() {
		boolean test = checkBoxAvailable.isSelected();
		updateTableView(test);
	}

	public void updateTableView(boolean showOnlyAvailable) {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}

		List<Equipment> equipmentList = showOnlyAvailable ? service.findAvailable() : service.findAll();
		obsList = FXCollections.observableArrayList(equipmentList);
		tableViewEquipment.setItems(obsList);
	}

	@Override
	public void onDataChanged() {
		updateTableView(false);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		initEditButtons();
		initRemoveButtons();// Para adicionar o botão "Editar" na tabela
	}

	public void initializeWithService() {
		if (service == null) {
			throw new IllegalStateException("Service was not initialized");
		}
		updateTableView(false); // Para preencher a tabela logo na inicialização
	}

	private void createDialogForm(Equipment obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			EquipmentFormController controller = loader.getController();
			controller.setEquipmentController(this);
			controller.setEquipmentService(this.service);
			controller.setEquipment(obj);
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Informe os dados do equipamento");
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
						event -> createDialogForm(obj, "/gui/EquipmentForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Equipment, Equipment>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Equipment obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Equipment obj) {
	    Optional<ButtonType> result = Alerts.showConfirmation("Remoção de equipamento", "Tem certeza que deseja excluir o equipamento?");
	    if(result.get() == ButtonType.OK) {
	        try { 
	            service.remove(obj); // Chama o serviço para remoção
	            updateTableView(false); // Atualiza a lista de empregados
	        } catch (DbIntegrityException e) {
	            Alerts.showAlert("Erro ao remover equipamento", null, e.getMessage(), AlertType.ERROR);
	        } catch (EquipmentInLoanException e) {
	            Alerts.showAlert("Erro ao remover equipamento", null, e.getMessage(), AlertType.ERROR);
	        }
	    }
	}
}

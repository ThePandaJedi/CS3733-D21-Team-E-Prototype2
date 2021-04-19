package edu.wpi.TeamE.views;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
//import edu.wpi.TeamE.algorithms.Node;
import edu.wpi.TeamE.algorithms.Node;
import edu.wpi.TeamE.databases.makeConnection;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.wpi.TeamE.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;
import sun.reflect.generics.tree.Tree;

import java.io.IOException;
import java.util.Collections;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiceRequestStatus {

    @FXML
    JFXTreeTableView serviceRequestTable;

    @FXML
    JFXButton completeButton;

    @FXML
    JFXButton cancelButton;

    @FXML
    private void toDefault(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/wpi/TeamE/fxml/Default.fxml"));
            App.getPrimaryStage().getScene().setRoot(root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void cancelRequest(ActionEvent e) {
        serviceRequestTable.getSelectionModel().getSelectedItem();//.setStatus("Cancelled")
    }

    @FXML
    private void completeRequest(ActionEvent e) {
        serviceRequestTable.getSelectionModel().getSelectedItem();//.setStatus("Completed")
    }


    ArrayList<String> testArrayID = new ArrayList<>();
    ArrayList<String> testArrayStatus = new ArrayList<>();
    private void createTests() {
        testArrayID.add(0,"test1");
        testArrayID.add(1,"test2");
        testArrayID.add(2,"test3");
        testArrayID.add(3,"test4");
        testArrayStatus.add(0,"In Progress");
        testArrayStatus.add(1,"Completed");
        testArrayStatus.add(2,"Cancelled");
        testArrayStatus.add(3,"In Progress");
    }


    private void addToTable(String tableName, TreeItem<String> inProgress, TreeItem<String> completed, TreeItem<String> cancelled) {
        makeConnection connection = makeConnection.makeConnection();
        ArrayList<String> idArray = testArrayID;   //connection.getRequestIDs(tableName);
        ArrayList<String> statusArray = testArrayStatus;    //connection.getRequestStatus(tableName);
        for(int i = 0; i < idArray.size(); i++) {
            TreeItem<String> request = new TreeItem<>(idArray.get(i));
            if(statusArray.get(i).equals("In Progress")) {
                inProgress.getChildren().add(request);
            }
            if(statusArray.get(i).equals("Completed")) {
                completed.getChildren().add(request);
            }
            if(statusArray.get(i).equals("Cancelled")) {
                cancelled.getChildren().add(request);
            }
        }
    }


    public void prepareTable(TreeTableView serviceRequestTable) {

        TreeItem<String> rootNode = new TreeItem<>("Service Requests");

        //Setting up sub-root nodes
        TreeItem<String> inProgress = new TreeItem<>("In Progress");
        TreeItem<String> completed = new TreeItem<>("Completed");
        TreeItem<String> cancelled = new TreeItem<>("Cancelled");

        //Setting up children of sub-root nodes
        TreeItem<String> externalPatientCompleted = new TreeItem<>("External Patient Form");
        TreeItem<String> floralFormCompleted = new TreeItem<>("Floral Form");
        TreeItem<String> medicineDeliveryCompleted = new TreeItem<>("Medicine Delivery Form");
        TreeItem<String> sanitationServicesCompleted = new TreeItem<>("Sanitation Services Form");
        TreeItem<String> securityServiceCompleted = new TreeItem<>("Security Service Form");
        TreeItem<String> externalPatientInProgress = new TreeItem<>("External Patient Form");
        TreeItem<String> floralFormInProgress = new TreeItem<>("Floral Form");
        TreeItem<String> medicineDeliveryInProgress = new TreeItem<>("Medicine Delivery Form");
        TreeItem<String> sanitationServicesInProgress = new TreeItem<>("Sanitation Services Form");
        TreeItem<String> securityServiceInProgress = new TreeItem<>("Security Service Form");
        TreeItem<String> externalPatientCancelled = new TreeItem<>("External Patient Form");
        TreeItem<String> floralFormCancelled = new TreeItem<>("Floral Form");
        TreeItem<String> medicineDeliveryCancelled = new TreeItem<>("Medicine Delivery Form");
        TreeItem<String> sanitationServicesCancelled = new TreeItem<>("Sanitation Services Form");
        TreeItem<String> securityServiceCancelled = new TreeItem<>("Security Service Form");

        //Adding request forms
        createTests();
        addToTable("securityServ", securityServiceInProgress, securityServiceCompleted, securityServiceCancelled);
        addToTable("extTransport", externalPatientInProgress, externalPatientCompleted, externalPatientCancelled);
        addToTable("floralRequests", floralFormInProgress, floralFormCompleted, floralFormCancelled);
        addToTable("sanitationRequest", sanitationServicesInProgress, sanitationServicesCompleted, sanitationServicesCancelled);
        addToTable("medDelivery", medicineDeliveryInProgress, medicineDeliveryCompleted, medicineDeliveryCancelled);

        //Adding children to sub-root nodes
        inProgress.getChildren().setAll(externalPatientInProgress,floralFormInProgress,medicineDeliveryInProgress,sanitationServicesInProgress,securityServiceInProgress);
        completed.getChildren().setAll(externalPatientCompleted,floralFormCompleted,medicineDeliveryCompleted,sanitationServicesCompleted,securityServiceCompleted);
        cancelled.getChildren().setAll(externalPatientCancelled,floralFormCancelled,medicineDeliveryCancelled,sanitationServicesCancelled,securityServiceCancelled);

        //Adding sub-roots to root node
        rootNode.getChildren().setAll(inProgress,completed,cancelled);

        //Adding Root
        serviceRequestTable.setRoot(rootNode);

        //Establishing some columns that are consistent throughout all the service requests
        //Column 1 - ID
        TreeTableColumn<String, String> formColumn = new TreeTableColumn<>("Form");
        formColumn.setPrefWidth(320);
        formColumn.setCellValueFactory(new Callback<CellDataFeatures<String, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<String, String> param) {
                return new SimpleStringProperty(param.getValue().getValue());
            }
        });
        serviceRequestTable.getColumns().add(formColumn);
        //Column 2 - Location
        TreeTableColumn<Node, Number> locationColumn = new TreeTableColumn<>("Location");
        locationColumn.setPrefWidth(150);
        serviceRequestTable.getColumns().add(locationColumn);
        //Column 3 - Assignee
        TreeTableColumn<String, Number> assigneeColumn = new TreeTableColumn<>("Assignee");
        assigneeColumn.setPrefWidth(150);
        serviceRequestTable.getColumns().add(assigneeColumn);

    }

    @FXML
    void initialize() {

        prepareTable(serviceRequestTable);

    }


}

package edu.wpi.cs3733.D21.teamE.views.serviceRequestControllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import edu.wpi.cs3733.D21.teamE.App;
import edu.wpi.cs3733.D21.teamE.DB;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;

public class MedicineDelivery extends ServiceRequestFormComponents {


    ObservableList<String> locations;
    ArrayList<String> nodeID = new ArrayList<>();
    ObservableList<String> names;
    ArrayList<Integer> userID = new ArrayList<>();

    @FXML
    private JFXComboBox<String> locationInput;

    @FXML
    private JFXTextField medicineNameInput;

    @FXML
    private JFXTextField doseQuantityInput;

    @FXML
    private JFXTextField doseMeasureInput;

    @FXML
    private JFXComboBox<String> assignee;

    @FXML
    private JFXTextArea specialInstructInput;

    @FXML
    private JFXTextField signatureInput;

    @FXML
    private JFXButton cancel;

    @FXML
    private JFXButton submit;

    @FXML
    public AnchorPane appBarAnchorPane;

    @FXML
    private StackPane stackPane;

    private boolean validateInput() {

        RequiredFieldValidator validator = new RequiredFieldValidator();

        validator.setMessage("Input required");

        locationInput.getValidators().add(validator);
        medicineNameInput.getValidators().add(validator);
        doseMeasureInput.getValidators().add(validator);
        doseQuantityInput.getValidators().add(validator);
        assignee.getValidators().add(validator);
        specialInstructInput.getValidators().add(validator);
        signatureInput.getValidators().add(validator);

        return  locationInput.validate() && medicineNameInput.validate() && doseMeasureInput.validate() && doseQuantityInput.validate() && assignee.validate() && specialInstructInput.validate() && signatureInput.validate();

    }

    @FXML
    private void saveData(ActionEvent event) {

        if(validateInput()) {
            int locIndex = locationInput.getSelectionModel().getSelectedIndex();
            int userIndex = assignee.getSelectionModel().getSelectedIndex();

            String location = nodeID.get(locIndex);
            String name = medicineNameInput.getText();
            String doseMeasure = doseMeasureInput.getText();
            int doseQuantity = Integer.parseInt(doseQuantityInput.getText());
            int assigned = userID.get(userIndex);
            String specialInstructions = specialInstructInput.getText();
            String signature = signatureInput.getText();

            DB.addMedicineRequest(App.userID, assigned, location, name, doseQuantity, doseMeasure, specialInstructions, signature);
            super.handleButtonSubmit(event);
        }

    }

    @FXML
    void initialize() {
        locations = DB.getAllNodeLongNames();
        nodeID = DB.getListOfNodeIDS();
        //TODO add user type
        names = DB.getAssigneeNames("Add user type here");
        userID = DB.getAssigneeIDs("Add user type here");

        locationInput.setItems(locations);
        assignee.setItems(names);


        assert locationInput != null;
        assert medicineNameInput != null;
        assert doseQuantityInput != null;
        assert doseMeasureInput != null;
        assert specialInstructInput != null;
        assert signatureInput != null;
        assert submit != null;
        assert cancel != null;

        //init appBar
        javafx.scene.Node appBarComponent = null;
        try {
            App.setShowHelp(false); // show help or not
            App.setShowLogin(true); // show login or not
            App.setPageTitle("Medicine Delivery (Shannen Lin)"); //set AppBar title
            App.setHelpText(""); //set help text
            App.setStackPane(stackPane); // required for dialog boxes, otherwise set null?
            appBarComponent = FXMLLoader.load(getClass().getResource("/edu/wpi/cs3733/D21/teamE/fxml/AppBarComponent.fxml"));
            appBarAnchorPane.getChildren().add(appBarComponent); //add FXML to this page's anchorPane element
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

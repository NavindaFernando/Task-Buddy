package controller;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {

    public PasswordField txtConfirmPassword;
    public PasswordField txtNewPassword;
    public TextField txtUserName;
    public TextField txtEmail;
    public Label lblPasswordNotMatch1;
    public Label lblPasswordNotMatch2;
    public Button btnRegister;
    public Label lblId;
    public AnchorPane root;

    public void initialize(){
        setVisibility(false);
        setDisableCommon(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword.equals(confirmPassword)){
            setBorderColor("transparent");
            setVisibility(false);
            register();
        }else {
            setBorderColor("red");
            setVisibility(true);
            txtNewPassword.requestFocus();
        }

        // check connection
        Connection connection = DBConnection.getInstance().getConnection();
        System.out.println(connection);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        setDisableCommon(false);
        txtUserName.requestFocus();
        autoGenerateID();
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select uid from user order by uid desc limit 1");
            boolean isExist = resultSet.next();

            if (isExist){
                String userId = resultSet.getString(1);
                userId = userId.substring(1, userId.length());
                int intId = Integer.parseInt(userId);
                intId++;

                if (intId < 10){
                    lblId.setText("U00"+intId);
                }else if (intId < 100){
                    lblId.setText("U0"+intId);
                }else {
                    lblId.setText("U"+intId);
                }
            }else {
                lblId.setText("U001");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void register(){
        String id = lblId.getText();
        String userName = txtUserName.getText();
        String email = txtEmail.getText();
        String password = txtConfirmPassword.getText();

        if (userName.trim().isEmpty()){
            txtUserName.requestFocus();
        }else if (email.trim().isEmpty()){
            txtEmail.requestFocus();
        }else if (txtNewPassword.getText().trim().isEmpty()){
            txtNewPassword.requestFocus();
        }else if(password.trim().isEmpty()){
            txtConfirmPassword.requestFocus();
        }else {
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,userName);
                preparedStatement.setObject(3,email);
                preparedStatement.setObject(4,password);

                preparedStatement.executeUpdate();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login");
                primaryStage.centerOnScreen();

            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: "+color);
        txtConfirmPassword.setStyle("-fx-border-color: "+color);
    }

    public void setVisibility(boolean isVisible){
        lblPasswordNotMatch1.setVisible(isVisible);
        lblPasswordNotMatch2.setVisible(isVisible);
    }

    public void setDisableCommon(boolean isDisable){
        btnRegister.setDisable(isDisable);
        txtUserName.setDisable(isDisable);
        txtEmail.setDisable(isDisable);
        txtNewPassword.setDisable(isDisable);
        txtConfirmPassword.setDisable(isDisable);
    }
}

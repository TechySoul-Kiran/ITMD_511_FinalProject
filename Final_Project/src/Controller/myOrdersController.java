package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.ModelTableUsersOrder;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.input.MouseEvent;

import javafx.scene.control.TableView;

import javafx.scene.control.TableColumn;

public class myOrdersController implements Initializable {
	
	Connection con = null;
    PreparedStatement st = null, st1=null;
    ResultSet rs = null, rs1=null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		loggedUser.setText(LoginController.getLoggedUserName());
		try {
			con = DriverManager.getConnection("jdbc:mysql://b89b4675b5cd41:7a58b101@us-cdbr-east-03.cleardb.com/heroku_3cdd412dc4ff1f0?reconnect=true");
	    	
			SelectMyOrders();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	private Text loggedUser;
	@FXML
	private Button logoutButton;
	@FXML
	private Button userHome;

    @FXML
    private TableView<ModelTableUsersOrder> myOrderTable;

    @FXML
    private TableColumn<ModelTableUsersOrder, String> col_s_no;

    @FXML
    private TableColumn<ModelTableUsersOrder, String> col_pid;

    @FXML
    private TableColumn<ModelTableUsersOrder, String> col_pname;

    @FXML
    private TableColumn<ModelTableUsersOrder, String> col_qty;
    
	@FXML
	private TextField srNo;
	@FXML
	private TextField ProductName;
	@FXML
	private TextField ProductID;
	@FXML
	private TextField qtyDonated;
	@FXML
	private Button cancelOrder;

	// Event Listener on Button[#logoutButton].onAction
	@FXML
	public void logoutButtonPressed(ActionEvent event) throws SQLException {
		// TODO Autogenerated	
		Alert alert = new Alert(AlertType.CONFIRMATION, "Do you want to logout?", ButtonType.YES, ButtonType.NO);
		alert.setHeaderText("");
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES)
		{	
			con.close();
			System.exit(0);
		}
	}
	// Event Listener on Button[#userHome].onAction
	@FXML
	public void userHomeButtonPressed(ActionEvent event) throws IOException {
		// TODO Autogenerated		
		Parent userView = FXMLLoader.load(getClass().getResource("/View/user.fxml"));
		Scene userScene = new Scene(userView);
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		window.setScene(userScene);
		window.show();
		userScene.getWindow().centerOnScreen();
	}
	// Event Listener on TableView[#myOrderTable].onMouseClicked
	@FXML
	public void setCellValue(MouseEvent event) {
		// TODO Autogenerated
		ModelTableUsersOrder data=myOrderTable.getSelectionModel().getSelectedItem();
		if (data!=null)
		{
			srNo.setText(data.getId());
			ProductID.setText(data.getPid());
			ProductName.setText(data.getPname());
			qtyDonated.setText(data.getQty());
		}
	}
	// Event Listener on Button[#cancelOrder].onAction
	@FXML
	public void cancelOrderButtonPressed(ActionEvent event) throws SQLException {
		// TODO Autogenerated		
		if(srNo.getText().equals(""))
        {
            Alert alert=new Alert(AlertType.ERROR, "Please select an order from list to process!");
            alert.show();
        }
		else
		{		
			Alert alert = new Alert(AlertType.CONFIRMATION, "are you sure you want to cancel this ordert?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
	
			if (alert.getResult() == ButtonType.YES)
			{
				//First check if Product is available in database
				
				int quantityInDB=0;
				String query = "SELECT id from product where id=?";
		           
                st = con.prepareStatement(query);
                st.setInt(1, Integer.parseInt(ProductID.getText()));
                rs=st.executeQuery(); 
                if (rs.next()) 
                {
                	//Product is available in database, so just update the quantity
                	//first get the quantity from database
                	String query1 = "SELECT * from product";
                	st = con.prepareStatement(query1);
                	rs1=st.executeQuery();
                	while(rs1.next())
                	{
                		if(rs1.getInt("id") == Integer.parseInt(ProductID.getText()))
                		{
                			quantityInDB=Integer.parseInt(rs1.getString("quantity"));
                		}
                	}
                	
                	//adding the quantity from database and ordered by user to add it back
                	int newQuantity=quantityInDB+Integer.parseInt(qtyDonated.getText());              	   	
                    
                	query = "update product set quantity=? where id=?";
                    
                    st = con.prepareStatement(query);  
                    st.setInt(1, newQuantity);
                    st.setInt(2, Integer.parseInt(ProductID.getText()));
                    
                    st.execute();                     
                    alert=new Alert(AlertType.INFORMATION, "Order Cancelled!");
                    alert.show();
                	
                } 
                else
                {
                	//if product not found in the list add it as a new product
                	query = "INSERT INTO product VALUES (?,?,?)";
                    st = con.prepareStatement(query);
                    st.setInt(1, Integer.parseInt(ProductID.getText()));
                    st.setString(2, ProductName.getText());
                    st.setInt(3, Integer.parseInt(qtyDonated.getText()));
                    st.execute(); 
                    alert=new Alert(AlertType.INFORMATION, "Order Cancelled!");
                    alert.show();
                }
                
                //after adding the product back to list, remove it from the users order list
                query = "delete from productordered where id=?";		           
                st = con.prepareStatement(query);
                st.setInt(1, Integer.parseInt(srNo.getText()));
                st.execute();                
                
                myOrderTable.getItems().clear();
                SelectMyOrders();
                clearFields();                
			}
		}
	}
	
	private void clearFields() {
		// TODO Auto-generated method stub
		srNo.setText("");
		ProductID.setText("");
		ProductName.setText("");
		qtyDonated.setText("");
	}

	ObservableList<ModelTableUsersOrder> oblist= FXCollections.observableArrayList();
	public void SelectMyOrders() throws SQLException
	{
		String query = "SELECT * from productordered Where customerID=?";
        st = con.prepareStatement(query);
        st.setInt(1, LoginController.getLoggedUserID());
        rs = st.executeQuery(); 
        while(rs.next())
        {
        	oblist.add(new ModelTableUsersOrder(rs.getString("id"), rs.getString("productID"), rs.getString("productName"), rs.getString("quantity")));
        }	    
        col_s_no.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_pid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        col_pname.setCellValueFactory(new PropertyValueFactory<>("pname"));
        col_qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        
        myOrderTable.setItems(oblist);  
		
	}
	
}

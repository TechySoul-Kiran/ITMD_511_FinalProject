package Testing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

public class Testing {

	//login with correct credentials should pass
	@Test
	public void loginTest1() throws SQLException {
		Connection con = null;	
		PreparedStatement pst = null;
	    ResultSet rs = null;
	    con = DriverManager.getConnection("jdbc:mysql://b89b4675b5cd41:7a58b101@us-cdbr-east-03.cleardb.com/heroku_3cdd412dc4ff1f0?reconnect=true");
		
	    String query = "SELECT * from userlogin WHERE username=? and password=? and usertype=?";
		
        pst = con.prepareStatement(query);
        pst.setString(1, "kiran");
        pst.setString(2, "12345");
        pst.setString(3, "admin");
        rs=pst.executeQuery();
        Assert.assertTrue(rs.next());
	}
	
	
	//login with wrong credentials should fail
	@Test
	public void loginTest2() throws SQLException
	{
		Connection con = null;	
		PreparedStatement pst = null;
	    ResultSet rs = null;
	    con = DriverManager.getConnection("jdbc:mysql://b89b4675b5cd41:7a58b101@us-cdbr-east-03.cleardb.com/heroku_3cdd412dc4ff1f0?reconnect=true");
		
	    String query = "SELECT * from userlogin WHERE username=? and password=? and usertype=?";
		
        pst = con.prepareStatement(query);
        pst.setString(1, "kira");
        pst.setString(2, "12345");
        pst.setString(3, "admin");
        rs=pst.executeQuery();
        Assert.assertFalse(rs.next());
	}
	

}

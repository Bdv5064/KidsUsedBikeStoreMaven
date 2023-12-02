// Import the first two package classes and there associated dependencies, if need be

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class StoreTest {
    private Connection conn;


    @BeforeEach
    public void setup() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/KidsUsedBikeStore", "root", "SQL3f=uTj!S(.&_qPcwyn"); // Use your own MySQL login name and password
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSignUp() throws SQLException {
        Store store = new Store(conn);
        // Simulate user input for sign-up
        ByteArrayInputStream inContent = new ByteArrayInputStream("Jon\nVar\njon@example.com\n1234567890\n123 Main St".getBytes());
        System.setIn(inContent);


        store.signUp();
        assertNotNull(store.getCurrentCustomer());
    }


    @Test
    public void testReturnProcess() throws SQLException {
        // ByteArrayInputStream simulates user input for returning a purchased bike
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);


        // Perform the return process
        Store store = new Store(conn);
        store.returnPurchase();
    }
}

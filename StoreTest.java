import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main2Test {
    private Connection testConnection;

    @BeforeEach
    public void setUp() {
        // Set up a test database connection
        try {
            testConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/TestDatabase", "testUser", "testPassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSignUp() {
        // Create a test store with the test connection
        Store store = new Store(testConnection);

        // Mock user input for testing
        String mockInput = "John\nDoe\njohn.doe@example.com\n123456789\nTest Address";
        System.setIn(new java.io.ByteArrayInputStream(mockInput.getBytes()));

        // Execute the signUp method
        store.signUp();

        // Retrieve the current customer from the store
        Store.Customer currentCustomer = store.getCurrentCustomer();

        // Validate the customer information
        assertNotNull(currentCustomer);
        assertEquals("John", currentCustomer.getName());
        assertEquals("john.doe@example.com", currentCustomer.getEmail());
    }

    @Test
    public void testShop() {
        // Create a test store with the test connection
        Store store = new Store(testConnection);

        // Mock user input for testing
        String mockInput = "1\n0\n";
        System.setIn(new java.io.ByteArrayInputStream(mockInput.getBytes()));

        // Execute the shop method
        store.shop();

        // Retrieve the total price from the store
        double totalPrice = store.getTotalPrice();

        // Validate the total price
        assertEquals(149.99, totalPrice);
    }

    // Add more test methods for other functionalities...

    // After all tests, close the test connection
    // @AfterAll
    // public void tearDown() {
    //     try {
    //         testConnection.close();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    // }
}

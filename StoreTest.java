import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;

public class StoreTest {

    private ByteArrayOutputStream mockOutput;
    private PrintStream originalSystemOut;
    private InputStream originalSystemIn;

    @BeforeEach
    public void setUp() {
        // Save the original System.out and System.in
        originalSystemOut = System.out;
        originalSystemIn = System.in;

        // Set up a stream to capture System.out
        mockOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(mockOutput));
    }

    @Test
    public void testSignUp() {
        // Create a test store with the test connection
        Store store = new Store();

        // Mock user input for testing
        String mockInput = "John\nDoe\njohn.doe@example.com\n123456789\nTest Address";
        System.setIn(new ByteArrayInputStream(mockInput.getBytes()));

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
        Store store = new Store();

        // Mock user input for testing
        String mockInput = "1\n0\n";
        System.setIn(new ByteArrayInputStream(mockInput.getBytes()));

        // Execute the shop method
        store.shop();

        // Retrieve the total price from the store
        double totalPrice = store.getTotalPrice();

        // Validate the total price
        assertEquals(149.99, totalPrice);
        assertEquals(129.99, totalPrice);
        assertEquals(89.99, totalPrice);
        assertEquals(109.99, totalPrice);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1\n0\n", "2\n0\n", "3\n0\n"})
    public void testShoppingWithDifferentInputs(String userInput) {
        Store store = new Store();
        store.welcome();
        store.signUp();

        // Mock user input for testing
        ByteArrayInputStream mockInput = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(mockInput);

        // Redirect output to capture it for assertions
        System.setOut(new PrintStream(mockOutput));

        store.shop();

        // Assert your expectations based on the provided input
    }

    @Test
    public void testReturnPurchase() {
        Store store = new Store();
        store.welcome();
        store.signUp();

        // Mock user input for testing
        ByteArrayInputStream mockInput = new ByteArrayInputStream("-1\n".getBytes());
        System.setIn(mockInput);

        // Redirect output to capture it for assertions
        System.setOut(new PrintStream(mockOutput));

        store.returnPurchase();

        // Validate the output contains expected messages
        assertTrue(mockOutput.toString().contains("Select the bike you would like to return:"));
        assertTrue(mockOutput.toString().contains("Invalid. Please select a valid number."));
    }

    @Test
    public void testShopping() {
        Store store = new Store();
        store.welcome();
        store.signUp();

        // Mock user input for testing (you can use a testing library for more advanced input mocking)
        ByteArrayInputStream mockInput = new ByteArrayInputStream("1\n0\n".getBytes());
        System.setIn(mockInput);

        // Redirect output to capture it for assertions
        System.setOut(new PrintStream(mockOutput));

        store.shop();

        // Validate the output contains expected messages
        assertTrue(mockOutput.toString().contains("Thank you for shopping with us!"));
        assertTrue(mockOutput.toString().contains("Total Price: $"));
    }

    @AfterEach
    public void tearDown() {
        // Restore the original System.out and System.in
        System.setOut(originalSystemOut);
        System.setIn(originalSystemIn);

        // Close the ByteArrayOutputStream
        try {
            mockOutput.close();
        } catch (Exception ignored) {
        }
    }
}

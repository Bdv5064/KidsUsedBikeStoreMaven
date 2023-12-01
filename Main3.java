import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;

// Abstraction: define abstract class for the base type of all products
abstract class Product {
    private final String name;
    private final double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public abstract String getType();

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

// Inheritance: extend the Bike class from the Product class
class Bike extends Product {
    private final BikeCategory category;

    public Bike(String name, BikeCategory category, double price) {
        super(name, price);
        this.category = category;
    }

    public BikeCategory getCategory() {
        return category;
    }

    // Overriding: The getType() method is declared in the abstract class Product and overridden in the Bike class
    @Override
    public String getType() {
        return "Bike";
    }


    // Static Methods: add a static method to the Bike class
    public static void printBikeDetails(Bike bike) {
        System.out.println("Bike Details: " + bike.getName() + ", Category: " + bike.getCategory() + ", Price: $" + bike.getPrice());
    }
}

// Interfaces: define an interface for database operations
interface DatabaseOperations {
    void saveToDatabase();

    void retrieveFromDatabase();
}

class Store implements DatabaseOperations {

    // Array Lists: modify the Store class to use ArrayList instead of a simple list
    private final List<Product> inventory = new ArrayList<>();
    private double totalPrice = 0.00;
    private Customer currentCustomer;
    private final List<Customer> customers = new ArrayList<>();
    private final Blockchain blockchain = new Blockchain();
    private final Connection conn;
    private int quantity = 0;
    private final List<Product> selectedProducts = new ArrayList<>();

    private int generatedOrderId = 1;
    private int generatedCustomerId = 1;


    public int generatedProductId = 1;
    private void initializeProducts() {
        inventory.clear();
        inventory.add(new Bike("(Trailcraft) Mountain Bike", BikeCategory.MOUNTAIN_BIKE, 149.99));
        inventory.add(new Bike("(Marin) Road Bike", BikeCategory.ROAD_BIKE, 129.99));
        inventory.add(new Bike("(AVASTA) BMX Bike", BikeCategory.BMX_BIKE, 89.99));
        inventory.add(new Bike("(Firmstrong) Cruiser Bike", BikeCategory.CRUISER_BIKE, 109.99));
    }

    public Store(Connection conn) throws SQLException {
        this.conn = conn;
        // Hardcode products into the inventory
        initializeProducts();
        // Add bikes to the database
        try {
            // Use a batch insert to improve efficiency
            String insertQuery = "INSERT INTO BikeInventory (BikeName, BikeCategory, Price) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                for (Product product : inventory) {
                    stmt.setString(1, product.getName());
                    if (product instanceof Bike) {
                        stmt.setString(2, ((Bike) product).getCategory().toString());
                    }
                    stmt.setDouble(3, product.getPrice());
                    stmt.addBatch();
                }
                // Execute the batch insert
                stmt.executeBatch();
            }
            // Commit the changes
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        }
    }

    public void welcome() {
        System.out.println("Welcome to the Used Bikes for Kids Store!");
        System.out.println("Sign Up Below");
    }
    public void resetShoppingState() {
        // Reset variables and processes related to shopping
        totalPrice = 0.00;
    }
    public void signUp() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter first name: ");
        String fName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        // Insert customer information into the Customer Details table
        try {
            String insertCustomerQuery = "INSERT INTO CustomerDetails (FName, LName, EMail, Phone, Address) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertCustomerQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, fName);
                stmt.setString(2, lName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, address);
                stmt.executeUpdate();

                // Get the generated customer ID

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedCustomerId = generatedKeys.getInt(1);
                        System.out.println("Customer ID: " + generatedCustomerId);
                    } else {
                        throw new SQLException("Creating customer failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        currentCustomer = new Customer(fName, lName, email, phone, address);
        customers.add(currentCustomer);
        System.out.println("Sign-up successful! Welcome, " + fName + "!");
    }

    public void displayInventory() {
        System.out.println("Available Products:");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i + 1) + ". " + inventory.get(i).getName());
        }
    }

    public void shop() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean continueShopping = true;
        Product selectedProduct = null;
        Map<Product, Integer> selectedProducts = new HashMap<>();

        //List<Product> selectedProducts = new ArrayList<>();


        while (continueShopping) {
            displayInventory();
            System.out.print("Enter a selection to purchase (0 to quit, -1 to return): ");
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= inventory.size()) {
                selectedProduct = inventory.get(choice - 1);

                System.out.print("Enter the quantity: "); // The user is prompted to enter the quantity for each selected product
                quantity = scanner.nextInt();

                totalPrice += selectedProduct.getPrice() * quantity; // The total price is calculated based on the product's price multiplied by the quantity.

                // Add the selected product to the list with the specified quantity
                selectedProducts.put(selectedProduct, quantity);

                System.out.println("You've added " + quantity + " " + selectedProduct.getName() + "(s) to your cart.");
                // Remove the selected product from the inventory
                inventory.remove(choice - 1);
                //The loop continues until the user decides to quit (enters 0) or return (enters -1).
            } else if (choice == 0) {
                continueShopping = false;
            } else if (choice == -1) {
                returnPurchase();
            } else {
                System.out.println("Invalid input. Please select a valid product number.");
            }
        }


        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.println("Product: " + product.getName() + ", Quantity: " + quantity);
        }
        System.out.println("Total Price: $" + totalPrice);
        System.out.print("Enter the amount of cash you're paying with: $");
        double payment = scanner.nextDouble();
        double change = payment - totalPrice;
        // Round the change to 2 decimal places
        change = Math.round(change * 100.0) / 100.0;
        Block transaction = null;
        if (currentCustomer == null) {
            System.out.println("Please sign up before making a purchase.");
            signUp();
        }
        if (change >= 0) {
            System.out.println("Change: $" + change);
            // Create a new block for the transaction
            transaction = new Block(0, null, null, null, 0.00);
            blockchain.addBlock(transaction);

            // Remove the purchased product from the database

//            try {
//                PreparedStatement stmt = conn.prepareStatement("DELETE FROM BikeInventory WHERE BikeName = ? AND BikeCategory = ? AND Price = ?");
//                stmt.setString(1, selectedProduct.getName());
//                if (selectedProduct instanceof Bike) {
//                    stmt.setString(2, ((Bike) selectedProduct).getCategory().toString());
//                }
//                stmt.setDouble(3, selectedProduct.getPrice()); // Convert price to string:  I did Double.toString(selectedProduct.getPrice()
//                stmt.executeUpdate();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
            System.out.println("Thank you for shopping with us!");
            // Print receipt
            System.out.println("Receipt:");
            System.out.println("Customer: " + currentCustomer.getName());
            System.out.println("Email: " + currentCustomer.getEmail());
            for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                System.out.println("Product Purchased: " + product.getName());
                System.out.println("Quantity: " + quantity);
                System.out.println("Price: $" + product.getPrice());
            }
            System.out.println("Total Price: $" + totalPrice);
            System.out.println("Payment: $" + payment);
            System.out.println("Change: $" + change);
            System.out.println("Transaction Hash: " + transaction.getHash());
        } else {
            System.out.println("Insufficient payment. Please pay the full amount.");
        }
        System.out.println("OrderID being used in OrderDetails: " + generatedOrderId);
        Date dateOfPurchase = new Date();
        String insertOrdersQuery = "INSERT INTO Orders ( CustID, DateOfPurchase, TotalPrice) VALUES ( ?, ?, ?)";
        try (PreparedStatement ordersStmt = conn.prepareStatement(insertOrdersQuery, Statement.RETURN_GENERATED_KEYS)) {
            ordersStmt.setInt(1, generatedCustomerId); // Use the generated customer ID
            ordersStmt.setDate(2, new java.sql.Date(dateOfPurchase.getTime())); // Using the current date
            ordersStmt.setDouble(3, totalPrice);
            ordersStmt.executeUpdate();

            // Get the generated order ID
            try (ResultSet generatedKeys = ordersStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedOrderID = generatedKeys.getInt(1);
                    System.out.println("Order ID: " + generatedOrderID);


                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Your date of purchase
        double totalPrice = this.totalPrice;  // Your total price
        int orderId = generatedOrderId;
        int custId = generatedCustomerId;
        int productId = generatedProductId;
        int bikeID = 0;
        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (product instanceof Bike) {
                String bikeName = product.getName();
                bikeID = getBikeIDFromDatabase(bikeName);
            }
            String insertOrderDetailsQuery = "INSERT INTO OrderDetails (OrderID, ProductID, CustID, DateOfPurchase, Price ,Quantity, ProductName, THash) VALUES (?, ?, ?, ?,?,?,?,?)";
            try (PreparedStatement orderDetailsStmt = conn.prepareStatement(insertOrderDetailsQuery, Statement.RETURN_GENERATED_KEYS)) {
                orderDetailsStmt.setInt(1, generatedOrderId);
                orderDetailsStmt.setInt(2, bikeID); // Assuming ProductID 1 for simplicity
                orderDetailsStmt.setInt(3, custId);
                orderDetailsStmt.setDate(4, new java.sql.Date(dateOfPurchase.getTime())); // Using the current date
                orderDetailsStmt.setDouble(5, product.getPrice());
                orderDetailsStmt.setInt(6, quantity);
                orderDetailsStmt.setString(7, product.getName());
                orderDetailsStmt.setString(8, transaction.getHash());


                orderDetailsStmt.executeUpdate();

                // Get the generated order ID
                try (ResultSet generatedKeys = orderDetailsStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedOrderDetailID = generatedKeys.getInt(1);
                        System.out.println("OrderDetails ID: " + generatedOrderDetailID);
                    } else {
                        throw new SQLException("Creating order details failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        generatedOrderId++;
        generatedCustomerId++;
        initializeProducts();


    }
    private int getBikeIDFromDatabase(String bikeName) throws SQLException {
        int bikeID = 0;
        String query = "SELECT BikeID FROM BikeInventory WHERE BikeName = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bikeName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    bikeID = resultSet.getInt("BikeID");
                }
            }
        }

        return bikeID;
    }


    public void returnPurchase() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select the product you would like to return:");
        int returnChoice = scanner.nextInt();
        Product returnedProduct = null;

        if (returnChoice >= 1 && returnChoice <= inventory.size()) {
            returnedProduct = inventory.get(returnChoice - 1);
            totalPrice -= returnedProduct.getPrice();
            System.out.println("You've returned " + returnedProduct.getName() + ". Refund amount: $" + returnedProduct.getPrice());
        } else {
            System.out.println("Invalid. Please select a valid number.");
        }
    }

    @Override
    public void saveToDatabase() {
        // Implement save logic
        // (e.g., save the state of the store, inventory, customers, etc., to the database)
    }

    @Override
    public void retrieveFromDatabase() {
        // Implement retrieval logic
        // (e.g., load the state of the store, inventory, customers, etc., from the database)
    }

    public Object getCurrentCustomer() {
        return customers;
    }

    class Customer {
        public String name;
        public String fName;
        public String lName;
        public String email;
        public String phone;
        public String address;

        public String getName() {
            return fName + " " + lName;
        }

        public String getEmail() {
            return email;
        }

        // Overloading: overloaded constructor in the Customer class
        public Customer(String fName, String lName, String email, String phone, String address) {
            this.fName = fName;
            this.lName = lName;
            this.email = email;
            this.phone = phone;
            this.address = address;
        }
    }
}

class Blockchain {
    private final List<Block> chain;

    public Blockchain() {
        chain = new ArrayList<>();
        // Add the genesis block
        chain.add(new Block(0, null, null, null, 0));
    }

    public void addBlock(Block block) {
        Block previousBlock = getLatestBlock();
        block.setPreviousHash(previousBlock.getHash());
        block.setHash(block.calculateHash());
        chain.add(block);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
}

// Hashing & Security: The Block class calculates a SHA-256 hash based on various data, demonstrating a basic form of hashing for security
class Block {
    private final Store.Customer customer;
    private int index;
    private long timestamp;
    private final Product product;
    private final double totalPrice;
    private String previousHash;
    private String hash;


    public Block(int index, String previousHash, Store.Customer currentCustomer, Product selectedProduct, double totalPrice) {
        this.index = index;
        this.timestamp = new Date().getTime();
        this.customer = currentCustomer;
        this.product = selectedProduct;
        this.totalPrice = totalPrice;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int nonce = 0;
            String input;

            while (true) {
                String customerName = (customer != null) ? customer.getName() : "";
                String productName = (product != null) ? product.getName() : "";
                double productPrice = (product != null) ? product.getPrice() : 0.0;
                input = index + timestamp + previousHash + customerName + productName + productPrice + totalPrice + nonce;
                byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();

                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                String hash = hexString.toString();

                // Check if the hash starts with "00"
                if (hash.startsWith("400")) {
                    return hash;
                }

                // If not, increment the nonce and try again
                nonce++;
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}

public class Main3 {
    public static void main(String[] args) {
        try {
            // Establish a connection
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/KidsUsedBikeStore", "root", "123qwe"); // Use your own MySQL login name and password

            System.out.println("Connected to the database");

            // Create a statement
            Statement stmt = conn.createStatement();

            // SQL statement for creating a new table (BikeInventory)
            String createBikeInventoryTable = "CREATE TABLE IF NOT EXISTS BikeInventory (" +
                    "BikeID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "BikeName VARCHAR(255), " +
                    "BikeCategory VARCHAR(255), " +
                    "Price DECIMAL (10, 2)" +
                    ");";
            stmt.execute(createBikeInventoryTable);

            // SQL statement for creating a new table (CustomerDetails)
            String createCustomerTable = "CREATE TABLE IF NOT EXISTS CustomerDetails (" +
                    "CustID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "FName VARCHAR(255), " +
                    "LName VARCHAR(255), " +
                    "EMail VARCHAR(255), " +
                    "Phone VARCHAR(255), " +
                    "Address VARCHAR(255)" +
                    ");";
            stmt.execute(createCustomerTable);

            String createOrdersTable = "CREATE TABLE IF NOT EXISTS Orders (" +
                    "OrderID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "CustID INT, " +
                    "DateOfPurchase DATE, " +
                    "TotalPrice DOUBLE, " +
                    "FOREIGN KEY (CustID) REFERENCES CustomerDetails(CustID)" +
                    ");";
            stmt.execute(createOrdersTable);

            // SQL statement for creating another table (OrderDetails) with foreign key referencing Customer(CustID)
            String createOrdersDetailsTable = "CREATE TABLE IF NOT EXISTS OrderDetails (" +
                    "OrderDetailID INT AUTO_INCREMENT PRIMARY KEY, " +
                    "OrderID INT, " +
                    "ProductID INT, " +
                    "ProductName VARCHAR(255), " +
                    "Quantity INT, " +
                    "CustID INT, " +
                    "DateOfPurchase DATE, " +
                    "Price DECIMAL (10, 2), " +
                    "THash VARCHAR(255)," +
                    "FOREIGN KEY (CustID) REFERENCES CustomerDetails(CustID), " +
                    "FOREIGN KEY (OrderID) REFERENCES Orders(OrderID)" +
                    ");";
            // Changed TotalPrice DECIMAL(10, 2) to VARCHAR(255) so that totalPrice is converted to a String and is read that way in MySQL
            //Did not quite work
            stmt.execute(createOrdersDetailsTable);

            System.out.println("Tables created successfully");

            Store store = new Store(conn);
            boolean continueApp = true;
            Scanner scanner = new Scanner(System.in);
            while (continueApp) {
                store.welcome();
                store.signUp();
                store.shop();
                //Resets shopping state like price
                store.resetShoppingState();

                // Ask the user if they want to continue
                System.out.print("Do you want to continue shopping? (yes/no): ");

                String userResponse = scanner.nextLine().toLowerCase();

                if (userResponse.equals("no")) {
                    continueApp = false;
                }

            }

            // Closing Database Connection: The MySQL database connection is properly closed in the main method using conn.close()
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

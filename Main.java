import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.lang.Object;

public class Main {

    public static void main(String[] args) {

        try {
            // Establish a connection
           Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/KidsUsedBikeStore", "root", "SQLW@ta$h!#914");

            // Create a statement
           // Statement stmt = conn.createStatement();

            // SQL statement for creating a new table
          //  String sql = "CREATE TABLE IF NOT EXISTS Customer (" +
          //          "CustID INT PRIMARY KEY, " +
          //          "FName VARCHAR(255), " +
          //          "LName VARCHAR(255), " +
          //          "EMail VARCHAR(255), " +
          //         "Phone VARCHAR(255), " +
           //         "Address VARCHAR(255)" +
          //          ");";
          //  stmt.execute(sql);

          //  sql = "CREATE TABLE IF NOT EXISTS BikeInventory (" +
          //          "BikeID INT PRIMARY KEY, " +
          //          "BikeMake VARCHAR(255), " +
          //          "BikeModel VARCHAR(255), " +
          //          "Price DECIMAL(10, 2)" +
          //          ");";
           // stmt.execute(sql);

          //  sql = "CREATE TABLE IF NOT EXISTS Orders (" +
          //          "OrderID INT PRIMARY KEY, " +
          //          "BikeID INT, " +
          //          "CustID INT, " +
           //         "DateOfPurchase DATE, " +
          //          "TotalPrice DECIMAL(10, 2), " +
           //         "FOREIGN KEY (BikeID) REFERENCES BikeInventory(BikeID)" +
          //          ");";
          //  stmt.execute(sql);

         //   sql = "CREATE TABLE IF NOT EXISTS OrderDetails (" +
         //           "OrderID INT PRIMARY KEY, " +
         //           "CustID INT, " +
         //           "FName VARCHAR(255), " +
         //           "LName VARCHAR(255), " +
         //           "DateOfPurchase DATE, " +
         //           "BikeMake VARCHAR(255), " +
         //           "BikeModel VARCHAR(255), " +
         //           "Price DECIMAL(10, 2), " +
        //            "TotalPrice DECIMAL(10, 2), " +
         //           "FOREIGN KEY (CustID) REFERENCES Customer(CustID)" +
         //           ");";
         //   stmt.execute(sql);

         //   sql = "CREATE TABLE IF NOT EXISTS CustomerLogin (" +
         //           "CustID INT PRIMARY KEY, " +
         //           "Email VARCHAR(255), " +
         //           "Password VARCHAR(24), " +
         //           "FOREIGN KEY (CustID) REFERENCES Customer(CustID)" +
         //           ");";
         //   stmt.execute(sql);

         //   System.out.println("Tables created successfully");

            Store store = new Store(conn);
            store.welcome();
            store.signUp();
            store.shop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Store {
    private final List<Bike> inventory = new ArrayList<>();
    private double totalPrice = 0.00;
    private Customer currentCustomer;
    private final List<Customer> customers = new ArrayList<>();
    private final Blockchain blockchain = new Blockchain();
    private final Connection conn;

    public Store(Connection conn) {
        this.conn = conn;
        // Hardcode products into the inventory
        inventory.add(new Bike("Kid's Bike", BikeCategory.MOUNTAIN_BIKE, 149.99));
        inventory.add(new Bike("Kid's Bike", BikeCategory.ROAD_BIKE, 129.99));
        inventory.add(new Bike("Kid's Bike", BikeCategory.BMX_BIKE, 89.99));
        inventory.add(new Bike("Kid's Bike", BikeCategory.CRUISER_BIKE, 109.99));
        // Add bikes to the database

       // for (Bike bike : inventory) {
       //     try {
       //         PreparedStatement stmt = conn.prepareStatement("INSERT INTO BikeInventory (BikeMake, BikeModel, Price) VALUES (?, ?, ?)");
       //         stmt.setString(1, bike.getName());
       //         stmt.setString(2, bike.getCategory().toString());
       //         stmt.setDouble(3, bike.getPrice());
       //         stmt.executeUpdate();
        //    } catch (SQLException e) {
         //       e.printStackTrace();
         //   }
       // }
    }

    public void welcome() {
        System.out.println("Welcome to the Used Bikes for Kids Store!");
        System.out.println("Sign Up Below");
    }

    public void signUp() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        currentCustomer = new Customer(name, email);
        customers.add(currentCustomer);
        System.out.println("Sign-up successful! Welcome, " + name + "!");
    }

    public void displayInventory() {
        System.out.println("Available Bikes:");
        for (int i = 0; i < inventory.size(); i++) {
            System.out.println((i + 1) + ". " + inventory.get(i));
        }
    }

    public void shop() {
        Scanner scanner = new Scanner(System.in);
        boolean continueShopping = true;
        Bike selectedBike = null;
        while (continueShopping) {
            displayInventory();
            System.out.print("Enter a selection to purchase (0 to quit, -1 to return): ");
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= inventory.size()) {
                selectedBike = inventory.get(choice - 1);
                totalPrice += selectedBike.getPrice();
                System.out.println("You've added " + selectedBike.getName() + " to your cart.");
                // Remove the selected bike from the inventory
                inventory.remove(choice - 1);
            } else if (choice == 0) {
                continueShopping = false;
            } else if (choice == -1) {
                returnPurchase();
            } else {
                System.out.println("Invalid input. Please select a valid bike number.");
            }
        }
        System.out.println("Thank you for shopping with us!");
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
        if (change >= 0) {            System.out.println("Change: $" + change);
            // Create a new block for the transaction
            transaction = new Block(0, null, null, null, 0.00);
            blockchain.addBlock(transaction);

        // Remove the purchased bike from the database
        // try {
        //     PreparedStatement stmt = conn.prepareStatement("DELETE FROM BikeInventory WHERE BikeMake = ? AND BikeModel = ? AND Price = ?");
        //     stmt.setString(1, selectedBike.getName());
        //     stmt.setString(2, selectedBike.getCategory().toString());
        //     stmt.setDouble(3, selectedBike.getPrice());


        //     stmt.executeUpdate();
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
        // Print receipt
        System.out.println("Receipt:");
        System.out.println("Customer: " + currentCustomer.getName());
        System.out.println("Email: " + currentCustomer.getEmail());
        System.out.println("Bike Purchased: " + selectedBike.getName());
        System.out.println("Category: " + selectedBike.getCategory());
        System.out.println("Price: $" + selectedBike.getPrice());
        System.out.println("Total Price: $" + totalPrice);
        System.out.println("Payment: $" + payment);
        System.out.println("Change: $" + change);
        System.out.println("Transaction Hash: " + transaction.getHash());
    } else {
            System.out.println("Insufficient payment. Please pay the full amount.");
        }
    }

    public void returnPurchase() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select the bike you would like to return:");
        int returnChoice = scanner.nextInt();
        Bike returnedBike = null;

        if (returnChoice >= 1 && returnChoice <= inventory.size()) {
            returnedBike = inventory.get(returnChoice - 1);
            totalPrice -= returnedBike.getPrice();
            System.out.println("You've returned " + returnedBike.getName() + ". Refund amount: $" + returnedBike.getPrice());
            // Add the returned bike back to the database
            //try {
             //   PreparedStatement stmt = conn.prepareStatement("INSERT INTO BikeInventory (BikeMake, BikeModel, Price) VALUES (?, ?, ?)");
             //   stmt.setString(1, returnedBike.getName());
             //   stmt.setString(2, returnedBike.getCategory().toString());
             //   stmt.setDouble(3, returnedBike.getPrice());
            //    stmt.executeUpdate();
            //} catch (SQLException e) {
           //     e.printStackTrace();
           // }
        } else {
            System.out.println("Invalid. Please select a valid number.");
        }
    }

    class Customer {
        public String name;
        public String email;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Customer(String name, String email) {
            this.name = name;
            this.email = email;


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

class Block {
    private final Store.Customer customer;
    private int index;
    private long timestamp;
    private final Bike bike;
    private final double totalPrice;
    private String previousHash;
    private String hash;

    public Block(int index, String previousHash, Store.Customer currentCustomer, Bike selectedBike, double totalPrice) {
        this.index = index;
        this.timestamp = new Date().getTime();
        this.customer = currentCustomer;
        this.bike = selectedBike;
        this.totalPrice = totalPrice;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }




    public String calculateHash(){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int nonce = 0;
            String input;

            while (true) {
                String customerName = (customer != null) ? customer.getName() : "";
                String bikeName = (bike != null) ? bike.getName() : "";
                double bikePrice = (bike != null) ? bike.getPrice() : 0.0;
                input = index + timestamp + previousHash + customerName + bikeName + bikePrice + totalPrice + nonce;
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

    private class Customer {
    }
}

class Bike {
    private final String name;
    private final BikeCategory category;
    private final double price;

    public Bike(String name, BikeCategory category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }



    public String getName() {
        return name;
    }

    public BikeCategory getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Bike: " +
                "name= '" + name + '\'' +
                ", type= " + category +
                ", price= " + price +
                '}';
    }
}
# KidsUsedBikeStoreMaven
Final Project - Kids Used Bike Store

Welcome to the Kids Used Bike Store!
This Java project is a simple terminal-based application for managing a store that sells used bikes.
Customers can sign up, browse the inventory, shop for bikes, and complete transactions.
The project also integrates with a MySQL database to store customer information, order details, and transaction history.

Features
1. Customer Sign-Up: New customers can sign up by providing their name, email, phone number, and address.
2. Product Inventory: The store maintains an inventory of bikes, including details such as name, category, and price.
3. Shopping Cart: Customers can add bikes to their shopping cart, specify quantities, and complete transactions.
4. Transaction History: Each transaction is recorded in a blockchain, providing a secure and tamper-resistant record.
5. Database Integration: Customer details, product information, and transaction records are stored in a MySQL database.

Getting Started
1. Database Setup: Create a MySQL database named KidsUsedBikeStore and update the connection details in the Main3.java file. Add your own user and password.
2. Run the Application: Execute the Main3.java file to start the application. Follow the on-screen prompts to sign up, browse the inventory, and make purchases.
3. Dependencies: This project requires a MySQL database and JDBC driver for database connectivity. Please add the appropriate version and compile for each of your dependencies.

Code Structure
- Main3.java: Main class containing the entry point of the application.
- Store.java: Class representing the store, customer management, shopping, and database interactions.
- Product.java and Bike.java: Classes defining the product hierarchy.
- DatabaseOperations.java: Interface defining database-related operations.
- Blockchain.java, Block.java: Classes for implementing a basic blockchain for transaction history.

# Contributions
Contributions are welcome! Feel free to submit issues, feature requests, or pull requests.

# License
This project is licensed under the MIT License.

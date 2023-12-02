-- SQL Code for KidsUsedBikeStore Database

-- Use the statement below to discard a database that was already created
-- DROP DATABASE KidsUsedBikeStore;

-- Un-comment the statement below to actually create the KidsUsedBikeStore database
-- CREATE DATABASE KidsUsedBikeStore;
USE KidsUsedBikeStore;

-- Drop the existing tables if they exist
-- DROP TABLE IF EXISTS BikeInventory;
-- DROP TABLE IF EXISTS Orders;
-- DROP TABLE IF EXISTS OrderDetails;

-- Create the BikeInventory table
CREATE TABLE IF NOT EXISTS BikeInventory (
    BikeID INT PRIMARY KEY AUTO_INCREMENT,
    BikeName VARCHAR(255),
    BikeCategory VARCHAR(255),
    Price DECIMAL(10, 2)
);


 -- Create CustomerDetails table
CREATE TABLE IF NOT EXISTS CustomerDetails (
    CustID INT PRIMARY KEY AUTO_INCREMENT,
    FName VARCHAR(255),
    LName VARCHAR(255),
    EMail VARCHAR(255),
    Phone VARCHAR(255),
    Address VARCHAR(255)
);


-- Create Orders table
CREATE TABLE IF NOT EXISTS Orders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    CustID INT,
    DateOfPurchase DATE,
    TotalPrice DOUBLE,
    FOREIGN KEY (CustID) REFERENCES CustomerDetails(CustID)
);


-- Create OrdersOrderDetails table
CREATE TABLE IF NOT EXISTS OrderDetails (
OrderDetailID INT PRIMARY KEY AUTO_INCREMENT,
	OrderID INT,
    CustID INT,
    DateOfPurchase DATE,
    Price DECIMAL(10, 2),
    ProductID INT,
    Quantity INT,
    ProductName VARCHAR(255),
    THash VARCHAR(255),
    FOREIGN KEY (CustID) REFERENCES CustomerDetails(CustID),
	FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),
	FOREIGN KEY (ProductID) REFERENCES BikeInventory(BikeID)


);


-- Optional: Insert sample data into CustomerDetails
-- Note: If you only want to insert one customer's sample data, then end the line with a ; not a , (or you will get an error)
-- INSERT INTO CustomerDetails (FName, LName, EMail, Phone, Address) VALUES
    -- ('John', 'Doe', 'john.doe01@example.com', '1234567890', '123 Main St'),
    -- ('Jane', 'Doe', 'jane.doe02@example.com', '9876543210', '456 Oak St');


-- Optional: Insert sample data into OrderDetails
-- INSERT INTO OrderDetails (ProductID, CustID, DateOfPurchase, TotalPrice) VALUES
    -- (1, 1, '2023-11-22', 149.99),
    -- (2, 2, '2023-11-22', 129.99);


-- Check data in BikeInventory
SELECT * FROM BikeInventory;

-- Check data in CustomerDetails
SELECT * FROM CustomerDetails;

-- Check data in OrderDetails
SELECT * FROM OrderDetails;

-- Check data in Orders
SELECT * FROM Orders




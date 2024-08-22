CREATE DATABASE IF NOT EXISTS club_cards;

USE club_cards;

CREATE TABLE ClubMembers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phoneNumber VARCHAR(20),
    role VARCHAR(50),
    privilege VARCHAR(50),
    balance DECIMAL(10, 2) DEFAULT 0.00
);
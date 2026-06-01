# Tabungin
# Final Project OOP Lab 2026 - Group 24

## Prerequisites
Make sure the following software is installed:
* **Java JDK**
* **Gradle** (or use the included `gradlew` wrapper)
* **XAMPP** (to run a local MySQL server)

## Setup Database (Local MySQL via XAMPP)
This project uses a local MySQL database to store user data, savings records, wishlist items, and transactions.

### File Locations
Database connection settings can be found in:
*database/DatabaseConfig.java*

All database table initialization settings can be found in:
*database/DBInitializer.java*

### Create Database
1. Open your browser and access `http://localhost/phpmyadmin`
2. Click the **SQL** tab at the top
3. Execute the following command:
```sql
CREATE DATABASE tabungin;
```
4. Make sure the `tabungin` database appears in the left panel.

### Configure Database Connection
1. Create a `db.properties` file by copying `db.properties.example` using the following command:
```properties
copy db.properties.example db.properties
```
> Make sure you run this command from the correct directory (*MyTabungan\app\src\main\resources*).

2. Update the values of **DB_USER** and **DB_PASSWORD** according to your local MySQL configuration.
> By default, XAMPP uses the `root` user without a password. Adjust the configuration if your MySQL installation uses a different password.

### Create Tables
All tables will be **created automatically** when the application is run for the first time through `DBInitializer.init()`, which is called from `App.java`.
Therefore, make sure `DBInitializer.init()` (line 15) is not commented out.

#### Notes
* Run the application once to allow all tables to be created automatically.
* If you encounter a `database doesn't exist` error, make sure the `tabungin` database has already been created.
* If you need to modify the database schema, update `DBInitializer.java` and remove the old tables through phpMyAdmin before running the application again.
* After the tables are successfully created, verify them through phpMyAdmin. You should see the following tables:
  * `users`
  * `tabungan_utama`
  * `wishlists`
  * `deposits`

## How to Run the Application
Make sure XAMPP is running (Apache and MySQL services are active) and db.properties has been configured correctly, then run the following command in the terminal:
```bash
./gradlew run
#or
gradlew run
```

## Deskripsi Aplikasi
**Tabungin** is a JavaFX-based personal savings management application designed to help users plan and monitor their financial goals in a structured way.
The application allows users to create monthly savings targets, record deposit transactions, manage multiple wishlists with customizable allocation percentages, and track their progress in real time. In addition, Tabungin provides a Growth feature that displays savings statistics, user levels, achievements, and visualizations of saving activities as a form of gamification to encourage users to maintain consistent saving habits.
Main features available:
- Create and monitor monthly savings with specific targets
- Manage wishlists and allocate funds toward desired items
- Record deposit transactions regularly
- Visualize savings growth and progress
- Login and registration with secure authentication using BCrypt password hashing

## Application Concept
Tabungin applies a goal-based saving concept, a saving method where users save money according to specific financial goals they want to achieve. Each wishlist has its own target amount and allocation percentage from the main savings fund, allowing users to manage multiple financial goals simultaneously while maintaining clear visibility of their overall saving progress.

## Folder Structure
The project is organized into several packages based on their responsibilities:
```
app/src/main/java/mytabungan/
├── App.java                  # JavaFX application entry point
├── dao/                      # Data Access Object: database operations
│   ├── UserDAO.java
│   ├── SavingDAO.java
│   ├── DepositDAO.java
│   └── WishlistDAO.java
├── database/                 # Database configuration and initialization
│   ├── DatabaseConfig.java   # Connecting to MySQL via db.properties
│   └── DBIniatializer.java   # Automatic table generation
├── models/                   # Data model (table representation)
│   ├── User.java
│   ├── Saving.java           # Abstract base class
│   ├── MonthlySaving.java    # Extends Saving
│   ├── Wishlist.java         # Extends Saving
│   └── Deposit.java
├── scenes/                   # User Interface (JavaFX Scene)
│   ├── AuthLayout.java       # The left panel used for Login & Register
│   ├── LoginScene.java
│   ├── RegisterScene.java
│   ├── MainScene.java
│   ├── TabunganScene.java
│   ├── WishlistScene.java
│   ├── GrowthScene.java
│   └── Sidebar.java
└── utils/                    # Public utilities
    ├── ValidationUtil.java   # Input validation (email, password, username)
    ├── SessionManager.java   # Management of currently logged-in users
    └── PasswordUtil.java     # Password hashing and verification (BCrypt)
```

## Code Structure
### Entry Point
`App.java` serves as the main JavaFX application class. When the application starts, it initially displays the `LoginScene`.
### Authentication Flow
1. The user opens the application and is presented with the `LoginScene`
2. If the user does not have an account, they can navigate to the `RegisterScene`
3. After a successful login, `SessionManager.login(user)` stores the current user session.
4. The application navigates to the `MainScene`, which contains the main navigation sidebar.

### DAO Pattern
Each database table has its own DAO class responsible for handling CRUD operations using a connection provided by `DatabaseConfig.connect()`.

### Database Configuration
Database connection settings are loaded from the `db.properties` file instead of being hardcoded, making the application easier to configure across different environments without modifying the source code.

## OOP (Object Oriented Programming) Principles Implementation

### 1. Encapsulation
**Location:** `app/src/main/java/mytabungan/models/`
All fields within model classes are declared as private, preventing direct access from outside the class. Data can only be accessed through the provided getter methods.

**Example in `User.java`:**
```java
private int id;
private String username;
private String email;
private String password;

public int getId()         { return id; }
public String getUsername(){ return username; }
public String getEmail()   { return email; }
```

**Example in `Saving.java`:**
```java
private int id;
private int userId;
protected double targetAmount;
protected double savedAmount;

public int getId()             { return id; }
public double getTargetAmount(){ return targetAmount; }
public double getSavedAmount() { return savedAmount; }
```

In `Saving.java`, the fields `targetAmount` and `savedAmount` are declared as `protected` so that subclasses (`MonthlySaving` and `Wishlist`) can access them directly while still keeping them hidden from external classes.

Encapsulation is also applied in `DatabaseConfig.java`, where database connection details (URL, username, and password) are loaded internally from `db.properties` and are only exposed through the `connect()` method.

### 2. Inheritance
**Location:** `app/src/main/java/mytabungan/models/`
`MonthlySaving` and `Wishlist` are subclasses that extend the abstract class `Saving`. Through inheritance, both subclasses can reuse common attributes and methods defined in `Saving` without duplicating code.

**Inheritance hierarchy:**
```
Saving  (abstract)
├── MonthlySaving
└── Wishlist
```

**Field inherited from `Saving.java`:**
```java
protected double targetAmount;
protected double savedAmount;
// including: id, userId, createdAt
```

**Method inherited from `Saving.java`:**
```java
public int getId()
public int getUserId()
public double getTargetAmount()
public double getSavedAmount()
public LocalDateTime getCreatedAt()
```

**An example of inheritance in `MonthlySaving.java`:**
```java
public class MonthlySaving extends Saving {
    private String periodMonth;

    public MonthlySaving(int id, int userId, double targetAmount,
                         double savedAmount, String periodMonth,
                         LocalDateTime createdAt) {
        super(id, userId, targetAmount, savedAmount, createdAt);
        this.periodMonth = periodMonth;
    }
}
```

**An example of inheritance in `Wishlist.java`:**
```java
public class Wishlist extends Saving {
    private String title;
    private double maxLimit;
    private String status;
    private String period;

    public Wishlist(...) {
        super(id, userId, targetAmount, savedAmount, createdAt);
        ...
    }
}
```
This inheritance structure promotes code reuse and simplifies maintenance by centralizing common saving-related functionality within a single base class.

### 3. Polymorphism
**Location:** `app/src/main/java/mytabungan/models/`
Polymorphism is implemented through the abstract methods `isReached()`, `getRemaining()`, and `getProgressPercentage()` declared in `Saving` and overridden by both `MonthlySaving` and `Wishlist`.
Although the methods share the same signatures, each subclass provides its own implementation according to its business logic and context. This allows objects of different subclasses to be treated as instances of `Saving` while still executing their respective implementations at runtime.

**Declaration at `Saving.java` (abstract):**
```java
public abstract boolean isReached();
abstract double getRemaining();
abstract double getProgressPercentage();
```

**Implementation in `MonthlySaving.java`:**
```java
@Override
public boolean isReached() {
    return getSavedAmount() >= getTargetAmount();
}

@Override
public double getRemaining() {
    return Math.max(0, getTargetAmount() - getSavedAmount());
}

@Override
public double getProgressPercentage() {
    if (targetAmount == 0) return 0;
    return Math.min(100, (savedAmount / targetAmount) * 100);
}
```

**Implementation in `Wishlist.java`:**
```java
@Override
public boolean isReached() {
    return getSavedAmount() >= getTargetAmount();
}

@Override
public double getRemaining() {
    return Math.max(0, getTargetAmount() - getSavedAmount());
}

@Override
public double getProgressPercentage() {
    if (targetAmount == 0) return 0;
    return Math.min(100, (savedAmount / targetAmount) * 100);
}
```

Additionally, `Wishlist` provides a specific method called `calculateAllocation()` that does not exist in `MonthlySaving`, demonstrating that subclasses can introduce behavior unique to their own domain requirements.
```java
// Wishlist.java
public double calculateAllocation(double totalSaving) {
    return totalSaving * maxLimit / 100;
}
```

### 4. Abstraction
**Location:** `app/src/main/java/mytabungan/models/Saving.java`
`Saving` is declared as an abstract class, meaning it cannot be instantiated directly. Instead, it acts as a blueprint that defines common attributes and behaviors shared by all saving-related entities.

```java
public abstract class Saving {
    // General features of all types of savings
    private int id;
    private int userId;
    protected double targetAmount;
    protected double savedAmount;
    protected LocalDateTime createdAt;

    // A general method that is directly inherited
    public int getId() { ... }
    public double getTargetAmount() { ... }
    public double getSavedAmount() { ... }

    // An abstract contract must be implemented by a subclass
    public abstract boolean isReached();
    abstract double getRemaining();
    abstract double getProgressPercentage();
}
```

Abstraction is also applied within the DAO layer. Classes such as `UserDAO`, `SavingDAO`, `WishlistDAO`, and `DepositDAO` encapsulate all SQL-related logic, allowing UI classes (Scenes) to interact with the database through simple method calls without needing to understand the underlying SQL queries or database operations.

**Example in `UserDAO.java`:**
```java
// Called from LoginScene.java — the scene is not aware of the SQL details
public User login(String usernameOrEmail, String password) { ... }
public boolean register(User user) { ... }
public boolean isEmailExists(String email) { ... }
public boolean isUsernameExists(String username) { ... }
```
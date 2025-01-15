# Banking Application

## Overview
This is a plain Java application designed to manage bank accounts with features like creating accounts, calculating savings interest, and performing withdrawals. The application uses MapStruct for mapping between entities and DTOs, ensuring a clean separation between persistence models and data transfer objects.


---

## Prerequisites

### Tools and Libraries
- **JDK**: Version 17 or higher.
- **Maven**: For dependency management and building the project.
- **MapStruct**: For automatic mapping between entities and DTOs.

---

## Setup Instructions

### Clone the Repository
```bash
git https://github.com/azizgouidercap/bnppb.git
cd bankingapp
```

### Build the Project
```bash
mvn clean package
```

### Run the Application

#### If Using a Fat JAR:
```bash
java -jar target/bankingapp-1.0-SNAPSHOT.jar
```

---

## Usage

### Create an Account
1. Run the application.
2. Follow the on-screen prompts to create an account by entering the owner's name and account type.

### Perform a Withdrawal
1. Select the withdrawal option.
2. Provide the account ID and amount.
3. The application validates and processes the transaction based on account rules.

### Calculate Savings Interest
1. Select the interest calculation option.
2. Provide the savings account ID.
3. The application calculates and applies the interest based on the balance and predefined interest rate.

---

## Customization

### Change the Interest Rate
Modify the interest rate in the application configuration :
savings.account.interest-rate

### Change the Withdraw Savings limit 
Modify the interest rate in the application configuration :
savings.account.withdraw-monthly-limit

---

## Technologies Used
- **Java**: Core programming language.
- **MapStruct**: For mapping between entities and DTOs.
- **Maven**: Dependency and build management.

---

## Contact
For questions or support, please contact:
- **Name**: Aziz GOUIDER
- **Email**: aziz.gouider@capgemini.com
- **GitHub**: [My GitHub Profile](https://github.com/azizgouidercap)


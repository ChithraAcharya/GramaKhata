Grama Khata – Digital Credit Ledger App

A simple Android application built using Kotlin and Jetpack Compose to help small village shops maintain customer credit records digitally.

This app allows shopkeepers to:

* Add customers
* Track credit dues
* Record payments
* View pending balances
* Manage customer lists easily


Features

* Add new customers
* Maintain digital khata (credit ledger)
* Track pending dues and advance payments
* Delete customers
* Sort customers by highest balance owed
* Simple and clean UI using Jetpack Compose
* WhatsApp due reminder message preview



Tech Stack

* Language: Kotlin
* UI Toolkit: Jetpack Compose
* Architecture: State Management with Compose
* IDE: Android Studio
* Minimum SDK: Android SDK



Project Structure


MainActivity.kt
 ├── Customer Data Class
 ├── CustomerScreen()
 ├── CustomerItem()
 └── UI Components



How to Run the Project

1. Clone the repository

```bash
git clone https://github.com/your-username/GramaKhata.git
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on:

* Android Emulator
* Physical Android Device



How the App Works

* Shopkeeper adds a customer.
* Credit amount can be added using the ➕ button.
* Payments can be recorded using the ➖ button.
* Positive balance = customer owes money.
* Negative balance = customer paid extra.
* Customers are automatically sorted based on highest due amount.



Sample WhatsApp Reminder

```text
Namaskara Ravi, your due at our shop is ₹500.
```



Code Highlights

### Customer Data Class

```kotlin
data class Customer(
    val id: Int,
    val name: String,
    val balance: Int = 0
)
```

### Customer List State Management

```kotlin
val customerList = remember { mutableStateListOf<Customer>() }
```

### Sorting Customers by Due Amount

```kotlin
customerList.sortedByDescending { it.balance }
```


Future Improvements

* User authentication
* PDF bill generation
* Daily collection reports
* Dark mode support


Author
Chithra R

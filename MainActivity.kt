package com.example.gramakhata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gramakhata.ui.theme.GramaKhataTheme

// ✅ FIX 1: Plain data class — no MutableState inside
data class Customer(
    val id: Int,
    val name: String,
    val balance: Int = 0
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GramaKhataTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CustomerScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerScreen() {
    var nameInput by remember { mutableStateOf("") }
    var idCounter by remember { mutableIntStateOf(0) }

    // ✅ FIX 2: mutableStateListOf with plain Customer objects
    val customerList = remember { mutableStateListOf<Customer>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ── Header ──
        Text(
            text = "🏪 Grama Khata",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "ಗ್ರಾಮ ಖಾತ | Digital Credit Ledger",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Add Customer Card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Add New Customer", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            idCounter++
                            customerList.add(Customer(id = idCounter, name = nameInput.trim()))
                            nameInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("➕ Add Customer")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Due Dashboard Header ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Customer List",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${customerList.size} customers",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Customer List (sorted by highest balance owed) ──
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(
                // ✅ FIX 3: Sort by highest amount owed (Due Dashboard requirement)
                items = customerList.sortedByDescending { it.balance },
                key = { it.id }
            ) { customer ->
                CustomerItem(
                    customer = customer,
                    onCredit = { amount ->
                        // ✅ FIX 4: Replace the object instead of mutating state inside data class
                        val index = customerList.indexOfFirst { it.id == customer.id }
                        if (index != -1) {
                            customerList[index] = customer.copy(balance = customer.balance + amount)
                        }
                    },
                    onPayment = { amount ->
                        val index = customerList.indexOfFirst { it.id == customer.id }
                        if (index != -1) {
                            customerList[index] = customer.copy(balance = customer.balance - amount)
                        }
                    },
                    onDelete = {
                        customerList.removeIf { it.id == customer.id }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerItem(
    customer: Customer,
    onCredit: (Int) -> Unit,
    onPayment: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var amountInput by remember { mutableStateOf("") }

    // Color based on balance
    val balanceColor = when {
        customer.balance > 0 -> Color(0xFFD32F2F)  // Red  = owes money
        customer.balance < 0 -> Color(0xFF388E3C)  // Green = overpaid
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ── Customer Name + Balance Row ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = customer.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = when {
                            customer.balance > 0 -> "⚠ Owes ₹${customer.balance}"
                            customer.balance < 0 -> "✅ Advance ₹${-customer.balance}"
                            else -> "✔ Settled"
                        },
                        fontSize = 14.sp,
                        color = balanceColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Delete button
                TextButton(onClick = onDelete) {
                    Text("🗑", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Amount Input + Buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { c -> c.isDigit() } },
                    label = { Text("Amount ₹") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // ➕ Credit (gave goods)
                Button(
                    onClick = {
                        val amt = amountInput.toIntOrNull()
                        if (amt != null && amt > 0) {
                            onCredit(amt)
                            amountInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                // ➖ Payment received
                Button(
                    onClick = {
                        val amt = amountInput.toIntOrNull()
                        if (amt != null && amt > 0) {
                            onPayment(amt)
                            amountInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF388E3C)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp)
                ) {
                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            // ── Daily Collection Report hint ──
            if (customer.balance != 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "📱 WhatsApp: \"Namaskara ${customer.name}, your due at our shop is ₹${customer.balance}.\"",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val MONTHS = listOf(
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
)

val TX_TYPES = listOf(
  "MONTHLY_DEPOSIT" to "Monthly Deposit",
  "SPECIAL_DEPOSIT" to "Special Deposit",
  "SHARE_CAPITAL" to "Share Capital",
  "WELFARE_FUND" to "Welfare Fund",
  "WITHDRAWAL" to "Withdrawal / Return"
)

val PAYMENT_METHODS = listOf("Cash", "bKash", "Nagad", "Bank Transfer", "Other")

@Composable
fun AddEditTransactionDialog(
  transaction: TransactionRecord?,
  preselectedMemberId: String?,
  members: List<Member>,
  onDismiss: () -> Unit,
  onSave: (TransactionRecord, Boolean) -> Unit
) {
  val isNew = (transaction == null)

  val currentYear = Calendar.getInstance().get(Calendar.YEAR)
  val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  val sdfMonth = SimpleDateFormat("MMMM", Locale.getDefault())
  val todayDate = sdfDate.format(Date())
  val defaultMonth = sdfMonth.format(Date())

  var memberId by remember {
    mutableStateOf(
      transaction?.memberId ?: preselectedMemberId ?: members.firstOrNull()?.memberId ?: ""
    )
  }
  var amountStr by remember {
    mutableStateOf(if (transaction != null) transaction.amount.toString() else "")
  }
  var selectedType by remember {
    mutableStateOf(transaction?.type ?: "MONTHLY_DEPOSIT")
  }
  var selectedMonth by remember {
    mutableStateOf(transaction?.month ?: defaultMonth)
  }
  var yearStr by remember {
    mutableStateOf((transaction?.year ?: currentYear).toString())
  }
  var dateStr by remember {
    mutableStateOf(transaction?.date ?: todayDate)
  }
  var receiptNumber by remember {
    mutableStateOf(
      transaction?.receiptNumber ?: "VR-${currentYear}-${(100..999).random()}"
    )
  }
  var paymentMethod by remember {
    mutableStateOf(transaction?.paymentMethod ?: "Cash")
  }
  var remarks by remember {
    mutableStateOf(transaction?.remarks ?: "")
  }

  var memberDropdownExpanded by remember { mutableStateOf(false) }
  var typeDropdownExpanded by remember { mutableStateOf(false) }
  var monthDropdownExpanded by remember { mutableStateOf(false) }
  var paymentDropdownExpanded by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isNew) "Add Deposit Transaction" else "Edit Transaction",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        if (errorMessage != null) {
          Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Text(
              text = errorMessage!!,
              color = MaterialTheme.colorScheme.onErrorContainer,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(8.dp)
            )
          }
        }

        // Member Selection
        Text(
          text = "ASSIGN TO MEMBER *",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        val currentMember = members.find { it.memberId.equals(memberId, ignoreCase = true) }
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = if (currentMember != null) "${currentMember.memberId} - ${currentMember.name}" else memberId,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Member") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = {
              Text(
                "▼",
                modifier = Modifier
                  .clickable { memberDropdownExpanded = true }
                  .padding(8.dp)
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { memberDropdownExpanded = true }
              .testTag("tx_select_member")
          )
          DropdownMenu(
            expanded = memberDropdownExpanded,
            onDismissRequest = { memberDropdownExpanded = false }
          ) {
            members.forEach { m ->
              DropdownMenuItem(
                text = { Text("${m.memberId} - ${m.name}") },
                onClick = {
                  memberId = m.memberId
                  memberDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Amount
        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("Amount (৳ BDT) *") },
          leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_amount_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Type Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = TX_TYPES.find { it.first == selectedType }?.second ?: selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Transaction Type") },
            leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null) },
            trailingIcon = {
              Text(
                "▼",
                modifier = Modifier
                  .clickable { typeDropdownExpanded = true }
                  .padding(8.dp)
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { typeDropdownExpanded = true }
              .testTag("tx_type_select")
          )
          DropdownMenu(
            expanded = typeDropdownExpanded,
            onDismissRequest = { typeDropdownExpanded = false }
          ) {
            TX_TYPES.forEach { (key, label) ->
              DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                  selectedType = key
                  typeDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month and Year
        Row(modifier = Modifier.fillMaxWidth()) {
          Box(modifier = Modifier.weight(1.3f)) {
            OutlinedTextField(
              value = selectedMonth,
              onValueChange = {},
              readOnly = true,
              label = { Text("Month") },
              leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
              modifier = Modifier
                .fillMaxWidth()
                .clickable { monthDropdownExpanded = true }
                .testTag("tx_month_select")
            )
            DropdownMenu(
              expanded = monthDropdownExpanded,
              onDismissRequest = { monthDropdownExpanded = false }
            ) {
              MONTHS.forEach { m ->
                DropdownMenuItem(
                  text = { Text(m) },
                  onClick = {
                    selectedMonth = m
                    monthDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          OutlinedTextField(
            value = yearStr,
            onValueChange = { yearStr = it },
            label = { Text("Year") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("tx_year_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Date & Voucher
        Row(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = dateStr,
            onValueChange = { dateStr = it },
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier
              .weight(1.1f)
              .testTag("tx_date_input")
          )

          Spacer(modifier = Modifier.width(8.dp))

          OutlinedTextField(
            value = receiptNumber,
            onValueChange = { receiptNumber = it },
            label = { Text("Receipt / Voucher #") },
            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .weight(1.2f)
              .testTag("tx_receipt_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Payment Method Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = paymentMethod,
            onValueChange = {},
            readOnly = true,
            label = { Text("Payment Method") },
            trailingIcon = {
              Text(
                "▼",
                modifier = Modifier
                  .clickable { paymentDropdownExpanded = true }
                  .padding(8.dp)
              )
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { paymentDropdownExpanded = true }
          )
          DropdownMenu(
            expanded = paymentDropdownExpanded,
            onDismissRequest = { paymentDropdownExpanded = false }
          ) {
            PAYMENT_METHODS.forEach { pm ->
              DropdownMenuItem(
                text = { Text(pm) },
                onClick = {
                  paymentMethod = pm
                  paymentDropdownExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Remarks
        OutlinedTextField(
          value = remarks,
          onValueChange = { remarks = it },
          label = { Text("Remarks / Note (Optional)") },
          leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_remarks_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (memberId.isBlank()) {
            errorMessage = "Please assign this transaction to a member"
            return@Button
          }
          val amount = amountStr.toDoubleOrNull()
          if (amount == null || amount <= 0) {
            errorMessage = "Please enter a valid deposit amount"
            return@Button
          }
          val year = yearStr.toIntOrNull() ?: currentYear

          val record = TransactionRecord(
            id = transaction?.id ?: 0L,
            memberId = memberId,
            amount = amount,
            type = selectedType,
            month = selectedMonth,
            year = year,
            date = dateStr.trim(),
            receiptNumber = receiptNumber.trim(),
            paymentMethod = paymentMethod,
            remarks = remarks.trim(),
            createdAt = transaction?.createdAt ?: System.currentTimeMillis()
          )
          onSave(record, isNew)
        },
        modifier = Modifier.testTag("save_tx_confirm_btn")
      ) {
        Text(if (isNew) "Add Deposit" else "Update Transaction")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

package com.example.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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

@Composable
fun AddEditMemberDialog(
  member: Member?,
  suggestedMemberId: String,
  onDismiss: () -> Unit,
  onSave: (Member, Boolean) -> Unit
) {
  val isNew = (member == null)

  var name by remember { mutableStateOf(member?.name ?: "") }
  var memberId by remember { mutableStateOf(member?.memberId ?: suggestedMemberId) }
  var mobileNumber by remember { mutableStateOf(member?.mobileNumber ?: "") }
  var address by remember { mutableStateOf(member?.address ?: "") }
  var nidNumber by remember { mutableStateOf(member?.nidNumber ?: "") }
  var nomineeName by remember { mutableStateOf(member?.nomineeName ?: "") }
  var nomineeAddress by remember { mutableStateOf(member?.nomineeAddress ?: "") }
  var nomineeNidNumber by remember { mutableStateOf(member?.nomineeNidNumber ?: "") }
  var loginPin by remember { mutableStateOf(member?.loginPin ?: "1234") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (isNew) "Register New Member" else "Edit Member Profile",
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

        Text(
          text = "MEMBER DETAILS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name *") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = memberId,
            onValueChange = { memberId = it },
            label = { Text("Member ID *") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("member_input_id")
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = { Text("Mobile Number *") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier
              .weight(1.2f)
              .testTag("member_input_mobile")
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Member Address (e.g. Kalir Bazar)") },
          leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_address")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = nidNumber,
          onValueChange = { nidNumber = it },
          label = { Text("NID Number") },
          leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_nid")
        )

        Spacer(modifier = Modifier.height(14.dp))
        Divider()
        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "NOMINEE INFORMATION",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
          value = nomineeName,
          onValueChange = { nomineeName = it },
          label = { Text("Nominee Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_nominee_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = nomineeAddress,
          onValueChange = { nomineeAddress = it },
          label = { Text("Nominee Address") },
          leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_nominee_address")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = nomineeNidNumber,
          onValueChange = { nomineeNidNumber = it },
          label = { Text("Nominee NID Number") },
          leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_nominee_nid")
        )

        Spacer(modifier = Modifier.height(14.dp))
        Divider()
        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "LOGIN SECURITY",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
          value = loginPin,
          onValueChange = { loginPin = it },
          label = { Text("Member 4-Digit Login PIN") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("member_input_pin")
        )

        Text(
          text = "Member can change their PIN anytime after login.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank() || memberId.isBlank() || mobileNumber.isBlank()) {
            errorMessage = "Name, Member ID, and Mobile are mandatory."
            return@Button
          }
          if (loginPin.length < 4) {
            errorMessage = "Login PIN must be at least 4 digits."
            return@Button
          }

          val updated = Member(
            id = member?.id ?: 0L,
            memberId = memberId.trim().uppercase(),
            name = name.trim(),
            mobileNumber = mobileNumber.trim(),
            address = address.trim(),
            nidNumber = nidNumber.trim(),
            nomineeName = nomineeName.trim(),
            nomineeAddress = nomineeAddress.trim(),
            nomineeNidNumber = nomineeNidNumber.trim(),
            loginPin = loginPin.trim(),
            joinDate = member?.joinDate ?: "2005",
            status = member?.status ?: "ACTIVE",
            createdAt = member?.createdAt ?: System.currentTimeMillis()
          )
          onSave(updated, isNew)
        },
        modifier = Modifier.testTag("save_member_btn")
      ) {
        Text(if (isNew) "Add Member" else "Save Changes")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

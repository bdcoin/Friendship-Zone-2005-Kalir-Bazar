package com.example.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ChangePinDialog(
  title: String = "Change Login PIN",
  onDismiss: () -> Unit,
  onConfirm: (String, String, String) -> Unit
) {
  var oldPin by remember { mutableStateOf("") }
  var newPin by remember { mutableStateOf("") }
  var confirmPin by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        if (errorText != null) {
          Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
          ) {
            Text(
              text = errorText!!,
              color = MaterialTheme.colorScheme.onErrorContainer,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(8.dp)
            )
          }
        }

        OutlinedTextField(
          value = oldPin,
          onValueChange = { oldPin = it },
          label = { Text("Current PIN") },
          leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("current_pin_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = newPin,
          onValueChange = { newPin = it },
          label = { Text("New PIN (min 4 digits)") },
          leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("new_pin_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = confirmPin,
          onValueChange = { confirmPin = it },
          label = { Text("Confirm New PIN") },
          leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("confirm_pin_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (oldPin.isBlank() || newPin.isBlank() || confirmPin.isBlank()) {
            errorText = "Please fill in all PIN fields"
            return@Button
          }
          if (newPin.length < 4) {
            errorText = "New PIN must be at least 4 digits"
            return@Button
          }
          if (newPin != confirmPin) {
            errorText = "New PIN and Confirm PIN do not match"
            return@Button
          }
          onConfirm(oldPin, newPin, confirmPin)
        },
        modifier = Modifier.testTag("confirm_change_pin_btn")
      ) {
        Text("Update PIN")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.entity.Member
import com.example.ui.components.InfoRow
import com.example.ui.components.formatCurrency
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@Composable
fun MemberProfileDetailDialog(
  member: Member,
  totalDeposit: Double,
  isAdmin: Boolean,
  onDismiss: () -> Unit,
  onEditClick: (() -> Unit)? = null
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(EmeraldDark),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = member.name.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = GoldLight,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = member.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = "ID: ${member.memberId}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Total Deposit Highlight
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Total Deposit:",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = formatCurrency(totalDeposit),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "MEMBER INFORMATION",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        InfoRow(label = "Mobile", value = member.mobileNumber, icon = Icons.Default.Phone)
        InfoRow(label = "Address", value = member.address, icon = Icons.Default.Home)
        InfoRow(label = "NID Number", value = member.nidNumber, icon = Icons.Default.CreditCard)
        InfoRow(label = "Status", value = member.status, icon = Icons.Default.Badge)
        if (isAdmin) {
          InfoRow(label = "Login PIN", value = member.loginPin)
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider()
        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "NOMINEE DETAILS",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        InfoRow(label = "Nominee Name", value = member.nomineeName, icon = Icons.Default.Person)
        InfoRow(label = "Nominee Address", value = member.nomineeAddress, icon = Icons.Default.Home)
        InfoRow(label = "Nominee NID", value = member.nomineeNidNumber, icon = Icons.Default.CreditCard)
      }
    },
    confirmButton = {
      if (isAdmin && onEditClick != null) {
        Button(
          onClick = {
            onDismiss()
            onEditClick()
          },
          modifier = Modifier.testTag("edit_profile_dialog_btn")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Edit Profile")
        }
      } else {
        Button(onClick = onDismiss) {
          Text("Close")
        }
      }
    },
    dismissButton = {
      if (isAdmin && onEditClick != null) {
        OutlinedButton(onClick = onDismiss) {
          Text("Close")
        }
      }
    }
  )
}

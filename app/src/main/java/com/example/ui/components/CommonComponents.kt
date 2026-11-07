package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.entity.TransactionRecord
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
  val formatter = NumberFormat.getNumberInstance(Locale.US)
  formatter.minimumFractionDigits = 0
  formatter.maximumFractionDigits = 2
  return "৳ " + formatter.format(amount)
}

@Composable
fun SocietyBrandHeader(
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(if (compact) 64.dp else 88.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(EmeraldDark, EmeraldPrimary)
          )
        )
        .padding(2.dp),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_launcher_fg_1794039628510),
        contentDescription = "Friendship Zone Emblem",
        modifier = Modifier
          .size(if (compact) 60.dp else 84.dp)
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "Friendship Zone (2005)",
      style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(2.dp))

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = "KALIR BAZAR",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
      }

      Spacer(modifier = Modifier.width(6.dp))

      Text(
        text = "Savings & Welfare Society",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun TotalDepositHeroCard(
  totalDeposit: Double,
  memberId: String?,
  memberName: String?,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("hero_total_deposit_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = EmeraldDark),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              EmeraldDark,
              Color(0xFF144D3D),
              Color(0xFF0F3B2F)
            )
          )
        )
        .padding(20.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TOTAL DEPOSIT MONEY",
              style = MaterialTheme.typography.labelMedium,
              color = GoldLight,
              letterSpacing = 1.sp,
              fontWeight = FontWeight.Bold
            )
            if (memberName != null) {
              Text(
                text = memberName,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          if (memberId != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = GoldPrimary.copy(alpha = 0.25f)
            ) {
              Text(
                text = memberId,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = GoldLight,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = formatCurrency(totalDeposit),
          style = MaterialTheme.typography.displayLarge.copy(fontSize = 36.sp),
          fontWeight = FontWeight.ExtraBold,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = EmeraldLight,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Verified Member Deposit Records",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f)
          )
        }
      }
    }
  }
}

@Composable
fun InfoRow(
  label: String,
  value: String,
  icon: ImageVector? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
    }
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.width(110.dp)
    )
    Text(
      text = if (value.isNotBlank()) value else "-",
      style = MaterialTheme.typography.bodyMedium,
      fontWeight = FontWeight.SemiBold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
fun TransactionCard(
  transaction: TransactionRecord,
  modifier: Modifier = Modifier,
  onEditClick: (() -> Unit)? = null,
  onDeleteClick: (() -> Unit)? = null
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${transaction.month} ${transaction.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Date: ${transaction.date}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          val isDeposit = transaction.isDeposit
          val amountPrefix = if (isDeposit) "+ " else "- "
          val amountColor = if (isDeposit) GreenDeposit else RedExpense

          Text(
            text = amountPrefix + formatCurrency(transaction.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
          )

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isDeposit) EmeraldSubtle else Color(0xFFFFEBEE)
          ) {
            Text(
              text = transaction.type.replace("_", " "),
              style = MaterialTheme.typography.labelSmall,
              color = if (isDeposit) EmeraldMedium else RedExpense,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      if (transaction.receiptNumber.isNotBlank() || transaction.remarks.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          if (transaction.receiptNumber.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Voucher: ${transaction.receiptNumber}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          if (transaction.paymentMethod.isNotBlank()) {
            Text(
              text = "Via: ${transaction.paymentMethod}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      if (transaction.remarks.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Note: ${transaction.remarks}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Admin actions if provided
      if (onEditClick != null || onDeleteClick != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          if (onEditClick != null) {
            androidx.compose.material3.TextButton(
              onClick = onEditClick,
              modifier = Modifier.testTag("edit_tx_btn_${transaction.id}")
            ) {
              Text("Edit", style = MaterialTheme.typography.labelMedium)
            }
          }
          if (onDeleteClick != null) {
            Spacer(modifier = Modifier.width(6.dp))
            androidx.compose.material3.TextButton(
              onClick = onDeleteClick,
              modifier = Modifier.testTag("delete_tx_btn_${transaction.id}")
            ) {
              Text(
                "Delete",
                style = MaterialTheme.typography.labelMedium,
                color = RedExpense
              )
            }
          }
        }
      }
    }
  }
}

package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "transactions",
  indices = [
    Index(value = ["memberId"]),
    Index(value = ["year", "month"]),
    Index(value = ["date"])
  ]
)
data class TransactionRecord(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val memberId: String,
  val amount: Double,
  val type: String, // MONTHLY_DEPOSIT, SPECIAL_DEPOSIT, SHARE_CAPITAL, WELFARE_FUND, WITHDRAWAL
  val month: String, // January, February, etc.
  val year: Int,
  val date: String, // YYYY-MM-DD
  val receiptNumber: String = "",
  val paymentMethod: String = "Cash", // Cash, bKash, Nagad, Bank
  val remarks: String = "",
  val createdAt: Long = System.currentTimeMillis()
) {
  val isDeposit: Boolean
    get() = type != "WITHDRAWAL"
}

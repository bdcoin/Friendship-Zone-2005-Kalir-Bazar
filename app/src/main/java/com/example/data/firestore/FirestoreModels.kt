package com.example.data.firestore

import com.example.data.entity.AdminConfig
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreMember(
  @DocumentId val id: String = "",
  val memberId: String = "",
  val name: String = "",
  val mobileNumber: String = "",
  val address: String = "",
  val nidNumber: String = "",
  val nomineeName: String = "",
  val nomineeAddress: String = "",
  val nomineeNidNumber: String = "",
  val loginPin: String = "1234",
  val status: String = "ACTIVE",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toMember(): Member {
    return Member(
      id = memberId.hashCode().toLong(),
      memberId = memberId,
      name = name,
      mobileNumber = mobileNumber,
      address = address,
      nidNumber = nidNumber,
      nomineeName = nomineeName,
      nomineeAddress = nomineeAddress,
      nomineeNidNumber = nomineeNidNumber,
      loginPin = loginPin,
      status = status,
      createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis()
    )
  }
}

@IgnoreExtraProperties
data class FirestoreTransaction(
  @DocumentId val id: String = "",
  val memberId: String = "",
  val amount: Double = 0.0,
  val type: String = "MONTHLY_DEPOSIT",
  val month: String = "",
  val year: Int = 2026,
  val date: String = "",
  val receiptNumber: String = "",
  val paymentMethod: String = "Cash",
  val remarks: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  val isDeposit: Boolean
    get() = type != "WITHDRAWAL"

  fun toTransactionRecord(): TransactionRecord {
    return TransactionRecord(
      id = (id.ifBlank { memberId + date + amount }).hashCode().toLong(),
      memberId = memberId,
      amount = amount,
      type = type,
      month = month,
      year = year,
      date = date,
      receiptNumber = receiptNumber,
      paymentMethod = paymentMethod,
      remarks = remarks,
      createdAt = createdAt?.toDate()?.time ?: System.currentTimeMillis()
    )
  }
}

@IgnoreExtraProperties
data class FirestoreSocietyConfig(
  @DocumentId val id: String = "main",
  val adminPin: String = "2005",
  val societyName: String = "Friendship Zone (2005)",
  val location: String = "Kalir Bazar",
  val establishedYear: String = "2005",
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toAdminConfig(): AdminConfig {
    return AdminConfig(
      id = 1,
      adminPin = adminPin,
      societyName = societyName,
      location = location,
      establishedYear = establishedYear
    )
  }
}

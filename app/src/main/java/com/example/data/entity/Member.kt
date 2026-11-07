package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "members",
  indices = [
    Index(value = ["memberId"], unique = true),
    Index(value = ["mobileNumber"])
  ]
)
data class Member(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val memberId: String,
  val name: String,
  val mobileNumber: String,
  val address: String,
  val nidNumber: String,
  val nomineeName: String,
  val nomineeAddress: String,
  val nomineeNidNumber: String,
  val loginPin: String = "1234",
  val joinDate: String = "",
  val status: String = "ACTIVE",
  val createdAt: Long = System.currentTimeMillis()
)

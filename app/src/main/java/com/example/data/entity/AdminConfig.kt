package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_config")
data class AdminConfig(
  @PrimaryKey
  val id: Int = 1,
  val adminPin: String = "2005",
  val societyName: String = "Friendship Zone (2005)",
  val location: String = "Kalir Bazar",
  val establishedYear: String = "2005"
)

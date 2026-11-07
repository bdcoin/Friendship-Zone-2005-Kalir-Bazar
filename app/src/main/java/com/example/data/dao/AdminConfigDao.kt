package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.AdminConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminConfigDao {
  @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
  fun getConfig(): Flow<AdminConfig?>

  @Query("SELECT * FROM admin_config WHERE id = 1 LIMIT 1")
  suspend fun getConfigSync(): AdminConfig?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateConfig(config: AdminConfig)

  @Query("UPDATE admin_config SET adminPin = :newPin WHERE id = 1")
  suspend fun updateAdminPin(newPin: String)
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AdminConfigDao
import com.example.data.dao.MemberDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.AdminConfig
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Member::class,
    TransactionRecord::class,
    AdminConfig::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun memberDao(): MemberDao
  abstract fun transactionDao(): TransactionDao
  abstract fun adminConfigDao(): AdminConfigDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "friendship_zone_database.db"
        )
          .addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
              super.onCreate(db)
              // Seed default admin configuration with PIN "2005"
              CoroutineScope(Dispatchers.IO).launch {
                getInstance(context).adminConfigDao().insertOrUpdateConfig(
                  AdminConfig(
                    id = 1,
                    adminPin = "2005",
                    societyName = "Friendship Zone (2005)",
                    location = "Kalir Bazar",
                    establishedYear = "2005"
                  )
                )
              }
            }
          })
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}

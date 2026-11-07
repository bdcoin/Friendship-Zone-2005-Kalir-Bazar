package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.TransactionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
  @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
  fun getAllTransactions(): Flow<List<TransactionRecord>>

  @Query("SELECT * FROM transactions WHERE memberId = :memberId ORDER BY date DESC, id DESC")
  fun getTransactionsForMember(memberId: String): Flow<List<TransactionRecord>>

  @Query("SELECT * FROM transactions WHERE memberId = :memberId AND year = :year ORDER BY date DESC, id DESC")
  fun getTransactionsForMemberAndYear(memberId: String, year: Int): Flow<List<TransactionRecord>>

  @Query("SELECT * FROM transactions WHERE month = :month AND year = :year ORDER BY date DESC, id DESC")
  fun getTransactionsForMonthAndYear(month: String, year: Int): Flow<List<TransactionRecord>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getTransactionById(id: Long): TransactionRecord?

  @Query("""
    SELECT COALESCE(
      SUM(CASE WHEN type != 'WITHDRAWAL' THEN amount ELSE -amount END), 
      0.0
    ) 
    FROM transactions 
    WHERE memberId = :memberId
  """)
  fun getTotalDepositForMember(memberId: String): Flow<Double>

  @Query("""
    SELECT COALESCE(
      SUM(CASE WHEN type != 'WITHDRAWAL' THEN amount ELSE -amount END), 
      0.0
    ) 
    FROM transactions
  """)
  fun getTotalSocietyFund(): Flow<Double>

  @Query("""
    SELECT COALESCE(SUM(amount), 0.0) 
    FROM transactions 
    WHERE month = :month AND year = :year AND type != 'WITHDRAWAL'
  """)
  fun getMonthlyTotalDeposit(month: String, year: Int): Flow<Double>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(tx: TransactionRecord): Long

  @Update
  suspend fun updateTransaction(tx: TransactionRecord)

  @Delete
  suspend fun deleteTransaction(tx: TransactionRecord)

  @Query("DELETE FROM transactions WHERE memberId = :memberId")
  suspend fun deleteTransactionsForMember(memberId: String)
}

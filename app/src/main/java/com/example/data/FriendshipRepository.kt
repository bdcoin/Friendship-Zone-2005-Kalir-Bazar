package com.example.data

import com.example.data.dao.AdminConfigDao
import com.example.data.dao.MemberDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.AdminConfig
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import kotlinx.coroutines.flow.Flow

class FriendshipRepository(
  private val memberDao: MemberDao,
  private val transactionDao: TransactionDao,
  private val adminConfigDao: AdminConfigDao
) {
  // Members
  val allMembers: Flow<List<Member>> = memberDao.getAllMembers()
  val memberCount: Flow<Int> = memberDao.getMemberCount()

  fun getMember(memberId: String): Flow<Member?> = memberDao.getMemberByMemberId(memberId)

  suspend fun authenticateMember(identifier: String, pin: String): Member? {
    return memberDao.authenticateMember(identifier.trim(), pin.trim())
  }

  suspend fun insertMember(member: Member): Result<Long> {
    return runCatching {
      memberDao.insertMember(member)
    }
  }

  suspend fun updateMember(member: Member): Result<Unit> {
    return runCatching {
      memberDao.updateMember(member)
    }
  }

  suspend fun updateMemberPin(memberId: String, newPin: String): Result<Unit> {
    return runCatching {
      memberDao.updateLoginPin(memberId, newPin.trim())
    }
  }

  suspend fun deleteMember(member: Member): Result<Unit> {
    return runCatching {
      transactionDao.deleteTransactionsForMember(member.memberId)
      memberDao.deleteMember(member)
    }
  }

  suspend fun generateNextMemberId(): String {
    val lastId = memberDao.getLastMemberId()
    if (lastId == null) return "FZ-101"
    val numericPart = lastId.filter { it.isDigit() }
    val nextNum = (numericPart.toIntOrNull() ?: 100) + 1
    return "FZ-$nextNum"
  }

  // Transactions
  val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()
  val totalSocietyFund: Flow<Double> = transactionDao.getTotalSocietyFund()

  fun getTransactionsForMember(memberId: String): Flow<List<TransactionRecord>> {
    return transactionDao.getTransactionsForMember(memberId)
  }

  fun getTransactionsForMemberAndYear(memberId: String, year: Int): Flow<List<TransactionRecord>> {
    return transactionDao.getTransactionsForMemberAndYear(memberId, year)
  }

  fun getTotalDepositForMember(memberId: String): Flow<Double> {
    return transactionDao.getTotalDepositForMember(memberId)
  }

  suspend fun insertTransaction(tx: TransactionRecord): Result<Long> {
    return runCatching {
      transactionDao.insertTransaction(tx)
    }
  }

  suspend fun updateTransaction(tx: TransactionRecord): Result<Unit> {
    return runCatching {
      transactionDao.updateTransaction(tx)
    }
  }

  suspend fun deleteTransaction(tx: TransactionRecord): Result<Unit> {
    return runCatching {
      transactionDao.deleteTransaction(tx)
    }
  }

  // Admin Config
  val adminConfig: Flow<AdminConfig?> = adminConfigDao.getConfig()

  suspend fun verifyAdminPin(pin: String): Boolean {
    val config = adminConfigDao.getConfigSync()
    val savedPin = config?.adminPin ?: "2005"
    return savedPin.trim() == pin.trim()
  }

  suspend fun updateAdminPin(newPin: String): Result<Unit> {
    return runCatching {
      val current = adminConfigDao.getConfigSync()
      if (current == null) {
        adminConfigDao.insertOrUpdateConfig(
          AdminConfig(id = 1, adminPin = newPin.trim())
        )
      } else {
        adminConfigDao.updateAdminPin(newPin.trim())
      }
    }
  }

  suspend fun ensureAdminConfigInitialized() {
    val current = adminConfigDao.getConfigSync()
    if (current == null) {
      adminConfigDao.insertOrUpdateConfig(
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
}

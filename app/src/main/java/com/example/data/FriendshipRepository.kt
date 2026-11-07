package com.example.data

import android.content.Context
import com.example.R
import com.example.data.dao.AdminConfigDao
import com.example.data.dao.MemberDao
import com.example.data.dao.TransactionDao
import com.example.data.entity.AdminConfig
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import com.example.data.firestore.FirestoreMember
import com.example.data.firestore.FirestoreSocietyConfig
import com.example.data.firestore.FirestoreTransaction
import com.example.data.firestore.OperationType
import com.example.data.firestore.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendshipRepository(
  private val context: Context,
  private val memberDao: MemberDao,
  private val transactionDao: TransactionDao,
  private val adminConfigDao: AdminConfigDao
) {
  // CRITICAL: Always initialize Firestore using the provisioned firestore_database_id
  private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(
    context.getString(R.string.firestore_database_id)
  )

  private val scope = CoroutineScope(Dispatchers.IO)

  init {
    startCloudSync()
  }

  // Real-time synchronization from Cloud Firestore to local Room cache
  private fun startCloudSync() {
    scope.launch {
      try {
        // 1. Observe Members collection
        firestore.collection("members")
          .snapshots()
          .map { snapshot -> snapshot.toObjects(FirestoreMember::class.java).map { it.toMember() } }
          .catch { e ->
            if (e !is CancellationException) {
              handleFirestoreError(e, OperationType.LIST, "members")
            }
          }
          .collect { cloudMembers ->
            if (cloudMembers.isNotEmpty()) {
              cloudMembers.forEach { m ->
                runCatching { memberDao.insertMember(m) }.onFailure {
                  memberDao.updateMember(m)
                }
              }
            }
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.LIST, "members")
      }
    }

    scope.launch {
      try {
        // 2. Observe Transactions collection
        firestore.collection("transactions")
          .snapshots()
          .map { snapshot -> snapshot.toObjects(FirestoreTransaction::class.java).map { it.toTransactionRecord() } }
          .catch { e ->
            if (e !is CancellationException) {
              handleFirestoreError(e, OperationType.LIST, "transactions")
            }
          }
          .collect { cloudTransactions ->
            if (cloudTransactions.isNotEmpty()) {
              cloudTransactions.forEach { tx ->
                transactionDao.insertTransaction(tx)
              }
            }
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.LIST, "transactions")
      }
    }

    scope.launch {
      try {
        // 3. Observe Society Config
        firestore.collection("society_config").document("main")
          .snapshots()
          .map { it.toObject(FirestoreSocietyConfig::class.java)?.toAdminConfig() }
          .catch { e ->
            if (e !is CancellationException) {
              handleFirestoreError(e, OperationType.GET, "society_config/main")
            }
          }
          .collect { config ->
            if (config != null) {
              adminConfigDao.insertOrUpdateConfig(config)
            }
          }
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.GET, "society_config/main")
      }
    }
  }

  // Members Flow
  val allMembers: Flow<List<Member>> = memberDao.getAllMembers().flowOn(Dispatchers.IO)
  val memberCount: Flow<Int> = memberDao.getMemberCount().flowOn(Dispatchers.IO)

  fun getMember(memberId: String): Flow<Member?> = memberDao.getMemberByMemberId(memberId).flowOn(Dispatchers.IO)

  suspend fun authenticateMember(identifier: String, pin: String): Member? {
    // Check locally first
    val localMember = memberDao.authenticateMember(identifier.trim(), pin.trim())
    if (localMember != null) return localMember

    // If not in local cache yet (new device login), query Firestore directly
    return try {
      val querySnap = firestore.collection("members")
        .whereEqualTo("loginPin", pin.trim())
        .get()
        .await()

      val member = querySnap.toObjects(FirestoreMember::class.java)
        .firstOrNull {
          it.memberId.equals(identifier.trim(), ignoreCase = true) ||
            it.mobileNumber.equals(identifier.trim(), ignoreCase = true)
        }?.toMember()

      if (member != null) {
        runCatching { memberDao.insertMember(member) }.onFailure { memberDao.updateMember(member) }
      }
      member
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, "members")
      null
    }
  }

  suspend fun insertMember(member: Member): Result<Long> {
    return runCatching {
      // 1. Sync to Cloud Firestore
      val payload = hashMapOf<String, Any>(
        "memberId" to member.memberId.trim().uppercase(),
        "name" to member.name.trim(),
        "mobileNumber" to member.mobileNumber.trim(),
        "address" to member.address.trim(),
        "nidNumber" to member.nidNumber.trim(),
        "nomineeName" to member.nomineeName.trim(),
        "nomineeAddress" to member.nomineeAddress.trim(),
        "nomineeNidNumber" to member.nomineeNidNumber.trim(),
        "loginPin" to member.loginPin.trim(),
        "status" to member.status,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val docPath = "members/${member.memberId.trim().uppercase()}"
      try {
        firestore.collection("members")
          .document(member.memberId.trim().uppercase())
          .set(payload)
          .await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, docPath)
        throw e
      }

      // 2. Cache locally
      memberDao.insertMember(member)
    }
  }

  suspend fun updateMember(member: Member): Result<Unit> {
    return runCatching {
      val payload = hashMapOf<String, Any>(
        "name" to member.name.trim(),
        "mobileNumber" to member.mobileNumber.trim(),
        "address" to member.address.trim(),
        "nidNumber" to member.nidNumber.trim(),
        "nomineeName" to member.nomineeName.trim(),
        "nomineeAddress" to member.nomineeAddress.trim(),
        "nomineeNidNumber" to member.nomineeNidNumber.trim(),
        "loginPin" to member.loginPin.trim(),
        "status" to member.status,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val docPath = "members/${member.memberId.trim().uppercase()}"
      try {
        firestore.collection("members")
          .document(member.memberId.trim().uppercase())
          .update(payload)
          .await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, docPath)
        throw e
      }

      memberDao.updateMember(member)
    }
  }

  suspend fun updateMemberPin(memberId: String, newPin: String): Result<Unit> {
    return runCatching {
      val docPath = "members/${memberId.trim().uppercase()}"
      try {
        firestore.collection("members")
          .document(memberId.trim().uppercase())
          .update(
            mapOf(
              "loginPin" to newPin.trim(),
              "updatedAt" to FieldValue.serverTimestamp()
            )
          )
          .await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, docPath)
        throw e
      }

      memberDao.updateLoginPin(memberId.trim().uppercase(), newPin.trim())
    }
  }

  suspend fun deleteMember(member: Member): Result<Unit> {
    return runCatching {
      val docPath = "members/${member.memberId.trim().uppercase()}"
      try {
        firestore.collection("members")
          .document(member.memberId.trim().uppercase())
          .delete()
          .await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.DELETE, docPath)
        throw e
      }

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

  // Transactions Flow
  val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions().flowOn(Dispatchers.IO)
  val totalSocietyFund: Flow<Double> = transactionDao.getTotalSocietyFund().flowOn(Dispatchers.IO)

  fun getTransactionsForMember(memberId: String): Flow<List<TransactionRecord>> {
    return transactionDao.getTransactionsForMember(memberId).flowOn(Dispatchers.IO)
  }

  fun getTransactionsForMemberAndYear(memberId: String, year: Int): Flow<List<TransactionRecord>> {
    return transactionDao.getTransactionsForMemberAndYear(memberId, year).flowOn(Dispatchers.IO)
  }

  fun getTotalDepositForMember(memberId: String): Flow<Double> {
    return transactionDao.getTotalDepositForMember(memberId).flowOn(Dispatchers.IO)
  }

  suspend fun insertTransaction(tx: TransactionRecord): Result<Long> {
    return runCatching {
      val docRef = firestore.collection("transactions").document()
      val payload = hashMapOf<String, Any>(
        "memberId" to tx.memberId.trim().uppercase(),
        "amount" to tx.amount,
        "type" to tx.type,
        "month" to tx.month,
        "year" to tx.year,
        "date" to tx.date,
        "receiptNumber" to tx.receiptNumber,
        "paymentMethod" to tx.paymentMethod,
        "remarks" to tx.remarks,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      try {
        docRef.set(payload).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, docRef.path)
        throw e
      }

      transactionDao.insertTransaction(tx.copy(id = docRef.id.hashCode().toLong()))
    }
  }

  suspend fun updateTransaction(tx: TransactionRecord): Result<Unit> {
    return runCatching {
      val querySnap = firestore.collection("transactions")
        .whereEqualTo("memberId", tx.memberId)
        .whereEqualTo("receiptNumber", tx.receiptNumber)
        .get()
        .await()

      val docRef = querySnap.documents.firstOrNull()?.reference
        ?: firestore.collection("transactions").document()

      val payload = hashMapOf<String, Any>(
        "amount" to tx.amount,
        "type" to tx.type,
        "month" to tx.month,
        "year" to tx.year,
        "date" to tx.date,
        "receiptNumber" to tx.receiptNumber,
        "paymentMethod" to tx.paymentMethod,
        "remarks" to tx.remarks,
        "updatedAt" to FieldValue.serverTimestamp()
      )
      try {
        docRef.set(payload, com.google.firebase.firestore.SetOptions.merge()).await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, docRef.path)
        throw e
      }

      transactionDao.updateTransaction(tx)
    }
  }

  suspend fun deleteTransaction(tx: TransactionRecord): Result<Unit> {
    return runCatching {
      val querySnap = firestore.collection("transactions")
        .whereEqualTo("memberId", tx.memberId)
        .whereEqualTo("receiptNumber", tx.receiptNumber)
        .get()
        .await()

      querySnap.documents.firstOrNull()?.reference?.delete()?.await()
      transactionDao.deleteTransaction(tx)
    }
  }

  // Admin Config
  val adminConfig: Flow<AdminConfig?> = adminConfigDao.getConfig().flowOn(Dispatchers.IO)

  suspend fun verifyAdminPin(pin: String): Boolean {
    // Check locally first
    val config = adminConfigDao.getConfigSync()
    val localPin = config?.adminPin ?: "2005"
    if (localPin.trim() == pin.trim()) return true

    // Check Cloud Firestore for any updated Admin PIN across devices
    return try {
      val snap = firestore.collection("society_config").document("main").get().await()
      val cloudPin = snap.getString("adminPin") ?: "2005"
      if (cloudPin.trim() == pin.trim()) {
        adminConfigDao.updateAdminPin(cloudPin)
        true
      } else {
        false
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.GET, "society_config/main")
      localPin.trim() == pin.trim()
    }
  }

  suspend fun updateAdminPin(newPin: String): Result<Unit> {
    return runCatching {
      val payload = mapOf(
        "adminPin" to newPin.trim(),
        "societyName" to "Friendship Zone (2005)",
        "location" to "Kalir Bazar",
        "establishedYear" to "2005",
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val docPath = "society_config/main"
      try {
        firestore.collection("society_config")
          .document("main")
          .set(payload, com.google.firebase.firestore.SetOptions.merge())
          .await()
      } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, docPath)
        throw e
      }

      adminConfigDao.updateAdminPin(newPin.trim())
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

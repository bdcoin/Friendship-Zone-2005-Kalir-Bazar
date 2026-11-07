package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FriendshipRepository
import com.example.data.entity.AdminConfig
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class UserRole {
  GUEST,
  MEMBER,
  ADMIN
}

data class FriendshipUiState(
  val role: UserRole = UserRole.GUEST,
  val currentMemberId: String? = null,
  val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
  val memberSearchQuery: String = "",
  val transactionSearchQuery: String = "",
  val selectedFilterMonth: String = "All Months",
  val infoMessage: String? = null,
  val errorMessage: String? = null
)

class FriendshipViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: FriendshipRepository

  init {
    val db = AppDatabase.getInstance(application)
    repository = FriendshipRepository(
      memberDao = db.memberDao(),
      transactionDao = db.transactionDao(),
      adminConfigDao = db.adminConfigDao()
    )
    viewModelScope.launch {
      repository.ensureAdminConfigInitialized()
    }
  }

  private val _uiState = MutableStateFlow(FriendshipUiState())
  val uiState: StateFlow<FriendshipUiState> = _uiState.asStateFlow()

  val allMembers: StateFlow<List<Member>> = repository.allMembers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allTransactions: StateFlow<List<TransactionRecord>> = repository.allTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val totalSocietyFund: StateFlow<Double> = repository.totalSocietyFund
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val memberCount: StateFlow<Int> = repository.memberCount
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val adminConfig: StateFlow<AdminConfig?> = repository.adminConfig
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Current logged in member flow
  val currentMember: StateFlow<Member?> = combine(_uiState, allMembers) { state, members ->
    state.currentMemberId?.let { id ->
      members.find { it.memberId.equals(id, ignoreCase = true) }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Current logged in member's transactions
  val currentMemberTransactions: StateFlow<List<TransactionRecord>> = combine(
    _uiState,
    allTransactions
  ) { state, transactions ->
    val id = state.currentMemberId
    if (id == null) emptyList()
    else transactions.filter { it.memberId.equals(id, ignoreCase = true) }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Member total deposit
  val currentMemberTotalDeposit: StateFlow<Double> = currentMemberTransactions.combine(_uiState) { list, _ ->
    list.sumOf { if (it.isDeposit) it.amount else -it.amount }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  fun loginAsMember(identifier: String, pin: String, onResult: (Boolean, String) -> Unit) {
    if (identifier.isBlank() || pin.isBlank()) {
      onResult(false, "Please enter both Member ID / Mobile and PIN")
      return
    }
    viewModelScope.launch {
      val member = repository.authenticateMember(identifier.trim(), pin.trim())
      if (member != null) {
        _uiState.update {
          it.copy(
            role = UserRole.MEMBER,
            currentMemberId = member.memberId,
            infoMessage = "Welcome back, ${member.name}!"
          )
        }
        onResult(true, "Login successful")
      } else {
        onResult(false, "Invalid Member ID/Mobile or PIN. Please check with society admin.")
      }
    }
  }

  fun loginAsAdmin(pin: String, onResult: (Boolean, String) -> Unit) {
    if (pin.isBlank()) {
      onResult(false, "Please enter Admin PIN")
      return
    }
    viewModelScope.launch {
      val valid = repository.verifyAdminPin(pin.trim())
      if (valid) {
        _uiState.update {
          it.copy(
            role = UserRole.ADMIN,
            currentMemberId = null,
            infoMessage = "Admin access granted"
          )
        }
        onResult(true, "Admin verified")
      } else {
        onResult(false, "Incorrect Admin PIN. Default PIN is 2005.")
      }
    }
  }

  fun logout() {
    _uiState.update {
      FriendshipUiState(
        role = UserRole.GUEST,
        currentMemberId = null
      )
    }
  }

  fun setSelectedYear(year: Int) {
    _uiState.update { it.copy(selectedYear = year) }
  }

  fun setMemberSearchQuery(query: String) {
    _uiState.update { it.copy(memberSearchQuery = query) }
  }

  fun setTransactionSearchQuery(query: String) {
    _uiState.update { it.copy(transactionSearchQuery = query) }
  }

  fun setSelectedFilterMonth(month: String) {
    _uiState.update { it.copy(selectedFilterMonth = month) }
  }

  fun clearMessages() {
    _uiState.update { it.copy(infoMessage = null, errorMessage = null) }
  }

  fun changeMemberPin(
    currentMemberId: String,
    oldPin: String,
    newPin: String,
    confirmPin: String,
    onComplete: (Boolean, String) -> Unit
  ) {
    if (newPin.length < 4) {
      onComplete(false, "New PIN must be at least 4 digits")
      return
    }
    if (newPin != confirmPin) {
      onComplete(false, "New PIN and Confirm PIN do not match")
      return
    }
    viewModelScope.launch {
      val member = repository.authenticateMember(currentMemberId, oldPin)
      if (member == null) {
        onComplete(false, "Current PIN is incorrect")
        return@launch
      }
      val res = repository.updateMemberPin(currentMemberId, newPin)
      if (res.isSuccess) {
        _uiState.update { it.copy(infoMessage = "PIN changed successfully!") }
        onComplete(true, "PIN updated successfully")
      } else {
        onComplete(false, "Failed to update PIN: ${res.exceptionOrNull()?.message}")
      }
    }
  }

  fun changeAdminPin(
    oldPin: String,
    newPin: String,
    confirmPin: String,
    onComplete: (Boolean, String) -> Unit
  ) {
    if (newPin.length < 4) {
      onComplete(false, "New PIN must be at least 4 digits")
      return
    }
    if (newPin != confirmPin) {
      onComplete(false, "PINs do not match")
      return
    }
    viewModelScope.launch {
      val valid = repository.verifyAdminPin(oldPin)
      if (!valid) {
        onComplete(false, "Current Admin PIN is incorrect")
        return@launch
      }
      val res = repository.updateAdminPin(newPin)
      if (res.isSuccess) {
        _uiState.update { it.copy(infoMessage = "Admin PIN updated successfully!") }
        onComplete(true, "Admin PIN changed successfully")
      } else {
        onComplete(false, "Failed to update PIN")
      }
    }
  }

  fun saveMember(
    member: Member,
    isNew: Boolean,
    onComplete: (Boolean, String) -> Unit
  ) {
    if (member.name.isBlank()) {
      onComplete(false, "Member Name cannot be empty")
      return
    }
    if (member.memberId.isBlank()) {
      onComplete(false, "Member ID cannot be empty")
      return
    }
    if (member.mobileNumber.isBlank()) {
      onComplete(false, "Mobile Number cannot be empty")
      return
    }

    viewModelScope.launch {
      val result = if (isNew) {
        repository.insertMember(member)
      } else {
        repository.updateMember(member)
      }

      if (result.isSuccess) {
        val msg = if (isNew) "Member ${member.name} created successfully" else "Member ${member.name} updated"
        _uiState.update { it.copy(infoMessage = msg) }
        onComplete(true, msg)
      } else {
        val err = result.exceptionOrNull()?.message ?: "Failed to save member"
        val userErr = if (err.contains("UNIQUE", ignoreCase = true)) "Member ID already exists!" else err
        onComplete(false, userErr)
      }
    }
  }

  fun deleteMember(member: Member, onComplete: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      val res = repository.deleteMember(member)
      if (res.isSuccess) {
        _uiState.update { it.copy(infoMessage = "Member ${member.name} and their records were deleted") }
        onComplete(true, "Member deleted")
      } else {
        onComplete(false, "Failed to delete: ${res.exceptionOrNull()?.message}")
      }
    }
  }

  fun saveTransaction(
    tx: TransactionRecord,
    isNew: Boolean,
    onComplete: (Boolean, String) -> Unit
  ) {
    if (tx.memberId.isBlank()) {
      onComplete(false, "Please select a member")
      return
    }
    if (tx.amount <= 0) {
      onComplete(false, "Amount must be greater than 0")
      return
    }
    if (tx.month.isBlank()) {
      onComplete(false, "Please specify the transaction month")
      return
    }

    viewModelScope.launch {
      val res = if (isNew) {
        repository.insertTransaction(tx)
      } else {
        repository.updateTransaction(tx)
      }

      if (res.isSuccess) {
        val msg = if (isNew) "Transaction ৳${tx.amount} saved" else "Transaction updated"
        _uiState.update { it.copy(infoMessage = msg) }
        onComplete(true, msg)
      } else {
        onComplete(false, res.exceptionOrNull()?.message ?: "Failed to save transaction")
      }
    }
  }

  fun deleteTransaction(tx: TransactionRecord, onComplete: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      val res = repository.deleteTransaction(tx)
      if (res.isSuccess) {
        _uiState.update { it.copy(infoMessage = "Transaction deleted") }
        onComplete(true, "Transaction deleted")
      } else {
        onComplete(false, "Failed to delete: ${res.exceptionOrNull()?.message}")
      }
    }
  }

  suspend fun suggestNextMemberId(): String {
    return repository.generateNextMemberId()
  }

  fun getTodayFormatted(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
  }

  fun getCurrentMonth(): String {
    val sdf = SimpleDateFormat("MMMM", Locale.getDefault())
    return sdf.format(Date())
  }

  fun getCurrentYear(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
  }
}

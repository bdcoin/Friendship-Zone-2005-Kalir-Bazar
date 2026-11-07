package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Member
import com.example.data.entity.TransactionRecord
import com.example.ui.FriendshipViewModel
import com.example.ui.components.TransactionCard
import com.example.ui.components.formatCurrency
import com.example.ui.dialogs.AddEditMemberDialog
import com.example.ui.dialogs.AddEditTransactionDialog
import com.example.ui.dialogs.ChangePinDialog
import com.example.ui.dialogs.MemberProfileDetailDialog
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.RedExpense
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: FriendshipViewModel,
  modifier: Modifier = Modifier
) {
  val allMembers by viewModel.allMembers.collectAsStateWithLifecycle()
  val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
  val totalFund by viewModel.totalSocietyFund.collectAsStateWithLifecycle()
  val memberCount by viewModel.memberCount.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Members, 1 = Transactions, 2 = Admin Settings

  // Dialog States
  var showAddMemberDialog by remember { mutableStateOf(false) }
  var memberToEdit by remember { mutableStateOf<Member?>(null) }
  var memberToViewDetail by remember { mutableStateOf<Member?>(null) }
  var memberToDelete by remember { mutableStateOf<Member?>(null) }

  var showAddTxDialog by remember { mutableStateOf(false) }
  var preselectedMemberIdForTx by remember { mutableStateOf<String?>(null) }
  var txToEdit by remember { mutableStateOf<TransactionRecord?>(null) }
  var txToDelete by remember { mutableStateOf<TransactionRecord?>(null) }

  var showChangeAdminPinDialog by remember { mutableStateOf(false) }

  // Search queries
  var memberSearch by remember { mutableStateOf("") }
  var txSearch by remember { mutableStateOf("") }

  // BackHandler to confirm logout or reset tab
  BackHandler {
    if (selectedTab != 0) {
      selectedTab = 0
    } else {
      viewModel.logout()
    }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "Friendship Zone (2005)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Text(
              text = "Kalir Bazar • Admin Management Portal",
              style = MaterialTheme.typography.labelSmall,
              color = GoldLight
            )
          }
        },
        actions = {
          IconButton(
            onClick = { showChangeAdminPinDialog = true },
            modifier = Modifier.testTag("admin_pin_change_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = "Admin Security",
              tint = Color.White
            )
          }
          IconButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.testTag("admin_logout_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Logout",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = EmeraldDark
        )
      )
    },
    floatingActionButton = {
      when (selectedTab) {
        0 -> {
          FloatingActionButton(
            onClick = { showAddMemberDialog = true },
            containerColor = EmeraldPrimary,
            contentColor = Color.White,
            modifier = Modifier.testTag("fab_add_member")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.PersonAdd, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Member", fontWeight = FontWeight.Bold)
            }
          }
        }
        1 -> {
          FloatingActionButton(
            onClick = {
              preselectedMemberIdForTx = null
              showAddTxDialog = true
            },
            containerColor = EmeraldPrimary,
            contentColor = Color.White,
            modifier = Modifier.testTag("fab_add_tx")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.AddCard, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Add Deposit", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      // Admin Overview Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldDark),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "TOTAL SOCIETY FUNDS",
              style = MaterialTheme.typography.labelSmall,
              color = GoldLight,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = formatCurrency(totalFund),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }

          Row {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.15f),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = memberCount.toString(),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "Members",
                  style = MaterialTheme.typography.labelSmall,
                  color = GoldLight
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.15f)
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = allTransactions.size.toString(),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "Records",
                  style = MaterialTheme.typography.labelSmall,
                  color = GoldLight
                )
              }
            }
          }
        }
      }

      // Admin Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("admin_tabs")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Members (${allMembers.size})", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_admin_members")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Transactions", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_admin_transactions")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Settings", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_admin_settings")
        )
      }

      when (selectedTab) {
        0 -> {
          // TAB 0: MEMBERS MANAGEMENT
          val filteredMembers = allMembers.filter { m ->
            m.name.contains(memberSearch, ignoreCase = true) ||
              m.memberId.contains(memberSearch, ignoreCase = true) ||
              m.mobileNumber.contains(memberSearch, ignoreCase = true)
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp)
          ) {
            item {
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = memberSearch,
                onValueChange = { memberSearch = it },
                label = { Text("Search Members by Name, ID, or Mobile") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("admin_member_search")
              )
              Spacer(modifier = Modifier.height(10.dp))
            }

            if (filteredMembers.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      imageVector = Icons.Default.Group,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                      text = if (allMembers.isEmpty()) "No members enrolled yet." else "No members match '$memberSearch'",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Tap 'Add Member' to register member profiles with Name, Mobile, NID, and Nominee info.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (allMembers.isEmpty()) {
                      Spacer(modifier = Modifier.height(14.dp))
                      Button(
                        onClick = { showAddMemberDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                      ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Register First Member")
                      }
                    }
                  }
                }
              }
            } else {
              items(filteredMembers, key = { it.id }) { member ->
                val memberTotalDeposit = allTransactions
                  .filter { it.memberId.equals(member.memberId, ignoreCase = true) }
                  .sumOf { if (it.isDeposit) it.amount else -it.amount }

                Card(
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { memberToViewDetail = member }
                    .testTag("admin_member_card_${member.memberId}")
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                          modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EmeraldDark),
                          contentAlignment = Alignment.Center
                        ) {
                          Text(
                            text = member.name.take(1).uppercase(),
                            color = GoldLight,
                            fontWeight = FontWeight.Bold
                          )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                          Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                          )
                          Text(
                            text = "Mob: ${member.mobileNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                        }
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                      ) {
                        Text(
                          text = member.memberId,
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onPrimaryContainer,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "Total Deposit:",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                          text = formatCurrency(memberTotalDeposit),
                          style = MaterialTheme.typography.titleMedium,
                          fontWeight = FontWeight.ExtraBold,
                          color = EmeraldPrimary
                        )
                      }

                      if (member.nomineeName.isNotBlank()) {
                        Column(horizontalAlignment = Alignment.End) {
                          Text(
                            text = "Nominee:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                          )
                          Text(
                            text = member.nomineeName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(6.dp))

                    // Action buttons
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      TextButton(
                        onClick = { memberToViewDetail = member }
                      ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View & Nominee", style = MaterialTheme.typography.labelSmall)
                      }

                      Row {
                        TextButton(
                          onClick = {
                            preselectedMemberIdForTx = member.memberId
                            showAddTxDialog = true
                          }
                        ) {
                          Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("+ Deposit", style = MaterialTheme.typography.labelSmall)
                        }

                        TextButton(
                          onClick = { memberToEdit = member }
                        ) {
                          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                          Spacer(modifier = Modifier.width(4.dp))
                          Text("Edit", style = MaterialTheme.typography.labelSmall)
                        }

                        IconButton(
                          onClick = { memberToDelete = member }
                        ) {
                          Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RedExpense,
                            modifier = Modifier.size(18.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
          }
        }

        1 -> {
          // TAB 1: ALL TRANSACTIONS
          val filteredTx = allTransactions.filter { tx ->
            val matchQuery = txSearch.isBlank() ||
              tx.memberId.contains(txSearch, ignoreCase = true) ||
              tx.month.contains(txSearch, ignoreCase = true) ||
              tx.receiptNumber.contains(txSearch, ignoreCase = true)
            matchQuery
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp)
          ) {
            item {
              Spacer(modifier = Modifier.height(10.dp))
              OutlinedTextField(
                value = txSearch,
                onValueChange = { txSearch = it },
                label = { Text("Filter Transactions by Member ID, Month, Voucher") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("admin_tx_search")
              )
              Spacer(modifier = Modifier.height(10.dp))
            }

            if (filteredTx.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      imageVector = Icons.Default.ReceiptLong,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                      text = if (allTransactions.isEmpty()) "No transactions logged yet." else "No transactions match '$txSearch'",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Tap 'Add Deposit' to record deposit receipts, monthly subscriptions, and share funds.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            } else {
              items(filteredTx, key = { it.id }) { tx ->
                val associatedMember = allMembers.find { it.memberId.equals(tx.memberId, ignoreCase = true) }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                  Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "Member: ${tx.memberId} • ${associatedMember?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                      )
                    }
                  }
                  TransactionCard(
                    transaction = tx,
                    onEditClick = { txToEdit = tx },
                    onDeleteClick = { txToDelete = tx }
                  )
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(80.dp)) // padding for FAB
            }
          }
        }

        2 -> {
          // TAB 2: ADMIN SETTINGS
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp)
          ) {
            item {
              Spacer(modifier = Modifier.height(14.dp))

              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Text(
                    text = "Society Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "Organization: Friendship Zone (2005)",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Text(
                    text = "Location: Kalir Bazar",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Text(
                    text = "Foundation Year: 2005",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Text(
                    text = "Total Active Members: ${allMembers.size}",
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Text(
                    text = "Total Society Funds: ${formatCurrency(totalFund)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LockReset,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "Change Admin Security PIN",
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Update the master security PIN used to log into the Admin portal. Current default is 2005.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(12.dp))
                  Button(
                    onClick = { showChangeAdminPinDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text("Change Admin PIN")
                  }
                }
              }

              Spacer(modifier = Modifier.height(30.dp))
            }
          }
        }
      }
    }

    // DIALOGS

    // 1. Add / Edit Member
    if (showAddMemberDialog || memberToEdit != null) {
      val isEditing = (memberToEdit != null)
      var suggestedId by remember { mutableStateOf("FZ-101") }

      remember {
        scope.launch {
          suggestedId = viewModel.suggestNextMemberId()
        }
      }

      AddEditMemberDialog(
        member = memberToEdit,
        suggestedMemberId = suggestedId,
        onDismiss = {
          showAddMemberDialog = false
          memberToEdit = null
        },
        onSave = { updatedMember, isNew ->
          viewModel.saveMember(updatedMember, isNew) { success, msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            if (success) {
              showAddMemberDialog = false
              memberToEdit = null
            }
          }
        }
      )
    }

    // 2. Member Profile Details View Dialog
    if (memberToViewDetail != null) {
      val member = memberToViewDetail!!
      val memberTotalDeposit = allTransactions
        .filter { it.memberId.equals(member.memberId, ignoreCase = true) }
        .sumOf { if (it.isDeposit) it.amount else -it.amount }

      MemberProfileDetailDialog(
        member = member,
        totalDeposit = memberTotalDeposit,
        isAdmin = true,
        onDismiss = { memberToViewDetail = null },
        onEditClick = {
          val toEdit = memberToViewDetail
          memberToViewDetail = null
          memberToEdit = toEdit
        }
      )
    }

    // 3. Delete Member Confirmation Dialog
    if (memberToDelete != null) {
      val m = memberToDelete!!
      AlertDialog(
        onDismissRequest = { memberToDelete = null },
        title = { Text("Delete Member?", fontWeight = FontWeight.Bold) },
        text = {
          Text("Are you sure you want to delete ${m.name} (${m.memberId})? All transaction records for this member will also be deleted.")
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteMember(m) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
                memberToDelete = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
          ) {
            Text("Delete Member")
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { memberToDelete = null }) {
            Text("Cancel")
          }
        }
      )
    }

    // 4. Add / Edit Transaction Dialog
    if (showAddTxDialog || txToEdit != null) {
      if (allMembers.isEmpty()) {
        AlertDialog(
          onDismissRequest = {
            showAddTxDialog = false
            txToEdit = null
          },
          title = { Text("No Members Enrolled") },
          text = { Text("Please register at least one member before recording transactions.") },
          confirmButton = {
            Button(
              onClick = {
                showAddTxDialog = false
                txToEdit = null
                showAddMemberDialog = true
              }
            ) {
              Text("Add Member Now")
            }
          },
          dismissButton = {
            OutlinedButton(
              onClick = {
                showAddTxDialog = false
                txToEdit = null
              }
            ) {
              Text("Cancel")
            }
          }
        )
      } else {
        AddEditTransactionDialog(
          transaction = txToEdit,
          preselectedMemberId = preselectedMemberIdForTx,
          members = allMembers,
          onDismiss = {
            showAddTxDialog = false
            txToEdit = null
            preselectedMemberIdForTx = null
          },
          onSave = { updatedTx, isNew ->
            viewModel.saveTransaction(updatedTx, isNew) { success, msg ->
              scope.launch { snackbarHostState.showSnackbar(msg) }
              if (success) {
                showAddTxDialog = false
                txToEdit = null
                preselectedMemberIdForTx = null
              }
            }
          }
        )
      }
    }

    // 5. Delete Transaction Confirmation Dialog
    if (txToDelete != null) {
      val tx = txToDelete!!
      AlertDialog(
        onDismissRequest = { txToDelete = null },
        title = { Text("Delete Transaction?", fontWeight = FontWeight.Bold) },
        text = {
          Text("Are you sure you want to delete this ${tx.type} of ৳${tx.amount} (${tx.month} ${tx.year})?")
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.deleteTransaction(tx) { success, msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
                txToDelete = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
          ) {
            Text("Delete")
          }
        },
        dismissButton = {
          OutlinedButton(onClick = { txToDelete = null }) {
            Text("Cancel")
          }
        }
      )
    }

    // 6. Change Admin PIN Dialog
    if (showChangeAdminPinDialog) {
      ChangePinDialog(
        title = "Change Admin Security PIN",
        onDismiss = { showChangeAdminPinDialog = false },
        onConfirm = { oldPin, newPin, confirmPin ->
          viewModel.changeAdminPin(oldPin, newPin, confirmPin) { success, msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            if (success) {
              showChangeAdminPinDialog = false
            }
          }
        }
      )
    }
  }
}

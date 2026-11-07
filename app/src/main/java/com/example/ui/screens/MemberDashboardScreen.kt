package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.Member
import com.example.ui.FriendshipViewModel
import com.example.ui.components.InfoRow
import com.example.ui.components.TotalDepositHeroCard
import com.example.ui.components.TransactionCard
import com.example.ui.components.formatCurrency
import com.example.ui.dialogs.ChangePinDialog
import com.example.ui.dialogs.MONTHS
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDashboardScreen(
  viewModel: FriendshipViewModel,
  modifier: Modifier = Modifier
) {
  val currentMember by viewModel.currentMember.collectAsStateWithLifecycle()
  val totalDeposit by viewModel.currentMemberTotalDeposit.collectAsStateWithLifecycle()
  val transactions by viewModel.currentMemberTransactions.collectAsStateWithLifecycle()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var selectedTab by remember { mutableIntStateOf(0) } // 0 = Transactions & Monthly, 1 = My Profile & Nominee, 2 = Security
  var showChangePinDialog by remember { mutableStateOf(false) }

  // Filter state
  var selectedYear by remember { mutableIntStateOf(viewModel.getCurrentYear()) }
  var selectedMonth by remember { mutableStateOf("All") }

  // Handle system back button to confirm logout or reset tab
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
              text = "Kalir Bazar • Member Portal",
              style = MaterialTheme.typography.labelSmall,
              color = GoldLight
            )
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.testTag("member_logout_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Log out",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = EmeraldDark
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    val member = currentMember

    if (member == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Member profile not found or session expired.")
          Spacer(modifier = Modifier.height(12.dp))
          Button(onClick = { viewModel.logout() }) {
            Text("Back to Login")
          }
        }
      }
      return@Scaffold
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      // Hero Card: Total Deposit Money
      TotalDepositHeroCard(
        totalDeposit = totalDeposit,
        memberId = member.memberId,
        memberName = member.name,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
      )

      // Dashboard Tabs
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("member_dashboard_tabs")
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("History", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_member_history")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("My Profile", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_member_profile")
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Security", fontWeight = FontWeight.Bold)
            }
          },
          modifier = Modifier.testTag("tab_member_security")
        )
      }

      when (selectedTab) {
        0 -> {
          // TAB 0: TRANSACTIONS & MONTHLY HISTORY
          val availableYears = (2005..viewModel.getCurrentYear()).toList().reversed()
          val filteredTransactions = transactions.filter { tx ->
            val matchYear = (tx.year == selectedYear)
            val matchMonth = (selectedMonth == "All" || tx.month.equals(selectedMonth, ignoreCase = true))
            matchYear && matchMonth
          }

          val filteredTotal = filteredTransactions.sumOf { if (it.isDeposit) it.amount else -it.amount }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp)
          ) {
            item {
              Spacer(modifier = Modifier.height(10.dp))

              // Year Selector
              Text(
                text = "Select Year:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp)
              ) {
                items(availableYears.take(7)) { year ->
                  FilterChip(
                    selected = selectedYear == year,
                    onClick = { selectedYear = year },
                    label = { Text(year.toString()) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = EmeraldPrimary,
                      selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("year_chip_$year")
                  )
                }
              }

              // Month Selector
              Text(
                text = "Monthly Filter:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp)
              ) {
                item {
                  FilterChip(
                    selected = selectedMonth == "All",
                    onClick = { selectedMonth = "All" },
                    label = { Text("All Months") }
                  )
                }
                items(MONTHS) { month ->
                  FilterChip(
                    selected = selectedMonth == month,
                    onClick = { selectedMonth = month },
                    label = { Text(month.take(3)) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = EmeraldPrimary,
                      selectedLabelColor = Color.White
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Filter summary
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Showing: ${if (selectedMonth == "All") "Entire Year" else selectedMonth} $selectedYear",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Subtotal: ${formatCurrency(filteredTotal)}",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = EmeraldPrimary
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
            }

            if (filteredTransactions.isEmpty()) {
              item {
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                ) {
                  Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    Icon(
                      imageVector = Icons.Default.FilterList,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                      text = "No transactions found for $selectedMonth $selectedYear",
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Deposits logged by the admin will be reflected here automatically.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.outline
                    )
                  }
                }
              }
            } else {
              items(filteredTransactions, key = { it.id }) { tx ->
                TransactionCard(
                  transaction = tx,
                  modifier = Modifier.padding(vertical = 5.dp)
                )
              }
            }

            item {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Notice: Only the society admin can create or edit transactions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
              Spacer(modifier = Modifier.height(30.dp))
            }
          }
        }

        1 -> {
          // TAB 1: MY PROFILE & NOMINEE
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp)
          ) {
            item {
              Spacer(modifier = Modifier.height(14.dp))

              // Member Profile Card
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(EmeraldDark),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(
                        text = member.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = GoldLight,
                        fontWeight = FontWeight.Bold
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                      )
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                      ) {
                        Text(
                          text = "MEMBER ID: ${member.memberId}",
                          style = MaterialTheme.typography.labelSmall,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onPrimaryContainer,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(14.dp))
                  Divider()
                  Spacer(modifier = Modifier.height(12.dp))

                  Text(
                    text = "PERSONAL INFORMATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(6.dp))

                  InfoRow(label = "Mobile", value = member.mobileNumber, icon = Icons.Default.Phone)
                  InfoRow(label = "Address", value = member.address, icon = Icons.Default.Home)
                  InfoRow(label = "NID Number", value = member.nidNumber, icon = Icons.Default.CreditCard)
                  InfoRow(label = "Status", value = member.status, icon = Icons.Default.Badge)
                  InfoRow(label = "Society", value = "Friendship Zone (2005), Kalir Bazar")
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Nominee Information Card
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(18.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "NOMINEE INFORMATION",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Spacer(modifier = Modifier.height(10.dp))
                  Divider()
                  Spacer(modifier = Modifier.height(10.dp))

                  InfoRow(label = "Nominee Name", value = member.nomineeName, icon = Icons.Default.Person)
                  InfoRow(label = "Address", value = member.nomineeAddress, icon = Icons.Default.Home)
                  InfoRow(label = "NID Number", value = member.nomineeNidNumber, icon = Icons.Default.CreditCard)
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Member profiles can only be edited by the society admin. To update your info or nominee, contact management.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(30.dp))
            }
          }
        }

        2 -> {
          // TAB 2: SECURITY & CHANGE PIN
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
                Column(modifier = Modifier.padding(20.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LockReset,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                      text = "Change Login PIN",
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.Bold
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "Keep your account secure by changing your 4-digit PIN. You can change this at any time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(16.dp))

                  Button(
                    onClick = { showChangePinDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("open_change_pin_btn")
                  ) {
                    Text("Change My PIN Now", fontWeight = FontWeight.Bold)
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(20.dp)) {
                  Text(
                    text = "Account Security Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "• Do not share your 4-digit PIN with others.\n• Admin will never ask for your confidential PIN.\n• For any discrepancy in your deposit records, contact society executives in Kalir Bazar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(30.dp))
            }
          }
        }
      }
    }

    if (showChangePinDialog) {
      ChangePinDialog(
        title = "Change My Login PIN",
        onDismiss = { showChangePinDialog = false },
        onConfirm = { oldPin, newPin, confirmPin ->
          viewModel.changeMemberPin(member.memberId, oldPin, newPin, confirmPin) { success, msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
            if (success) {
              showChangePinDialog = false
            }
          }
        }
      )
    }
  }
}

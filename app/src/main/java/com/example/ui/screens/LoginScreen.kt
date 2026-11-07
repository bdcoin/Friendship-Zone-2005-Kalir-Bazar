package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FriendshipViewModel
import com.example.ui.components.SocietyBrandHeader
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
  viewModel: FriendshipViewModel,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var selectedTabIndex by remember { mutableIntStateOf(0) } // 0 = Member, 1 = Admin

  // Member Login state
  var memberIdentifier by remember { mutableStateOf("") }
  var memberPin by remember { mutableStateOf("") }
  var isMemberPinVisible by remember { mutableStateOf(false) }

  // Admin Login state
  var adminPin by remember { mutableStateOf("") }
  var isAdminPinVisible by remember { mutableStateOf(false) }

  var isLoading by remember { mutableStateOf(false) }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(paddingValues)
        .imePadding()
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(12.dp))

        SocietyBrandHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Role Tabs
        TabRow(
          selectedTabIndex = selectedTabIndex,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .testTag("login_tabs")
        ) {
          Tab(
            selected = selectedTabIndex == 0,
            onClick = { selectedTabIndex = 0 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Person,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Member Login", fontWeight = FontWeight.Bold)
              }
            },
            modifier = Modifier.testTag("tab_member_login")
          )
          Tab(
            selected = selectedTabIndex == 1,
            onClick = { selectedTabIndex = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.AdminPanelSettings,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin Portal", fontWeight = FontWeight.Bold)
              }
            },
            modifier = Modifier.testTag("tab_admin_login")
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Login Card
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            if (selectedTabIndex == 0) {
              // MEMBER LOGIN
              Text(
                text = "Member Access",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Enter your Member ID or registered Mobile Number",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(16.dp))

              OutlinedTextField(
                value = memberIdentifier,
                onValueChange = { memberIdentifier = it },
                label = { Text("Member ID or Mobile Number") },
                placeholder = { Text("e.g. FZ-101 or 017xxxxxxxx") },
                leadingIcon = {
                  Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Next
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_member_id")
              )

              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = memberPin,
                onValueChange = { memberPin = it },
                label = { Text("4-Digit Login PIN") },
                placeholder = { Text("••••") },
                leadingIcon = {
                  Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                },
                trailingIcon = {
                  IconButton(onClick = { isMemberPinVisible = !isMemberPinVisible }) {
                    Icon(
                      imageVector = if (isMemberPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = if (isMemberPinVisible) "Hide PIN" else "Show PIN"
                    )
                  }
                },
                visualTransformation = if (isMemberPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.NumberPassword,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (!isLoading) {
                      isLoading = true
                      viewModel.loginAsMember(memberIdentifier, memberPin) { success, msg ->
                        isLoading = false
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                      }
                    }
                  }
                ),
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_member_pin")
              )

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = {
                  isLoading = true
                  viewModel.loginAsMember(memberIdentifier, memberPin) { success, msg ->
                    isLoading = false
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                },
                enabled = !isLoading && memberIdentifier.isNotBlank() && memberPin.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("btn_member_login")
              ) {
                if (isLoading) {
                  CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                } else {
                  Text(
                    "Sign In to Dashboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "New member? Account and initial PIN are created exclusively by the society Admin.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

            } else {
              // ADMIN LOGIN
              Text(
                text = "Society Administrator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Authorized access to create members, edit profiles & log deposits",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(16.dp))

              OutlinedTextField(
                value = adminPin,
                onValueChange = { adminPin = it },
                label = { Text("Admin Security PIN") },
                placeholder = { Text("Enter Admin PIN (Default: 2005)") },
                leadingIcon = {
                  Icon(
                    Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                  )
                },
                trailingIcon = {
                  IconButton(onClick = { isAdminPinVisible = !isAdminPinVisible }) {
                    Icon(
                      imageVector = if (isAdminPinVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                      contentDescription = if (isAdminPinVisible) "Hide PIN" else "Show PIN"
                    )
                  }
                },
                visualTransformation = if (isAdminPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.NumberPassword,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = {
                    if (!isLoading) {
                      isLoading = true
                      viewModel.loginAsAdmin(adminPin) { success, msg ->
                        isLoading = false
                        scope.launch { snackbarHostState.showSnackbar(msg) }
                      }
                    }
                  }
                ),
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_admin_pin")
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    text = "Default Admin PIN: 2005",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                androidx.compose.material3.TextButton(
                  onClick = { adminPin = "2005" }
                ) {
                  Text("Quick Fill 2005", style = MaterialTheme.typography.labelSmall)
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  isLoading = true
                  viewModel.loginAsAdmin(adminPin) { success, msg ->
                    isLoading = false
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                  }
                },
                enabled = !isLoading && adminPin.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("btn_admin_login")
              ) {
                if (isLoading) {
                  CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                } else {
                  Text(
                    "Enter Admin Portal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Only the Admin can register members and edit transactions. Admin PIN can be changed in settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
          text = "Friendship Zone (2005) • Kalir Bazar",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
  }
}

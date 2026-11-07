package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FriendshipUiState
import com.example.ui.FriendshipViewModel
import com.example.ui.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberDashboardScreen
import com.example.ui.theme.FriendshipZoneTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      FriendshipZoneTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          FriendshipZoneApp()
        }
      }
    }
  }
}

@Composable
fun FriendshipZoneApp(
  viewModel: FriendshipViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  when (uiState.role) {
    UserRole.GUEST -> {
      LoginScreen(viewModel = viewModel)
    }
    UserRole.MEMBER -> {
      MemberDashboardScreen(viewModel = viewModel)
    }
    UserRole.ADMIN -> {
      AdminDashboardScreen(viewModel = viewModel)
    }
  }
}

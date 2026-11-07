package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FriendshipUiState
import com.example.ui.FriendshipViewModel
import com.example.ui.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CloudConnectScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemberDashboardScreen
import com.example.ui.theme.FriendshipZoneTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

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
  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose {
      Firebase.auth.removeAuthStateListener(listener)
    }
  }

  if (currentUser == null) {
    CloudConnectScreen(
      onConnected = {
        currentUser = Firebase.auth.currentUser
      }
    )
  } else {
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
}

package com.cycling.sonic

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cycling.presentation.navigation.AppNavGraph
import com.cycling.presentation.player.PlayerViewModel
import com.cycling.presentation.theme.SonicTheme
import com.cycling.sonic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            val playerViewModel: PlayerViewModel = hiltViewModel()

            SonicTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}

@Composable
private fun MainScreen(
    navController: NavHostController,
    playerViewModel: PlayerViewModel
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AppNavGraph(
            navController = navController,
            onShowToast = { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            playerViewModel = playerViewModel
        )
    }
}

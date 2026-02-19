package com.cycling.sonic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cycling.presentation.navigation.AppNavGraph
import com.cycling.sonic.ui.theme.SonicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SonicTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        onShowToast = { message ->
                            android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

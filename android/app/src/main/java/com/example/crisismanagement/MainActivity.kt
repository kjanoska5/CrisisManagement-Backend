package com.example.crisismanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.crisismanagement.app.common.navigation.AppNavHost
import com.example.crisismanagement.ui.theme.CrisisManagementAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CrisisManagementAppTheme {
                AppNavHost()
            }
        }
    }
}
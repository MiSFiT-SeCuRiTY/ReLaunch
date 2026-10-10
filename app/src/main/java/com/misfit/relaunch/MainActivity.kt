package com.misfit.relaunch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misfit.relaunch.ui.HomeScreen
import com.misfit.relaunch.ui.HomeViewModel
import com.misfit.relaunch.ui.theme.ReLaunchTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReLaunchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(application)
                    )
                    HomeScreen(viewModel = vm)
                }
            }
        }
    }
}
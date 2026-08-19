package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.GardenScreen
import com.example.ui.GardenViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.worker.PlantWorkScheduler

class MainActivity : ComponentActivity() {

    private val viewModel: GardenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule periodic background updates for plant growth and hydration
        PlantWorkScheduler.schedulePeriodicPlantSync(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    GardenScreen(viewModel = viewModel)
                }
            }
        }
    }
}

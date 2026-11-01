package com.assetcalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.assetcalc.app.data.AppDatabase
import com.assetcalc.app.data.AssetRepository
import com.assetcalc.app.ui.AssetScreen
import com.assetcalc.app.ui.AssetViewModel
import com.assetcalc.app.ui.theme.AssetCalcTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AssetRepository(AppDatabase.get(this).assetDao())

        setContent {
            AssetCalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AssetApp(repository)
                }
            }
        }
    }
}

@Composable
private fun AssetApp(repository: AssetRepository) {
    val viewModel: AssetViewModel = viewModel(factory = AssetViewModel.factory(repository))
    AssetScreen(viewModel = viewModel)
}

package com.ntt.generativeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntt.generativeai.camera.CameraScreen
import com.ntt.generativeai.summary.SummaryRoute
import com.ntt.generativeai.ui.theme.GenerativeAITheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

const val LOADING_SCREEN = "loading_screen"
const val CAMERA_SCREEN = "camera_screen"
const val ANALYZE_SCREEN = "analyze_screen"

class MainActivity : ComponentActivity() {
    val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenerativeAITheme {
                val scanned = remember { mutableStateListOf<File>() }
                Scaffold { innerPadding ->
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White,
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = LOADING_SCREEN
                        ) {
                            composable(LOADING_SCREEN) {
                                LoadingRoute(
                                    mainViewModel.llm,
                                    onModelLoaded = {
                                        /*navController.navigate(CAMERA_SCREEN) {
                                            popUpTo(LOADING_SCREEN) { inclusive = true }
                                            launchSingleTop = true
                                        }*/
                                    }
                                )
                            }

                            composable(ANALYZE_SCREEN) {
                                SummaryRoute(mainViewModel.llm.partialResults)
                            }

                            composable(CAMERA_SCREEN) {
                                CameraScreen(Modifier, scanned) {
                                     mainViewModel.analyzeText(scanned)
                                     navController.navigate(ANALYZE_SCREEN)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
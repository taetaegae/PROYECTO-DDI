package com.appw.appwear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.appw.appwear.ui.LevelUpRoot
import com.appw.appwear.ui.theme.LevelUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpTheme {
                LevelUpRoot()
            }
        }
    }
}

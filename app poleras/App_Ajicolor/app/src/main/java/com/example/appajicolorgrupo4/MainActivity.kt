package com.example.appajicolorgrupo4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.appajicolorgrupo4.navigation.AppNavigation
import com.example.appajicolorgrupo4.ui.theme.AppAjiColorGrupo4Theme

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppAjiColorGrupo4Theme { AppNavigation() } }
    }
}

package com.example.minutanutricional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.minutanutricional.ui.*
import com.example.minutanutricional.ui.theme.MinutaNutricionalTheme

enum class Screen { LOGIN, REGISTER, FORGOT_PASSWORD, MINUTA }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

            MinutaNutricionalTheme {
                when (currentScreen) {
                    Screen.LOGIN -> LoginScreen(
                        onLoginSuccess = { currentScreen = Screen.MINUTA },
                        onNavigateToRegister = { currentScreen = Screen.REGISTER },
                        onNavigateToForgotPassword = { currentScreen = Screen.FORGOT_PASSWORD }
                    )
                    Screen.REGISTER -> RegisterScreen(
                        onRegisterSuccess = { currentScreen = Screen.MINUTA },
                        onBackToLogin = { currentScreen = Screen.LOGIN }
                    )
                    Screen.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        onBackToLogin = { currentScreen = Screen.LOGIN }
                    )
                    Screen.MINUTA -> MinutaScreen(
                        onLogout = { currentScreen = Screen.LOGIN }
                    )
                }
            }
        }
    }
}
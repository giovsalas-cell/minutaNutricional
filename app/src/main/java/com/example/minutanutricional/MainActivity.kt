package com.example.minutanutricional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.minutanutricional.ui.theme.MinutaNutricionalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinutaNutricionalTheme {
                val navController = rememberNavController()
                var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Rutas.LOGIN,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Rutas.LOGIN) {
                            LoginScreen(
                                onLoginSuccess = { usuario ->
                                    usuarioLogueado = usuario
                                    navController.navigate(Rutas.MINUTA) {
                                        popUpTo(Rutas.LOGIN) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Rutas.REGISTER)
                                },
                                onNavigateToForgotPassword = {
                                    navController.navigate(Rutas.FORGOT_PASSWORD)
                                }
                            )
                        }

                        composable(Rutas.REGISTER) {
                            RegistroScreen(
                                onRegisterSuccess = { navController.popBackStack() },
                                onBackToLogin = { navController.popBackStack() }
                            )
                        }

                        composable(Rutas.FORGOT_PASSWORD) {
                            ForgotPasswordScreen(
                                onBackToLogin = { navController.popBackStack() },
                                onCodigoGenerado = { email -> }
                            )
                        }

                        composable(Rutas.MINUTA) {
                            MinutaScreen(
                                usuarioActual = usuarioLogueado,
                                onLogout = {
                                    usuarioLogueado = null
                                    navController.navigate(Rutas.LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onBuscarReceta = {
                                    navController.navigate(Rutas.BUSCAR_RECETA)
                                },
                                onAdminRecetas = {
                                    navController.navigate(Rutas.ADMIN_RECETAS)
                                }
                            )
                        }

                        composable(Rutas.BUSCAR_RECETA) {
                            BuscarRecetaScreen(
                                onVolver = { navController.popBackStack() }
                            )
                        }
                        composable(Rutas.ADMIN_RECETAS) {
                            AdminRecetasScreen(
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

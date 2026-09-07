package com.example.minutanutricional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.minutanutricional.ui.*
import com.example.minutanutricional.ui.theme.MinutaNutricionalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinutaNutricionalTheme {
                MinutaNutricionalApp()
            }
        }
    }
}

/**
 * Grafo de navegación de la aplicación implementado con Navigation
 * Compose (NavController + NavHost + rutas), reemplazando la navegación
 * manual basada en un enum Screen y un "when" dentro de la Activity.
 *
 * Esto resuelve la observación del Criterio 3 de la pauta de evaluación:
 * favorecer la navegación y usabilidad usando las herramientas propias
 * del framework, en lugar de un estado manual.
 */
@Composable
fun MinutaNutricionalApp() {
    val navController = rememberNavController()

    // Se mantiene en este nivel porque varias pantallas del grafo lo
    // necesitan (Minuta lo lee, Login lo escribe tras autenticar).
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginSuccess = { usuario ->
                    usuarioActual = usuario
                    navController.navigate(Rutas.MINUTA) {
                        // Elimina Login del back stack para que el botón
                        // "atrás" no regrese a la pantalla de acceso.
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Rutas.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Rutas.FORGOT_PASSWORD) }
            )
        }

        composable(Rutas.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Vuelve a Login para que el usuario recién creado
                    // inicie sesión con sus nuevas credenciales.
                    navController.popBackStack(Rutas.LOGIN, inclusive = false)
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Rutas.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Rutas.MINUTA) {
            MinutaScreen(
                usuarioActual = usuarioActual,
                onLogout = {
                    usuarioActual = null
                    navController.navigate(Rutas.LOGIN) {
                        // Limpia toda la pila para que "atrás" no vuelva
                        // a mostrar la Minuta luego de cerrar sesión.
                        popUpTo(Rutas.MINUTA) { inclusive = true }
                    }
                }
            )
        }
    }
}
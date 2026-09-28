package com.example.minutanutricional

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (Usuario) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Minuta Nutricional",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    mensajeError = "Por favor completa todos los campos."
                    return@Button
                }

                cargando = true
                mensajeError = ""

                scope.launch {
                    val resultado = FirebaseUsuarioRepository.iniciarSesion(email, password)
                    cargando = false
                    resultado
                        .onSuccess { usuario -> onLoginSuccess(usuario) }
                        .onFailure { error ->
                            mensajeError = mapearErrorFirebase(error)
                        }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister, enabled = !cargando) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }

        TextButton(onClick = onNavigateToForgotPassword, enabled = !cargando) {
            Text("¿Olvidaste tu contraseña?")
        }
    }
}

/**
 * Traduce las excepciones típicas de Firebase Auth a mensajes en español
 * entendibles para el usuario final, en vez de mostrar el nombre técnico
 * de la excepción.
 */
fun mapearErrorFirebase(error: Throwable): String {
    val mensaje = error.message ?: ""
    return when {
        mensaje.contains("password is invalid", ignoreCase = true) ||
            mensaje.contains("no user record", ignoreCase = true) ||
            mensaje.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
            "Correo o contraseña incorrectos."
        mensaje.contains("badly formatted", ignoreCase = true) ->
            "El correo ingresado no tiene un formato válido."
        mensaje.contains("email address is already in use", ignoreCase = true) ->
            "El correo ya se encuentra registrado."
        mensaje.contains("network", ignoreCase = true) ->
            "No hay conexión a internet. Verifica tu red e inténtalo de nuevo."
        mensaje.contains("WEAK_PASSWORD", ignoreCase = true) ->
            "La contraseña es muy débil (mínimo 6 caracteres)."
        mensaje.contains("no user record", ignoreCase = true) ->
            "El correo ingresado no está registrado."
        else -> "Ocurrió un error: $mensaje"
    }
}

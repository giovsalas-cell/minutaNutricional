package com.example.minutanutricional

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A diferencia de la versión anterior (que dejaba escribir una contraseña
 * nueva directamente y la guardaba en SQLite), Firebase Authentication no
 * permite cambiar la contraseña de un usuario sin que esté logueado o sin
 * pasar por el enlace que Firebase manda al correo. Por eso esta pantalla
 * ahora solo pide el correo y dispara el envío de ese enlace de
 * recuperación oficial de Firebase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onCodigoGenerado: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Te enviaremos un correo con un enlace para crear una nueva contraseña.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
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

        if (mensajeExito.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeExito,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isBlank()) {
                    mensajeError = "Por favor ingresa tu correo electrónico."
                    mensajeExito = ""
                    return@Button
                }

                cargando = true
                mensajeError = ""
                mensajeExito = ""

                scope.launch {
                    val resultado = FirebaseUsuarioRepository.enviarCorreoDeRecuperacion(email)
                    cargando = false
                    resultado
                        .onSuccess {
                            mensajeExito = "Te enviamos un correo a ${email.trim()} con instrucciones para recuperar tu contraseña."
                            onCodigoGenerado(email.trim())
                        }
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
                Text("Enviar correo de recuperación")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin, enabled = !cargando) {
            Text("Volver al Login")
        }
    }
}

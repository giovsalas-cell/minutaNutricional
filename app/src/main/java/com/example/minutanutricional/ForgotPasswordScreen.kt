package com.example.minutanutricional

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    onCodigoGenerado: (String) -> Unit
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var mensajeExito by remember { mutableStateOf("") }

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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nuevaPassword,
            onValueChange = { nuevaPassword = it },
            label = { Text("Nueva contraseña") },
            visualTransformation = PasswordVisualTransformation(),
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
                if (email.isBlank() || nuevaPassword.isBlank()) {
                    mensajeError = "Por favor completa todos los campos."
                    mensajeExito = ""
                    return@Button
                }

                val existe = UsuarioRepository.existeUsuario(context, email)

                if (existe) {
                    val actualizado = UsuarioRepository.actualizarPassword(context, email, nuevaPassword)
                    if (actualizado) {
                        mensajeError = ""
                        mensajeExito = "¡Contraseña actualizada con éxito!"
                        onCodigoGenerado(email.trim())
                    } else {
                        mensajeError = "No se pudo actualizar la contraseña."
                        mensajeExito = ""
                    }
                } else {
                    mensajeError = "El correo ingresado no está registrado."
                    mensajeExito = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Actualizar Contraseña")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin) {
            Text("Volver al Login")
        }
    }
}
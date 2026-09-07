package com.example.minutanutricional.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minutanutricional.R
import com.example.minutanutricional.Usuario
import com.example.minutanutricional.UsuarioRepository

@Composable
fun LoginScreen(
    onLoginSuccess: (Usuario) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Imagen de fondo ---
        Image(
            painter = painterResource(id = R.drawable.fondo_cocina),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // --- Tarjeta semitransparente con el formulario ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Minuta Nutricional",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 28.sp
            )
            Text(
                text = " ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = usuario,
                onValueChange = {
                    usuario = it
                    mensajeError = ""
                },
                label = { Text("Correo electrónico ") },
                isError = mensajeError.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    mensajeError = ""
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = mensajeError.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        usuario.isBlank() || password.isBlank() -> {
                            mensajeError = "Por favor ingresa tu correo y contraseña."
                        }
                        else -> {
                            val usuarioValidado = UsuarioRepository.validarCredenciales(usuario.trim(), password.trim())
                            if (usuarioValidado != null) {
                                mensajeError = ""
                                onLoginSuccess(usuarioValidado)
                            } else {
                                mensajeError = "Usuario o contraseña incorrectos."
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ingresar", fontSize = 18.sp)
            }

            if (mensajeError.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToForgotPassword) {
                Text("¿Olvidaste tu contraseña?")
            }

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}
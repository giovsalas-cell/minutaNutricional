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
fun RegistroScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tipoMinuta by remember { mutableStateOf("Estándar / Familiar") }
    var mensajeError by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    var expandidoTipo by remember { mutableStateOf(false) }
    val tiposDisponibles = listOf("Estándar / Familiar", "Vegetariana", "Hipocalórica")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            enabled = !cargando,
            supportingText = { Text(Validaciones.AYUDA_PASSWORD) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandidoTipo,
            onExpandedChange = { if (!cargando) expandidoTipo = !expandidoTipo },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tipoMinuta,
                onValueChange = {},
                readOnly = true,
                enabled = !cargando,
                label = { Text("Tipo de minuta preferido") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipo) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expandidoTipo,
                onDismissRequest = { expandidoTipo = false }
            ) {
                tiposDisponibles.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo) },
                        onClick = {
                            tipoMinuta = tipo
                            expandidoTipo = false
                        }
                    )
                }
            }
        }

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
                if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                    mensajeError = "Por favor completa todos los campos."
                    return@Button
                }
                if (!Validaciones.emailEsValido(email)) {
                    mensajeError = Validaciones.AYUDA_EMAIL
                    return@Button
                }
                if (!Validaciones.passwordEsValida(password)) {
                    mensajeError = Validaciones.AYUDA_PASSWORD
                    return@Button
                }

                cargando = true
                mensajeError = ""

                scope.launch {
                    val resultado = FirebaseUsuarioRepository.registrarUsuario(
                        nombre = nombre.trim(),
                        email = email,
                        password = password,
                        tipoMinuta = tipoMinuta
                    )
                    cargando = false
                    resultado
                        .onSuccess { onRegisterSuccess() }
                        .onFailure { error -> mensajeError = mapearErrorFirebase(error) }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBackToLogin, enabled = !cargando) {
            Text("¿Ya tienes cuenta? Inicia sesión")
        }
    }
}

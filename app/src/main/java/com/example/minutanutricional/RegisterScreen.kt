package com.example.minutanutricional.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.minutanutricional.Usuario
import com.example.minutanutricional.UsuarioRepository
import com.example.minutanutricional.Validaciones

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val opcionesTipo = listOf("Estándar / Familiar", "Vegetariana", "Hipocalórica")
    var tipoSeleccionado by remember { mutableStateOf(opcionesTipo[0]) }

    // Validaciones en vivo, reutilizadas tanto para mostrar ayuda como
    // para habilitar/deshabilitar el botón de registro.
    val emailFormatoValido = email.isBlank() || Validaciones.emailEsValido(email)
    val passwordFormatoValido = password.isBlank() || Validaciones.passwordEsValida(password)

    val formularioValido = nombre.isNotBlank() &&
            Validaciones.emailEsValido(email) &&
            Validaciones.passwordEsValida(password) &&
            aceptaTerminos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                mensajeError = ""
            },
            label = { Text("Nombre completo") },
            isError = nombre.isEmpty() && mensajeError.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                mensajeError = ""
            },
            label = { Text("Correo electrónico") },
            isError = !emailFormatoValido,
            supportingText = {
                if (!emailFormatoValido) {
                    Text(Validaciones.AYUDA_EMAIL)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                mensajeError = ""
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = !passwordFormatoValido,
            supportingText = {
                Text(Validaciones.AYUDA_PASSWORD)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Preferencia de Minuta:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        opcionesTipo.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = (opcion == tipoSeleccionado),
                    onClick = { tipoSeleccionado = opcion }
                )
                Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it }
            )
            Text(text = "Acepto los términos y condiciones de uso")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val nombreLimpio = nombre.trim()
                val emailLimpio = email.trim()
                val passwordLimpio = password.trim()

                when {
                    nombreLimpio.isBlank() -> {
                        mensajeError = "Ingresa tu nombre completo."
                    }
                    !Validaciones.emailEsValido(emailLimpio) -> {
                        mensajeError = "El correo ingresado no tiene un formato válido."
                    }
                    !Validaciones.passwordEsValida(passwordLimpio) -> {
                        mensajeError = "La contraseña no cumple los requisitos mínimos."
                    }
                    UsuarioRepository.existeUsuario(emailLimpio) -> {
                        mensajeError = "Ya existe una cuenta registrada con este correo."
                    }
                    !aceptaTerminos -> {
                        mensajeError = "Debes aceptar los términos y condiciones."
                    }
                    else -> {
                        val nuevoUsuario = Usuario(
                            nombre = nombreLimpio,
                            email = emailLimpio,
                            password = passwordLimpio,
                            tipoMinuta = tipoSeleccionado
                        )
                        UsuarioRepository.registrarUsuario(nuevoUsuario)
                        mensajeError = ""
                        // Volvemos al Login para que inicie sesión con su cuenta recién creada
                        onRegisterSuccess()
                    }
                }
            },
            enabled = formularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        if (mensajeError.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        TextButton(onClick = onBackToLogin) {
            Text("Volver al Inicio de Sesión")
        }
    }
}
package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.domain.repository.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth

actual suspend fun registerPlatformUser(
    email: String,
    password: String,
    name: String,
    auth: FirebaseAuth
): AuthResult {
    return try {
        val result = auth.createUserWithEmailAndPassword(email, password)
        val user = result.user ?: return AuthResult.Error("Error al crear cuenta")
        try {
            user.updateProfile(displayName = name)
        } catch (_: Throwable) {}
        AuthResult.Success(user.uid)
    } catch (e: Throwable) {
        val msg = e.message ?: "Error de registro"
        val friendlyMsg = when {
            msg.contains("email-already-in-use", ignoreCase = true) || msg.contains("EMAIL_EXISTS", ignoreCase = true) -> "Este correo electrónico ya está registrado."
            msg.contains("invalid-email", ignoreCase = true) || msg.contains("INVALID_EMAIL", ignoreCase = true) -> "El formato del correo no es válido."
            msg.contains("weak-password", ignoreCase = true) || msg.contains("WEAK_PASSWORD", ignoreCase = true) -> "La contraseña debe tener al menos 6 caracteres."
            msg.contains("network", ignoreCase = true) -> "Error de conexión. Verifica tu internet."
            else -> msg
        }
        AuthResult.Error(friendlyMsg)
    }
}

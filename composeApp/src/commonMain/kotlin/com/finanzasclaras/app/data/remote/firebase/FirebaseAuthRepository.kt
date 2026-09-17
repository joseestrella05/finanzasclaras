package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.AuthResult
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class FirebaseAuthRepository : AuthRepository {
    private val auth by lazy { Firebase.auth }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password)
            val uid = result.user?.uid ?: return AuthResult.Error("Error al iniciar sesión")
            AuthResult.Success(uid)
        } catch (e: Throwable) {
            val msg = e.message ?: "Error de autenticación"
            val friendlyMsg = when {
                msg.contains("user-not-found", ignoreCase = true) || msg.contains("wrong-password", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) || msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) -> "Correo o contraseña incorrectos."
                msg.contains("invalid-email", ignoreCase = true) || msg.contains("INVALID_EMAIL", ignoreCase = true) -> "El formato del correo no es válido."
                msg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) -> "Firebase Authentication no está habilitado en tu consola. Debes entrar a Firebase Console > Authentication y activar 'Correo electrónico/contraseña'."
                msg.contains("network", ignoreCase = true) || msg.contains("host", ignoreCase = true) -> "Error de conexión. Verifica tu internet."
                else -> msg
            }
            AuthResult.Error(friendlyMsg)
        }
    }

    override suspend fun register(email: String, password: String, name: String): AuthResult {
        return registerPlatformUser(email.trim(), password, name.trim(), auth)
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Throwable) {}
    }

    override suspend fun getCurrentUserId(): String? {
        return try {
            auth.currentUser?.uid
        } catch (_: Throwable) {
            null
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            auth.currentUser != null
        } catch (_: Throwable) {
            false
        }
    }

    override suspend fun sendPasswordReset(email: String): AuthResult {
        return try {
            auth.sendPasswordResetEmail(email.trim())
            AuthResult.Success("")
        } catch (e: Throwable) {
            AuthResult.Error(e.message ?: "Error al enviar correo")
        }
    }
}

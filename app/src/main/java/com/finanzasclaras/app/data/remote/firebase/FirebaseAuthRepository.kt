package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.domain.repository.AuthRepository
import com.finanzasclaras.app.domain.repository.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return AuthResult.Error("Error al iniciar sesión")
            AuthResult.Success(uid)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error de autenticación")
        }
    }

    override suspend fun register(email: String, password: String, name: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return AuthResult.Error("Error al crear cuenta")
            user.updateProfile(userProfileChangeRequest { displayName = name }).await()
            AuthResult.Success(user.uid)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error de registro")
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override suspend fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun sendPasswordReset(email: String): AuthResult {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            AuthResult.Success("")
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al enviar correo")
        }
    }
}

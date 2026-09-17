package com.finanzasclaras.app.domain.repository

import com.finanzasclaras.app.domain.model.Transaction
import com.finanzasclaras.app.domain.model.SavingGoal
import com.finanzasclaras.app.domain.model.Investment

sealed class AuthResult {
    data class Success(val userId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String, name: String): AuthResult
    suspend fun logout()
    suspend fun getCurrentUserId(): String?
    suspend fun isLoggedIn(): Boolean
    suspend fun sendPasswordReset(email: String): AuthResult
}

package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.domain.repository.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth

expect suspend fun registerPlatformUser(
    email: String,
    password: String,
    name: String,
    auth: FirebaseAuth
): AuthResult

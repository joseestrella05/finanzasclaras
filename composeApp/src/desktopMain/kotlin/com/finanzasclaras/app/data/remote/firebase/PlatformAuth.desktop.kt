package com.finanzasclaras.app.data.remote.firebase

import com.finanzasclaras.app.domain.repository.AuthResult
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URI

private const val FIREBASE_API_KEY = "AIzaSyCz-zY8DdpmEqIQhNLrjvd0LPwZpTvNTTc"

actual suspend fun registerPlatformUser(
    email: String,
    password: String,
    name: String,
    auth: FirebaseAuth
): AuthResult = withContext(Dispatchers.IO) {
    try {
        val signUpUrl = URI("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$FIREBASE_API_KEY").toURL()
        val jsonParser = Json { ignoreUnknownKeys = true }

        val escapedEmail = email.replace("\\", "\\\\").replace("\"", "\\\"")
        val escapedPassword = password.replace("\\", "\\\\").replace("\"", "\\\"")
        val escapedName = name.replace("\\", "\\\\").replace("\"", "\\\"")

        val reqBody = """{"email":"$escapedEmail","password":"$escapedPassword","returnSecureToken":true}"""

        val conn = (signUpUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            connectTimeout = 15000
            readTimeout = 15000
        }

        conn.outputStream.use { os ->
            os.write(reqBody.toByteArray(Charsets.UTF_8))
        }

        val responseCode = conn.responseCode
        val responseText = if (responseCode in 200..299) {
            conn.inputStream.bufferedReader().use { it.readText() }
        } else {
            conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
        }

        if (responseCode !in 200..299) {
            val errorMsg = try {
                val elem = jsonParser.parseToJsonElement(responseText)
                elem.jsonObject["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content ?: "Error de registro"
            } catch (_: Throwable) {
                "Error de registro ($responseCode)"
            }
            val friendlyMsg = when {
                errorMsg.contains("EMAIL_EXISTS", ignoreCase = true) -> "Este correo electrónico ya está registrado."
                errorMsg.contains("INVALID_EMAIL", ignoreCase = true) -> "El formato del correo no es válido."
                errorMsg.contains("WEAK_PASSWORD", ignoreCase = true) -> "La contraseña debe tener al menos 6 caracteres."
                errorMsg.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) -> "Firebase Authentication no está habilitado en tu consola. Debes entrar a Firebase Console > Authentication y activar 'Correo electrónico/contraseña'."
                else -> errorMsg
            }
            return@withContext AuthResult.Error(friendlyMsg)
        }

        val parsedJson = jsonParser.parseToJsonElement(responseText).jsonObject
        val idToken = parsedJson["idToken"]?.jsonPrimitive?.content ?: ""
        val localId = parsedJson["localId"]?.jsonPrimitive?.content ?: ""

        // Update profile displayName if provided
        if (escapedName.isNotBlank() && idToken.isNotBlank()) {
            try {
                val updateUrl = URI("https://identitytoolkit.googleapis.com/v1/accounts:update?key=$FIREBASE_API_KEY").toURL()
                val updateConn = (updateUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    doOutput = true
                    connectTimeout = 15000
                    readTimeout = 15000
                }
                val updateBody = """{"idToken":"$idToken","displayName":"$escapedName","returnSecureToken":true}"""
                updateConn.outputStream.use { os ->
                    os.write(updateBody.toByteArray(Charsets.UTF_8))
                }
                updateConn.responseCode
            } catch (_: Throwable) {}
        }

        // Sign in with GitLive SDK to initialize local session and currentUser
        try {
            auth.signInWithEmailAndPassword(email, password)
        } catch (_: Throwable) {}

        AuthResult.Success(localId)
    } catch (e: Throwable) {
        val msg = e.message ?: "Error de conexión"
        val friendlyMsg = if (msg.contains("host", ignoreCase = true) || msg.contains("network", ignoreCase = true)) {
            "Error de conexión. Verifica tu internet."
        } else {
            msg
        }
        AuthResult.Error(friendlyMsg)
    }
}

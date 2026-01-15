package com.lostandfound.frontend_app.util

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
    }

    // Guarda o Token
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // --- NOVO: Guardar e Recuperar o ID do Utilizador ---
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Long {
        // Retorna -1 se não encontrar o ID
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    // Limpa tudo no Logout
    fun clearToken() {
        prefs.edit().clear().apply()
    }
}
package com.lostandfound.frontend_app.data.remote

import android.content.Context
import com.lostandfound.frontend_app.data.remote.API.ApiService
import com.lostandfound.frontend_app.data.remote.API.AuthInterceptor
import com.lostandfound.frontend_app.util.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Se for telemóvel real, mudar para o IP da rede (ex: 192.168.1.XX)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun getApiService(context: Context): ApiService {
        val tokenManager = TokenManager(context)

        // Log para ver as requisições no Logcat (ajuda muito no debug)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
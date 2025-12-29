package com.lostandfound.frontend_app

import ItemDetailScreen
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lostandfound.frontend_app.data.remote.RetrofitClient
import com.lostandfound.frontend_app.data.repository.AuthRepository
import com.lostandfound.frontend_app.data.repository.ItemRepository
import com.lostandfound.frontend_app.ui.Screen
import com.lostandfound.frontend_app.ui.screens.*
import com.lostandfound.frontend_app.ui.theme.FrontendappTheme
import com.lostandfound.frontend_app.ui.viewmodel.*
import com.lostandfound.frontend_app.util.TokenManager

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.getApiService(this)
        val authRepository = AuthRepository(apiService)
        val itemRepository = ItemRepository(apiService)
        val tokenManager = TokenManager(this)

        setContent {
            FrontendappTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {

                    // Rota de Login
                    composable(Screen.Login.route) {
                        val loginViewModel: LoginViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return LoginViewModel(authRepository, tokenManager) as T
                                }
                            }
                        )

                        LoginScreen(
                            viewModel = loginViewModel,
                            onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                        )

                        if (loginViewModel.isSuccess) {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }

                    // Rota de Registro
                    composable(Screen.Register.route) {
                        val registerViewModel: RegisterViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return RegisterViewModel(authRepository, tokenManager) as T
                                }
                            }
                        )

                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateToLogin = { navController.popBackStack() }
                        )

                        if (registerViewModel.isSuccess) {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    }

                    // Dashboard
                    // Dashboard
                    composable(Screen.Dashboard.route) {
                        val dashboardViewModel: DashboardViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return DashboardViewModel(itemRepository) as T
                                }
                            }
                        )

                        // Garante que os itens carreguem ao abrir a tela
                        LaunchedEffect(Unit) {
                            dashboardViewModel.loadItems()
                        }

                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onAddItemClick = { navController.navigate(Screen.CreateItem.route) },
                            onItemClick = { itemId ->
                                navController.navigate("item_detail/$itemId")
                            }
                        )
                    }

                    // Criar Item
                    composable(Screen.CreateItem.route) {
                        val createItemViewModel: CreateItemViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return CreateItemViewModel(itemRepository) as T
                                }
                            }
                        )

                        CreateItemScreen(
                            viewModel = createItemViewModel,
                            onItemCreated = { navController.popBackStack() },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Adiciona esta rota na MainActivity:
                    composable("item_detail/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")?.toLong() ?: 0L
                        val detailViewModel: ItemDetailViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return ItemDetailViewModel(itemRepository) as T
                                }
                            }
                        )

                        ItemDetailScreen(
                            itemId = itemId,
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() },
                            onContactClick = { userId ->
                                // O segredo está aqui: usamos o itemId que esta rota já possui
                                // e o userId que o botão acabou de nos dar.
                                navController.navigate("chat/$itemId/$userId")
                            }
                        )
                    }
                    // Dentro do seu NavHost na MainActivity
                    // ... seus outros composables (Login, Register, Dashboard, etc.)

// Rota de Chat Corrigida
                    composable(
                        route = "chat/{itemId}/{recipientId}",
                        arguments = listOf(
                            navArgument("itemId") { type = NavType.LongType },
                            navArgument("recipientId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
                        val recipientId = backStackEntry.arguments?.getLong("recipientId") ?: 0L

                        // 1. Recupera o Token real do TokenManager
                        val token = tokenManager.getToken() ?: ""

                        // 2. Cria o ChatViewModel usando a Factory (Igual você fez nas outras telas)
                        val chatViewModel: ChatViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    // Certifique-se que o construtor do ChatViewModel recebe o itemRepository
                                    return ChatViewModel(itemRepository) as T
                                }
                            }
                        )

                        // 3. Passa a instância correta para a Screen
                        ChatScreen(
                            itemId = itemId,
                            recipientId = recipientId,
                            viewModel = chatViewModel,
                            token = token,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

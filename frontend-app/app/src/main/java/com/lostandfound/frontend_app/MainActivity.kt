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

        // Inicialização das dependências
        val apiService = RetrofitClient.getApiService(this)
        val authRepository = AuthRepository(apiService)
        val itemRepository = ItemRepository(apiService)
        val tokenManager = TokenManager(this)

        setContent {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        ProfileViewModel(itemRepository, tokenManager) as T
                }
            )

            FrontendappTheme(darkTheme = profileViewModel.isDarkMode) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {

                    // 1. Rota de Perfil
                    composable(Screen.Profile.route) {
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onBack = { navController.popBackStack() },
                            onLogout = {
                                tokenManager.clearToken()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onEditClick = {
                                navController.navigate(Screen.EditProfile.route)
                            }
                        )
                    }

                    // 2. Rota de Edição de Perfil
                    composable(Screen.EditProfile.route) {
                        EditProfileScreen(
                            viewModel = profileViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 3. Rota de Login
                    composable(Screen.Login.route) {
                        val loginViewModel: LoginViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    LoginViewModel(authRepository, tokenManager) as T
                            }
                        )

                        LoginScreen(
                            viewModel = loginViewModel,
                            onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                        )

                        if (loginViewModel.isSuccess) {
                            LaunchedEffect(Unit) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        }
                    }

                    // 4. Rota de Registro
                    composable(Screen.Register.route) {
                        val registerViewModel: RegisterViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    RegisterViewModel(authRepository, tokenManager) as T
                            }
                        )

                        RegisterScreen(
                            viewModel = registerViewModel,
                            onNavigateToLogin = { navController.popBackStack() }
                        )

                        if (registerViewModel.isSuccess) {
                            LaunchedEffect(Unit) {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            }
                        }
                    }

                    // 5. Dashboard
                    composable(Screen.Dashboard.route) {
                        val dashboardViewModel: DashboardViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    DashboardViewModel(itemRepository) as T
                            }
                        )

                        LaunchedEffect(Unit) {
                            dashboardViewModel.loadItems()
                        }

                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onAddItemClick = { navController.navigate(Screen.CreateItem.route) },
                            onItemClick = { itemId ->
                                navController.navigate("item_detail/$itemId")
                            },
                            onChatIconClick = {
                                navController.navigate(Screen.MessagesList.route)
                            },
                            onProfileClick = {
                                navController.navigate(Screen.Profile.route)
                            }
                        )
                    }

                    // 6. Lista de Mensagens (Inbox)
                    composable(Screen.MessagesList.route) {
                        val token = tokenManager.getToken() ?: ""
                        val chatViewModel: ChatViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    ChatViewModel(itemRepository) as T
                            }
                        )

                        MessagesListScreen(
                            viewModel = chatViewModel,
                            token = token,
                            onBackClick = { navController.popBackStack() },
                            onChatClick = { chatId, recipientId, itemId ->
                                navController.navigate("chat/$itemId/$recipientId")
                            }
                        )
                    }

                    // 7. Detalhe do Item
                    composable(
                        route = "item_detail/{itemId}",
                        arguments = listOf(navArgument("itemId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
                        val detailViewModel: ItemDetailViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    ItemDetailViewModel(itemRepository) as T
                            }
                        )

                        ItemDetailScreen(
                            itemId = itemId,
                            viewModel = detailViewModel,
                            onBack = { navController.popBackStack() },
                            onContactClick = { userId ->
                                navController.navigate("chat/$itemId/$userId")
                            }
                        )
                    }

                    // 8. Criar Item
                    composable(Screen.CreateItem.route) {
                        val createItemViewModel: CreateItemViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    CreateItemViewModel(itemRepository) as T
                            }
                        )

                        CreateItemScreen(
                            viewModel = createItemViewModel,
                            onItemCreated = { navController.popBackStack() },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 9. Chat Individual
                    composable(
                        route = "chat/{itemId}/{recipientId}",
                        arguments = listOf(
                            navArgument("itemId") { type = NavType.LongType },
                            navArgument("recipientId") { type = NavType.LongType }
                        )
                    ) { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0L
                        val recipientId = backStackEntry.arguments?.getLong("recipientId") ?: 0L
                        val token = tokenManager.getToken() ?: ""

                        val chatViewModel: ChatViewModel = viewModel(
                            factory = object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                                    ChatViewModel(itemRepository) as T
                            }
                        )

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
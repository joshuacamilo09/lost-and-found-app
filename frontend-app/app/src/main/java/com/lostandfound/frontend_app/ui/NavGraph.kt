package com.lostandfound.frontend_app.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object CreateItem : Screen("create_item")
    object MessagesList : Screen("messages_list") // Nova rota para a Inbox

    // Helper para rotas com argumentos
    fun createChatRoute(itemId: Long, recipientId: Long) = "chat/$itemId/$recipientId"
    fun createItemDetailRoute(itemId: Long) = "item_detail/$itemId"
}
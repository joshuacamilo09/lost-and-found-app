package com.lostandfound.frontend_app.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object CreateItem : Screen("create_item")
}
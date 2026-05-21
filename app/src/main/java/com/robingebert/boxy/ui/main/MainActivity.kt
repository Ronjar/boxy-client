package com.robingebert.boxy.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.robingebert.boxy.ui.main.composables.ServerConnectionDetailsDialog
import com.robingebert.boxy.ui.navigation.AppNavigation
import com.robingebert.boxy.ui.theme.BoxyTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject

data class LoginData(val username: String, val token: String, val url: String?)

class MainActivity : ComponentActivity() {
    private val deepLinkData = MutableStateFlow<LoginData?>(null)

    private val viewModel by inject<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val loginData by deepLinkData.collectAsState()

            BoxyTheme {
                if (loginData != null) {
                    ServerConnectionDetailsDialog(
                        initialUrl = loginData!!.url ?: "",
                        initialUsername = loginData!!.username,
                        initialToken = loginData!!.token,
                        onDismiss = {
                            deepLinkData.value = null
                        },
                        onConnect = { finalUrl, finalUsername, finalToken ->
                            viewModel.setCredentials(finalUrl, finalUsername, finalToken)
                            deepLinkData.value = null
                        }
                    )
                }
                AppNavigation(navController = navController)
            }
        }
        handleDeepLink()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink()
    }

    private fun handleDeepLink() {
        val uri = intent?.data

        if (uri?.scheme == "boxy" && uri.host == "login") {
            val username = uri.getQueryParameter("username")
            val token = uri.getQueryParameter("token")
            val url = uri.getQueryParameter("url")

            if (username != null && token != null) {
                deepLinkData.value = LoginData(username, token, url)
            }
        }
    }
}
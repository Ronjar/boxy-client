package com.robingebert.boxy.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.robingebert.boxy.ui.main.composables.MainLayout
import com.robingebert.boxy.ui.main.composables.ServerConnectionDetailsDialog
import com.robingebert.boxy.ui.navigation.AppNavigation
import com.robingebert.boxy.ui.navigation.Destination
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
            var destination by remember { mutableStateOf(Destination()) }
            val loginData by deepLinkData.collectAsState()

            val localChanges by viewModel.localChanges.collectAsStateWithLifecycle()
            val newRemoteVersion by viewModel.remoteChanges.collectAsStateWithLifecycle()

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

                MainLayout(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController,
                    destination = destination,
                    hasLocalChanges = localChanges,
                    hasRemoteChanges = newRemoteVersion
                ) {
                    AppNavigation(navController = navController) {
                        destination = it
                    }
                }
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
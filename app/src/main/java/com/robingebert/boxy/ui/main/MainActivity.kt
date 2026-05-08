package com.robingebert.boxy.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.robingebert.boxy.data.DataStoreManager
import com.robingebert.boxy.ui.auth.AuthScreen
import com.robingebert.boxy.ui.navigation.AppNavigation
import com.robingebert.boxy.ui.theme.BoxyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //val dataStoreManager = DataStoreManager(applicationContext)
            //val loginToken by dataStoreManager.loginToken.flow.collectAsState()
            val navController = rememberNavController()

            BoxyTheme {
                /*if (loginToken.isEmpty()) {
                    AuthScreen()
                } else {*/
                    AppNavigation(navController = navController)
                //}
            }
        }
    }
}
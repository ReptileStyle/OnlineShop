package com.example.onlineshop.ui.splash

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.ui.registration.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen (
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
    onNavigate: (route: String, popBackStack: Boolean) -> Unit = {_,_ ->}
){
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when(it) {
                is UiEvent.Navigate -> onNavigate(it.route, it.popBackStack)
                is UiEvent.NavigateUp -> onNavigateUp()
                is UiEvent.Message -> Toast.makeText(context,it.text,it.length).show()
                else -> {}
            }
        }
    }
    Scaffold() {
        Column(
            modifier = Modifier
            .padding(it)
            .fillMaxSize()) {

        }
    }
}
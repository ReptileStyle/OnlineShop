package com.example.onlineshop.ui.registration

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineshop.R
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.ui.components.GoogleOrAppleButton
import com.example.onlineshop.ui.components.OutlinedTextFieldNoContentPadding
import com.example.onlineshop.ui.components.RegistrationTextField
import com.example.onlineshop.ui.theme.Montserrat
import com.example.onlineshop.ui.theme.OnlineShopTheme
import com.google.android.gms.auth.api.identity.BeginSignInResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RegistrationScreen (
    viewModel: RegistrationViewModel = hiltViewModel(),
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


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        viewModel.processGoogleSignInResult(result, context)
    }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }
    LaunchedEffect(key1 = viewModel.state.oneTapSignInResponse){
        val response = viewModel.state.oneTapSignInResponse
        if(response != null){
            launch(response)
        }
    }
    Scaffold() {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 42.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(105.dp))
            Text(
                text = "Sign in",
                style = TextStyle(
                    fontFamily = Montserrat,
                    fontSize = 26.sp,
                    color = Color(0xff161826),
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(78.dp))
            RegistrationTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.firstName,
                onValueChange = {viewModel.onEvent(RegistrationEvent.OnFirstNameChange(it))},
                placeholder = "First name",
                isError = viewModel.state.isFirstNameError
            )
            Spacer(modifier = Modifier.height(35.dp))
            RegistrationTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.lastName,
                onValueChange = {viewModel.onEvent(RegistrationEvent.OnLastNameChange(it))},
                placeholder = "Last name",
                isError = viewModel.state.isLastNameError
            )
            Spacer(modifier = Modifier.height(35.dp))
            RegistrationTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.email,
                onValueChange = {viewModel.onEvent(RegistrationEvent.OnEmailChange(it))},
                placeholder = "Email",
                isError = viewModel.state.isEmailError
            )
            Spacer(modifier = Modifier.height(35.dp))
            Button(
                onClick = { viewModel.onEvent(RegistrationEvent.OnSignInClick)},
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xff4e55d7)
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
            ) {
                if(viewModel.state.isLoading)
                    CircularProgressIndicator (
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                else
                    Text(
                        text = "Sign in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xffeaeaea)
                    )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = "Already have an account?",
                    fontSize = 10.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xff808080)
                )
                Text(
                    text = "Log in",
                    fontSize = 10.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xff254FE6),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            viewModel.onEvent(RegistrationEvent.OnLogInClick)
                        }
                )
            }
            Spacer(modifier = Modifier.height(70.dp))
            GoogleOrAppleButton(
                modifier = Modifier
                    .padding(horizontal = 44.dp)
                    .fillMaxWidth(),
                text = "Sign in with Google",
                iconResource = R.drawable.google_black_logo,
                onClick = {viewModel.signInWithGoogle(context)},
                isLoading = viewModel.state.isGoogleLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            GoogleOrAppleButton(
                modifier = Modifier
                    .padding(horizontal = 44.dp)
                    .fillMaxWidth(),
                text = "Sign in with Apple",
                iconResource = R.drawable.apple_black_logo,
                onClick = {}
            )
        }
    }

}

@Preview
@Composable
fun RegistrationScreenPreview(){
    OnlineShopTheme {
        RegistrationScreen()
    }
}
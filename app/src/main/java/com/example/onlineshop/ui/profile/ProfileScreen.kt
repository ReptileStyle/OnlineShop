package com.example.onlineshop.ui.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.onlineshop.R
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.ui.components.BottomNavBar
import com.example.onlineshop.ui.components.ProfilePhotoContainer
import com.example.onlineshop.ui.components.ScaffoldWithBackButton
import com.example.onlineshop.ui.login.LoginViewModel
import com.example.onlineshop.ui.registration.RegistrationEvent
import com.example.onlineshop.ui.theme.Montserrat
import com.example.onlineshop.ui.theme.OnlineShopTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
    onNavigate: (route: String, popBackStack: Boolean) -> Unit = { _, _ -> },
    navController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> onNavigate(it.route, it.popBackStack)
                is UiEvent.NavigateUp -> onNavigateUp()
                is UiEvent.Message -> Toast.makeText(context,it.text,it.length).show()
                else -> {}
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onPhotoPicked(uri,context.contentResolver)
    }

    ProfileScreenUi(
        navController = navController,
        balance = 1593,
        isPhotoLoading = viewModel.state.isPhotoLoading,
        photoBitmap = viewModel.state.imageBitmap,
        profileName = viewModel.state.username,
        onLogOutClick = {viewModel.onEvent(ProfileEvent.OnLogOutClick)},
        onPhotoClick = {
            launcher.launch("image/*")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenUi(
    onPhotoClick: () -> Unit = {},
    onBackButtonClick: () -> Unit = {},
    isPhotoLoading:Boolean = false,
    photoBitmap: ImageBitmap? = null,
    profileName: String = "test username",
    balance: Int = 0,
    onLogOutClick: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    ScaffoldWithBackButton(
        bottomBar = {BottomNavBar(navController = navController)},
        onBackButtonClick = onBackButtonClick
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat
            )
            Spacer(modifier = Modifier.height(19.dp))
            ProfilePhotoContainer(
                modifier = Modifier.size(61.dp),
                imageBitmap = photoBitmap,
                isPhotoLoading = isPhotoLoading,
                onClick = onPhotoClick
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "change photo",
                color = Color(0xff808080),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Montserrat
            )
            Spacer(modifier = Modifier.height(17.dp))
            Text(
                text = profileName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat
            )
            Spacer(modifier = Modifier.height(36.dp))
            androidx.compose.material.Button(
                onClick = { },
                colors = androidx.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xff4e55d7)
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val (icon,text) = createRefs()
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.share),
                        contentDescription = null,
                        tint = Color(0xffeaeaea),
                        modifier = Modifier.constrainAs(icon){
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start,50.dp)
                        }
                    )
                    Text(
                        modifier = Modifier.constrainAs(text){
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                        text = "Upload item",
                        color = Color(0xffeaeaea),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Montserrat
                    )
                }

            }
            Spacer(modifier = Modifier.height(15.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.credit_card),
                trailingIcon = Icons.Filled.ChevronRight,
                text = "Trade store"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.credit_card),
                trailingIcon = Icons.Filled.ChevronRight,
                text = "Payment method"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.credit_card),
                balance = balance,
                text = "Balance"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.credit_card),
                trailingIcon = Icons.Filled.ChevronRight,
                text = "Trade history"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.refresh),
                trailingIcon = Icons.Filled.ChevronRight,
                text = "Restore purchase"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.help),
                text = "Help"
            )
            Spacer(modifier = Modifier.height(25.dp))
            profileButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                leadingIcon = ImageVector.vectorResource(id = R.drawable.log_in),
                onClick = onLogOutClick,
                text = "Log out"
            )
        }
    }
}

@Composable
private fun profileButton(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Outlined.CreditCard,
    trailingIcon: ImageVector? = null,
    text: String = "",
    onClick: () -> Unit = {}
) {
    ConstraintLayout(modifier.clickable { onClick() }) {
        val (leadingIconRef, trailingIconRef, textRef) = createRefs()
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = Color(0xff040402),
            modifier = Modifier
                .size(22.dp)
                .constrainAs(leadingIconRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(
            modifier = Modifier.constrainAs(textRef) {
                start.linkTo(leadingIconRef.end, 14.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            text = text,
        )
        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Color(0xff040402),
                modifier = Modifier
                    .size(22.dp)
                    .constrainAs(trailingIconRef) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
        }
    }
}

@Composable
private fun profileButton(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Outlined.CreditCard,
    balance: Int,
    text: String = "",
    onClick: () -> Unit = {}
) {
    ConstraintLayout(modifier.clickable { onClick() }) {
        val (leadingIconRef, trailingIconRef, textRef) = createRefs()
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = Color(0xff040402),
            modifier = Modifier
                .size(22.dp)
                .constrainAs(leadingIconRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(
            modifier = Modifier.constrainAs(textRef) {
                start.linkTo(leadingIconRef.end, 14.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            text = text,
        )
        Text(
            modifier = Modifier.constrainAs(trailingIconRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            text = "$ $balance"
        )

    }
}



@Preview
@Composable
private fun Preview() {
    OnlineShopTheme {
        ProfileScreenUi()
    }
}
package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.noAccountAnnotatedString
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.IconLabelButton
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.SignInTextField
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.SurfaceTextButton
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.LoginViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.*
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.get

@Composable
fun LoginScreen(
    navController: NavHostController,
    userState: DataState,
    lVm: LoginViewModel = get(),
    onLoginSuccess: () -> Unit
) {
    val ctx = LocalContext.current
    var pinSet by remember { mutableStateOf(false) }
    val sharedPreferences = ctx.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    LaunchedEffect(lVm.currentNavigationCommand) {
        when (lVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                println("LoginScreen NavigationOpenCommand: ${lVm.currentNavigationCommand}")
                onLoginSuccess()
                println("login success")
            }
        }
    }

    LaunchedEffect(userState) {
        if (userState is SuccessState<*>) {
            if ((userState.data as? GetUserResponse) != null) {

                if (lVm.currentNavigationCommand is NavigationOpenCommand) {
                    val destination = (lVm.currentNavigationCommand as NavigationOpenCommand).screen
                    lVm.resetAll {
                        navController clearBackStackAndNavigateTo destination
                    }
                }
            }
        }
    }

    LaunchedEffect(lVm.currentLoginState) {

        lVm.currentErrorMsg = when (lVm.currentLoginState) {
            is ErrorState -> (lVm.currentLoginState as ErrorState).throwable.message
            else -> null
        }
        if (lVm.currentErrorMsg?.isNotEmpty() == true){
            println("Error message : ${lVm.currentErrorMsg}")
        }

        if (lVm.currentLoginState is SuccessState<*>) {

            pinSet = SecurityUtils.isPinSet(ctx)
            println("Pin state : $pinSet and Login state : ${if (lVm.currentLoginState is SuccessState<*>) (lVm.currentLoginState as SuccessState<*>).data else lVm.currentLoginState}")

            sharedPreferences.edit().putBoolean("isAuthenticated", true).apply()

            if (pinSet) {
                println("Pin set, redirecting to main content")
                navController.navigate(HarvestRoutes.Screen.CHAT)
            } else {
                println("Pin not set, redirecting to pin creation")
                navController.navigate(HarvestRoutes.Screen.PIN_CREATE)
            }
        }

    }

    LaunchedEffect(lVm.currentLogoutState) {

        lVm.currentErrorMsg = when (lVm.currentLogoutState) {
            is ErrorState -> (lVm.currentLogoutState as ErrorState).throwable.message
            else -> null
        }
        if (lVm.currentErrorMsg?.isNotEmpty() == true){
            println("Error message : ${lVm.currentErrorMsg}")
        }

        println("Logout state : ${lVm.currentLogoutState}")
        if (lVm.currentLogoutState is SuccessState<*>) {
            SecurityUtils.clearPreferences(ctx)
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.onSecondary)
            .systemBarsPadding()
            .padding(top = 30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .size(68.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(MR.images.open.drawableResId),
                contentDescription = "Icon"
            )
            SignInTextField(
                value = lVm.currentWorkEmail,
                onValueChange = { updatedString -> lVm.currentWorkEmail = updatedString },
                placeholderText = stringResource(MR.strings.login_screen_email_et_placeholder.resourceId)
            )
            SignInTextField(
                value = lVm.currentPassword,
                onValueChange = { updatedString -> lVm.currentPassword = updatedString },
                placeholderText = stringResource(MR.strings.password_et_placeholder.resourceId),
                isPasswordTextField = true
            )
            SurfaceTextButton(
                text = stringResource(MR.strings.forgot_password.resourceId),
                fontWeight = FontWeight.Medium,
                onClick = { navController.navigate(HarvestRoutes.Screen.FORGOT_PASSWORD) }
            )
            IconLabelButton(
                label = stringResource(MR.strings.login_screen_signIn_btn_txt.resourceId),
                onClick = {
                    if (lVm.currentWorkEmail.isNotEmpty() && lVm.currentPassword.isNotEmpty()) {
                        lVm.login()
                    } else {
                        lVm.currentErrorMsg = "Please enter email and password"
                    }
                },
                isLoading = lVm.currentLoginState is LoadingState || userState is LoadingState,
                errorMsg = lVm.currentErrorMsg,
            )
            SurfaceTextButton(
                text = noAccountAnnotatedString(),
                onClick = { navController.navigate(HarvestRoutes.Screen.SIGNUP) }
            )
            SurfaceTextButton(
                text = MR.strings.view_tour.get(),
                fontWeight = FontWeight.Medium,
                onClick = {
                    navController clearBackStackAndNavigateTo HarvestRoutes.Screen.ON_BOARDING
                }
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun LoadingIndicator(
    loadingFlow: StateFlow<Boolean>
) {
    val loading = loadingFlow.collectAsState()
    AnimatedVisibility(
        visible = loading.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ProfilePicture(
    pictureString: String,
    displayName: String
) {
//    val imageBytes = Base64.decode(pictureString, 0)
//    val picture = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//    Image(
//        bitmap = picture.asImageBitmap(),
//        contentDescription = stringResource(id = MR.strings.choose_project.resourceId,
//            formatArgs = arrayOf(displayName)),
//        modifier = Modifier
//            .padding(horizontal = 2.dp, vertical = 2.dp)
//            .clip(CircleShape)
//            .requiredSize(PROFILE_PICTURE_SIZE.dp)
//    )
    println("Image url : $pictureString")
    AsyncImage(
        model = pictureString,
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(CircleShape)
            .requiredSize(PROFILE_PICTURE_SIZE.dp),
        contentDescription = "Translated description of what the image contains"
    )

    val painter = rememberAsyncImagePainter(model = pictureString)

    Image(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(CircleShape)
            .requiredSize(PROFILE_PICTURE_SIZE.dp),
        //Use painter in Image composable
        painter = painter,
        contentDescription = "Cat"
    )


}

@Composable
fun DefaultProfilePicture(displayName: String) {

    Image(
        modifier = Modifier
            .padding(5.dp)
            .size(PROFILE_PICTURE_SIZE.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        painter = painterResource(MR.images.stock2.drawableResId),
        contentDescription = displayName
    )

//    AsyncImage(
//        modifier = Modifier
//            .size(PROFILE_PICTURE_SIZE.dp)
//            .clip(CircleShape),
//        contentScale = ContentScale.Crop,
//        model = "https://xsgames.co/randomusers/avatar.php?g=male",
//        contentDescription = displayName
//    )
}

@Composable
fun DefaultGroupPicture(displayName: String) {
    Image(
        modifier = Modifier
            .size(PROFILE_PICTURE_SIZE.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        painter = painterResource(MR.images.open.drawableResId),
        contentDescription = "User picture"
    )
}
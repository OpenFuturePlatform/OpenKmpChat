package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.noAccountAnnotatedString
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.IconLabelButton
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.SignInTextField
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.SurfaceTextButton
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.LoginViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.ModalOpenCommand
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.DataState
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.ErrorState
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.LoadingState
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

    LaunchedEffect(lVm.currentNavigationCommand) {
        when (lVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                onLoginSuccess()
            }
        }
    }

    LaunchedEffect(userState) {
        if (userState is OpenDataModel.SuccessState<*>) {
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .systemBarsPadding()
            .padding(top = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                onClick = { lVm.login() },
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
        HarvestDialog(
            openCommand = lVm.currentNavigationCommand,
            onConfirm = {
                if (lVm.currentNavigationCommand is ModalOpenCommand) {
                    if ((lVm.currentNavigationCommand as ModalOpenCommand).title == "Work in Progress") {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://harvestkmp.web.app/")
                        }
                        ctx.startActivity(intent)
                    }
                }
                lVm.currentNavigationCommand = null
            },
            onDismiss = {
                lVm.currentNavigationCommand = null
            }
        )
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
        painter = painterResource(MR.images.stock1.drawableResId),
        contentDescription = "User picture"
    )
}
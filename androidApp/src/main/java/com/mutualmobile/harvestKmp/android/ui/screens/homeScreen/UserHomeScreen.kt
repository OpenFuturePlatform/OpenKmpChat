package com.mutualmobile.harvestKmp.android.ui.screens.homeScreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.pager.ExperimentalPagerApi
import com.mutualmobile.harvestKmp.android.ui.theme.PrimaryColor
import com.mutualmobile.harvestKmp.android.viewmodels.UserHomeViewModel
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import org.koin.androidx.compose.get

@Composable
fun UserHomeScreen(
    navController: NavHostController,
    uhVm: UserHomeViewModel = get(),
    user: GetUserResponse?
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState
    ) { bodyPadding ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bodyPadding),
            color = PrimaryColor
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    user?.firstName?.let {
                        Text(
                            text = "Hi ${it.uppercase()}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 2.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .statusBarsPadding()
                                .padding(top = 24.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}



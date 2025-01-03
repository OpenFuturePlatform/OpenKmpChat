package com.mutualmobile.harvestKmp.android.ui.screens.onboradingScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.noAccountAnnotatedString
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.IconLabelButton
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.components.SurfaceTextButton
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.domain.model.onBoardingItems

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreen(navController: NavHostController) {
    val scaffoldState = rememberScaffoldState()

    val pagerState = rememberPagerState(
        initialPage = 0,
    )

    val color by animateColorAsState(
        targetValue = Color(
            onBoardingItems[pagerState.currentPage].color
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
    ) { bodyPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bodyPadding),
            color = color
        ) {
            LaunchedEffect(key1 = Unit) {
                pagerState.animateScrollToPage(page = pagerState.currentPage)
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(MR.strings.app_name.resourceId).uppercase(),
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

                    ScrollingText(
                        pagerState = pagerState
                    )
                }

                Column {
//                    HorizontalPager(
//                        state = pagerState,
//                        count = onBoardingItems.size,
//                        verticalAlignment = Alignment.Bottom,
//                    ) { page ->
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .fillMaxHeight(0.425f),
//                            verticalArrangement = Arrangement.Bottom
//                        ) {
//                            Image(
//                                painter = painterResource(id = onBoardingItems[page].image.drawableResId),
//                                contentDescription = null,
//                                modifier = Modifier.fillMaxSize()
//                            )
//                        }
//                    }

                    Box(
                        modifier = Modifier.navigationBarsPadding(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            HorizontalPagerIndicator(
//                                pagerState = pagerState,
//                                activeColor = Color.White
//                            )

                            IconLabelButton(
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .padding(top = 16.dp),
                                label = stringResource(MR.strings.login_screen_signIn_btn_txt.resourceId),
                                onClick = { navController.navigate(HarvestRoutes.Screen.LOGIN) }
                            )

                            SurfaceTextButton(
                                text = noAccountAnnotatedString(),
                                onClick = { navController.navigate(HarvestRoutes.Screen.NEW_ORG_SIGNUP) }
                            )

                            Spacer(modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun ScrollingText(pagerState: PagerState) {
    val textPagerState = rememberPagerState()
    VerticalPager(
        count = onBoardingItems.size,
        state = textPagerState,
        modifier = Modifier.height(50.dp),
        userScrollEnabled = false
    ) { index ->
        Text(
            text = onBoardingItems[index].title.get(),
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        textPagerState.animateScrollToPage(pagerState.currentPage)
    }
}

@Preview
@Composable
fun OnBoardingScreenPreview() {
    OpenChatTheme {
        OnBoardingScreen(navController = rememberNavController())
    }
}
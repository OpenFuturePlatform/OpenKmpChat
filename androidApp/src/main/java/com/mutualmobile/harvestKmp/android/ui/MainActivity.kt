package com.mutualmobile.harvestKmp.android.ui

//noinspection SuspiciousImport
import android.R
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.navigation.NavigationItem
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.ChatGptScreen
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.ChatRoomScreen
import com.mutualmobile.harvestKmp.android.ui.screens.findWorkspaceScreen.FindWorkspaceScreen
import com.mutualmobile.harvestKmp.android.ui.screens.homeScreen.UserHomeScreen
import com.mutualmobile.harvestKmp.android.ui.screens.landingScreen.LandingScreen
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoginScreen
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.NewEntryScreen
import com.mutualmobile.harvestKmp.android.ui.screens.onboradingScreen.OnBoardingScreen
import com.mutualmobile.harvestKmp.android.ui.screens.password.ChangePasswordScreen
import com.mutualmobile.harvestKmp.android.ui.screens.password.ForgotPasswordScreen
import com.mutualmobile.harvestKmp.android.ui.screens.projectScreen.ProjectScreen
import com.mutualmobile.harvestKmp.android.ui.screens.settingsScreen.SettingsScreen
import com.mutualmobile.harvestKmp.android.ui.screens.signUpScreen.NewOrgSignUpScreen
import com.mutualmobile.harvestKmp.android.ui.screens.signUpScreen.SignUpScreen
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.SetupSystemUiController
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.EmptyState
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.LoadingState
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    val mainActivityViewModel: MainActivityViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            OpenChatTheme {
                SetupSystemUiController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val startDestination = remember {
                        if (mainActivityViewModel.doesLocalUserExist) {
                            HarvestRoutes.Screen.CHAT
                        } else {
                            HarvestRoutes.Screen.ON_BOARDING
                        }
                    }

                    val navController = rememberNavController()

                    val bottomNavigationItems = listOf(
                        //NavigationItem.Home,
                        NavigationItem.Chat,
                        NavigationItem.ChatGPT,
                        NavigationItem.Project,
                        NavigationItem.Settings
                    )

                    Scaffold(
                        bottomBar = {
                            OpenChatAppBottomNavigation(navController = navController,modifier = Modifier, bottomNavigationItems)
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(it)
                        ) {
                            composable(HarvestRoutes.Screen.ON_BOARDING) {
                                OnBoardingScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.SIGNUP) {
                                SignUpScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.NEW_ORG_SIGNUP) {
                                NewOrgSignUpScreen(navController = navController)
                            }
                            composable(
                                HarvestRoutes.Screen.LOGIN_WITH_ORG_ID_IDENTIFIER,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.orgId) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.orgIdentifier) { nullable = true },
                                ),
                            ) {
                                LoginScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    onLoginSuccess = {
                                        mainActivityViewModel.fetchUser()
                                    },
                                )
                            }
                            composable(HarvestRoutes.Screen.ORG_USER_DASHBOARD) {
                                LandingScreen(
                                    navController = navController,
                                    userOrganization = mainActivityViewModel.userOrganization,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.FIND_WORKSPACE) {
                                FindWorkspaceScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.ORG_PROJECTS) {
                                ProjectScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.WORK_ENTRY) {
                                NewEntryScreen(navController = navController, user = mainActivityViewModel.user)
                            }
                            composable(HarvestRoutes.Screen.SETTINGS) {
                                SettingsScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.FORGOT_PASSWORD) {
                                ForgotPasswordScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.CHANGE_PASSWORD) {
                                ChangePasswordScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.SETTINGS) {
                                SettingsScreen(navController = navController)
                            }
                            composable(HarvestRoutes.Screen.USER_HOME) {
                                UserHomeScreen(
                                    navController = navController,
                                    user = mainActivityViewModel.user
                                )
                            }
                            composable(HarvestRoutes.Screen.CHAT) {
                                ChatRoomScreen(
                                    navController = navController,
                                    user = mainActivityViewModel.user,
                                    modifier = Modifier
                                )
                            }
                            composable(HarvestRoutes.Screen.CHAT_GPT) {
                                ChatGptScreen(
                                    navController = navController,
                                    user = mainActivityViewModel.user
                                )
                            }
                        }
                    }

                }
            }
        }
        removeSplashScreen()
    }

    private fun removeSplashScreen() {
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (mainActivityViewModel.getUserState !is EmptyState && mainActivityViewModel.getUserState !is LoadingState) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )
    }

    private fun setupSplashScreen() {
        installSplashScreen().setOnExitAnimationListener { splashScreenView ->
            val fadeOut = ObjectAnimator.ofFloat(
                splashScreenView.view,
                View.ALPHA,
                1f,
                0f
            )
            with(fadeOut) {
                interpolator = LinearInterpolator()
                duration = 200L
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }
    }

    @Composable
    fun OpenChatAppTopBar(
        onProfileClicked: () -> Unit,
        onSearchClicked: () -> Unit,
        displayName: String
    ) {
        TopAppBar(
            title = {
                Text(
                    stringResource(
                        id = MR.strings.app_name.resourceId,
                        formatArgs = arrayOf(displayName)
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = onProfileClicked) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(id = MR.strings.choose_project.resourceId)
                    )
                }
            },
            actions = {
                IconButton(onClick = onSearchClicked) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = MR.strings.choose_project.resourceId)
                    )
                }
            }
        )
    }
    @Composable
    fun OpenChatAppBottomNavigation(
        navController: NavHostController,
        modifier: Modifier = Modifier,
        items: List<NavigationItem>
    ) {
        val currentRoute = currentRoute(navController)
        println("CURRENT ROUTE $currentRoute")
        val visibility =
            currentRoute in listOf(
                HarvestRoutes.Screen.ON_BOARDING,
                HarvestRoutes.Screen.SIGNUP,
                HarvestRoutes.Screen.LOGIN,
                HarvestRoutes.Screen.LOGIN_WITH_ORG_ID_IDENTIFIER
            ) || currentRoute == null
        if (!visibility) {
            BottomNavigation(
                elevation = 5.dp,
                modifier = modifier.navigationBarsPadding()
            ) {
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                modifier = Modifier,
                                tint = Color.White,
                                contentDescription = ""
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        alwaysShowLabel = false, // This hides the title for the unselected items
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun currentRoute(navController: NavHostController): String? {
        return navController.currentBackStackEntryAsState().value?.destination?.route
    }
}
package com.mutualmobile.harvestKmp.android.ui

//noinspection SuspiciousImport
import android.Manifest
import android.R
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.navDeepLink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.navigation.NavigationItem
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.*
import com.mutualmobile.harvestKmp.android.ui.screens.homeScreen.UserHomeScreen
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoginScreen
import com.mutualmobile.harvestKmp.android.ui.screens.onboradingScreen.OnBoardingScreen
import com.mutualmobile.harvestKmp.android.ui.screens.password.ChangePasswordScreen
import com.mutualmobile.harvestKmp.android.ui.screens.password.ForgotPasswordScreen
import com.mutualmobile.harvestKmp.android.ui.screens.taskScreen.TaskScreen
import com.mutualmobile.harvestKmp.android.ui.screens.settingsScreen.SettingsScreen
import com.mutualmobile.harvestKmp.android.ui.screens.signUpScreen.NewOrgSignUpScreen
import com.mutualmobile.harvestKmp.android.ui.screens.signUpScreen.SignUpScreen
import com.mutualmobile.harvestKmp.android.ui.screens.userScreen.ContactProfileScreen
import com.mutualmobile.harvestKmp.android.ui.screens.userScreen.GroupProfileScreen
import com.mutualmobile.harvestKmp.android.ui.screens.userScreen.UserListScreen
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.WalletDetailScreen
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.WalletReceiverDetailScreen
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.WalletScreen
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.WalletSenderDetailScreen
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.SetupSystemUiController
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.EmptyState
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.LoadingState
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    val mainActivityViewModel: MainActivityViewModel = get()
    private lateinit var navController: NavHostController

    init {
        System.loadLibrary("TrustWalletCore")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle deep link intent
        intent?.data?.let { uri ->
            println("Deep link url : $uri")
            navController.handleDeepLink(intent)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            OpenChatTheme {
                SetupSystemUiController()
                val context = LocalContext.current
                val permissionState = rememberPermissionState(
                    permission = Manifest.permission.POST_NOTIFICATIONS
                )
                val permissionOpenDialog = remember { mutableStateOf(false) }
                val rationalPermissionOpenDialog = remember { mutableStateOf(false) }

                if (permissionOpenDialog.value) {
                    ShowSettingDialog(openDialog = permissionOpenDialog)
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        if (!isGranted) {
                            println("PERMISSION NOT GRANTED")
                            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                                rationalPermissionOpenDialog.value = true
                            } else {
                                permissionOpenDialog.value = true
                            }
                        } else {
                            println("PERMISSION GRANTED")
                        }
                    }
                )
                if (rationalPermissionOpenDialog.value) {
                    ShowRationalPermissionDialog(openDialog = rationalPermissionOpenDialog) {
                        rationalPermissionOpenDialog.value = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

//                LaunchedEffect(Unit) {
//                    permissionState.launchPermissionRequest()
//                }


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

                    navController = rememberNavController()

                    val bottomNavigationItems = listOf(
                        //NavigationItem.Home,
                        NavigationItem.Chat,
                        NavigationItem.ChatGPT,
                        NavigationItem.Contacts,
                        NavigationItem.Task,
                        NavigationItem.Settings
                    )

                    Scaffold(
                        bottomBar = {
                            OpenChatAppBottomNavigation(
                                navController = navController,
                                modifier = Modifier,
                                bottomNavigationItems
                            )
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
                                UserHomeScreen(navController = navController, user = mainActivityViewModel.user)
                            }
                            composable(HarvestRoutes.Screen.ORG_USERS) {
                                UserListScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.CHAT) {
                                ChatRoomScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.CHAT_GPT) {
                                ChatGptScreen(
                                    navController = navController,
                                    user = mainActivityViewModel.user,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.ADD_ACTION) {
                                AddScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }
                            composable(HarvestRoutes.Screen.ADD_GROUP) {
                                AddToGroupScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.WALLET_DETAIL_WITH_ADDRESS,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.address) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.blockchainType) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.privateKey) { nullable = true },
                                ),
                            ) {
                                WalletDetailScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    address = it.arguments?.getString("address"),
                                    privateKey = it.arguments?.getString("privateKey"),
                                    blockchainType = it.arguments?.getString("blockchainType"),
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.ADD_MEMBER_WITH_GROUP_ID,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.groupId) { nullable = false },
                                ),
                            ) {
                                AddMemberToGroupScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    groupId = it.arguments?.getString("groupId")
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.CONTACT_PROFILE_WITH_ID,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.profileId) { nullable = true },
                                ),
                            ) {
                                ContactProfileScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    profileId = it.arguments?.getString("profileId")
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.GROUP_PROFILE_WITH_ID,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.profileId) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.isGroup) { nullable = true },
                                ),
                            ) {
                                GroupProfileScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    profileId = it.arguments?.getString("profileId"),
                                    isGroup = it.arguments?.getString("isGroup")
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.CHAT_PRIVATE_WITH_SENDER_RECEIVER,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.recipient) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.sender) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.chatUid) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.isGroup) { nullable = true },
                                ),
                            ) {
                                ChatPrivateScreen(
                                    navController = navController,
                                    user = mainActivityViewModel.user,
                                    userState = mainActivityViewModel.getUserState,
                                    recipient = it.arguments?.getString("recipient"),
                                    sender = it.arguments?.getString("sender"),
                                    chatUid = it.arguments?.getString("chatUid"),
                                    isGroup = it.arguments?.getString("isGroup")
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.GROUP_WITH_PARTICIPANTS,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.participants) { nullable = true },
                                ),
                            ) {
                                CreateGroupScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    _participants = it.arguments?.getString("participants")
                                )
                            }

                            composable(HarvestRoutes.Screen.TASK) {
                                TaskScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.USER_WALLETS,
                                deepLinks = listOf(navDeepLink {
                                    uriPattern = "https://openaix.io/wallets"
                                    action = Intent.ACTION_VIEW
                                }),
                            ) {
                                WalletScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.WALLET_SENDER_DETAIL_WITH_ADDRESS,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.address) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.blockchainType) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.privateKey) { nullable = true },
                                ),
                            ) {
                                WalletSenderDetailScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    address = it.arguments?.getString("address")!!,
                                    privateKey = it.arguments?.getString("privateKey")!!,
                                    blockchainType = it.arguments?.getString("blockchainType")!!,
                                )
                            }

                            composable(
                                HarvestRoutes.Screen.WALLET_RECEIVER_DETAIL_WITH_ADDRESS,
                                arguments = listOf(
                                    navArgument(HarvestRoutes.Keys.address) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.blockchainType) { nullable = true },
                                    navArgument(HarvestRoutes.Keys.privateKey) { nullable = true },
                                ),
                            ) {
                                WalletReceiverDetailScreen(
                                    navController = navController,
                                    userState = mainActivityViewModel.getUserState,
                                    address = it.arguments?.getString("address")!!,
                                    privateKey = it.arguments?.getString("privateKey")!!,
                                    blockchainType = it.arguments?.getString("blockchainType")!!,
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

    @Composable
    fun ShowSettingDialog(openDialog: MutableState<Boolean>) {
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Notification Permission")
                },
                text = {
                    Text("Notification permission is required, Please allow notification permission from setting")
                },

                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        TextButton(
                            onClick = {
                                openDialog.value = false

                                startActivity(intent)
                            },
                        ) {
                            Text("Ok")
                        }
                    }

                },
            )
        }
    }

    @Composable
    fun ShowRationalPermissionDialog(openDialog: MutableState<Boolean>, onclick: () -> Unit) {
        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Alert")
                },
                text = {
                    Text("Notification permission is required, to show notification")
                },

                buttons = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        TextButton(
                            onClick = onclick,
                        ) {
                            Text("Ok")
                        }
                    }

                },
            )
        }
    }
}
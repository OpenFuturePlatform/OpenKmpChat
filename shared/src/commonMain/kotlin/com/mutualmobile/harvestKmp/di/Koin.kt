package com.mutualmobile.harvestKmp.di

import com.mutualmobile.harvestKmp.data.local.*
import com.mutualmobile.harvestKmp.data.local.impl.*
import com.mutualmobile.harvestKmp.data.network.Constants
import com.mutualmobile.harvestKmp.data.network.Endpoint
import com.mutualmobile.harvestKmp.data.network.Endpoint.REFRESH_TOKEN
import com.mutualmobile.harvestKmp.data.network.attachment.AttachmentApi
import com.mutualmobile.harvestKmp.data.network.attachment.impl.AttachmentApiImpl
import com.mutualmobile.harvestKmp.data.network.authUser.AuthApi
import com.mutualmobile.harvestKmp.data.network.authUser.UserForgotPasswordApi
import com.mutualmobile.harvestKmp.data.network.authUser.impl.AuthApiImpl
import com.mutualmobile.harvestKmp.data.network.authUser.impl.UserForgotPasswordApiImpl
import com.mutualmobile.harvestKmp.data.network.chat.*
import com.mutualmobile.harvestKmp.data.network.chat.impl.*
import com.mutualmobile.harvestKmp.data.network.org.OrgApi
import com.mutualmobile.harvestKmp.data.network.org.OrgProjectsApi
import com.mutualmobile.harvestKmp.data.network.org.OrgUsersApi
import com.mutualmobile.harvestKmp.data.network.org.UserProjectApi
import com.mutualmobile.harvestKmp.data.network.org.UserWorkApi
import com.mutualmobile.harvestKmp.data.network.org.impl.OrgApiImpl
import com.mutualmobile.harvestKmp.data.network.org.impl.OrgProjectsApiImpl
import com.mutualmobile.harvestKmp.data.network.org.impl.OrgUsersApiImpl
import com.mutualmobile.harvestKmp.data.network.org.impl.UserProjectApiImpl
import com.mutualmobile.harvestKmp.data.network.org.impl.UserWorkApiImpl
import com.mutualmobile.harvestKmp.data.network.state.StateApi
import com.mutualmobile.harvestKmp.data.network.state.impl.StateApiImpl
import com.mutualmobile.harvestKmp.data.network.wallet.WalletApi
import com.mutualmobile.harvestKmp.data.network.wallet.impl.WalletApiImpl
import com.mutualmobile.harvestKmp.domain.model.response.LoginResponse
import com.mutualmobile.harvestKmp.domain.usecases.CurrentUserLoggedInUseCase
import com.mutualmobile.harvestKmp.domain.usecases.SaveSettingsUseCase
import com.mutualmobile.harvestKmp.domain.usecases.aiApiUseCases.*
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.ChangePasswordUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.ExistingOrgSignUpUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.FcmTokenUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.GetUserUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.LoginUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.LogoutUseCase
import com.mutualmobile.harvestKmp.domain.usecases.authApiUseCases.NewOrgSignUpUseCase
import com.mutualmobile.harvestKmp.domain.usecases.chatApiUseCases.*
import com.mutualmobile.harvestKmp.domain.usecases.groupApiUseCases.*
import com.mutualmobile.harvestKmp.domain.usecases.orgApiUseCases.FindOrgByIdUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgApiUseCases.FindOrgByIdentifierUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.CreateProjectUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.DeleteProjectUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.FindProjectsInOrgUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.GetListOfUsersForAProjectUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.GetProjectsFromIdsUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgProjectsUseCases.UpdateProjectUseCase
import com.mutualmobile.harvestKmp.domain.usecases.orgUsersApiUseCases.FindUsersInOrgUseCase
import com.mutualmobile.harvestKmp.domain.usecases.stateApiUseCases.*
import com.mutualmobile.harvestKmp.domain.usecases.userForgotPasswordApiUseCases.ForgotPasswordUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userForgotPasswordApiUseCases.ResetPasswordUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userProjectUseCases.AssignProjectsToUsersUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userProjectUseCases.DeleteWorkTimeUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userProjectUseCases.GetUserAssignedProjectsUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userProjectUseCases.LogWorkTimeUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userTaskUseCases.GetUserTasksUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userTaskUseCases.SaveUserTasksUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases.DecryptWalletUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases.GenerateWalletUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases.GetWalletUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userWalletUseCases.SaveWalletUseCase
import com.mutualmobile.harvestKmp.domain.usecases.userWorkUseCases.GetWorkLogsForDateRangeUseCase
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initSharedDependencies() = startKoin {
    modules(
        commonModule,
        networkModule,
        localDBRepos,
        useCaseModule,
        authApiUseCaseModule,
        orgApiUseCaseModule,
        orgProjectsUseCaseModule,
        orgUsersApiUseCaseModule,
        forgotPasswordApiUseCaseModule,
        userProjectUseCaseModule,
        userWorkUseCaseModule,
        chatApiUseCaseModule,
        groupApiUseCaseModule,
        userTaskUseCaseModule,
        userWalletUseCaseModule,
        stateUseCaseModule,
        platformModule()
    )
}

fun initSqlDelightExperimentalDependencies() = startKoin {
    modules(
        commonModule,
        networkModule,
        jsSqliteDeps,
        useCaseModule,
        authApiUseCaseModule,
        orgApiUseCaseModule,
        orgProjectsUseCaseModule,
        orgUsersApiUseCaseModule,
        forgotPasswordApiUseCaseModule,
        userProjectUseCaseModule,
        userWorkUseCaseModule,
        chatApiUseCaseModule,
        groupApiUseCaseModule,
        userTaskUseCaseModule,
        userWalletUseCaseModule,
        stateUseCaseModule,
        platformModule()
    )
}

val jsSqliteDeps = module {
    single<HarvestUserLocal> { HarvestUserLocalImpl() }
    single<WalletLocal> { WalletLocalImpl() }
}

val localDBRepos = module {
    single<HarvestUserLocal> { HarvestUserLocalImpl(get()) }
    single<ChatLocal> { ChatLocalImpl(get()) }
    single<AttachmentLocal> { AttachmentLocalImpl(get()) }
    single<WalletLocal> { WalletLocalImpl(get()) }
    single<TokenLocal> { TokenLocalImpl(get()) }
}

val networkModule = module {
    single {
        httpClient(get(), get(), get())
    }
    single<RealtimeMessagingClient> { KtorRealtimeMessagingClient(get()) }
}

val commonModule = module {
    single<AuthApi> { AuthApiImpl(get()) }
    single<UserForgotPasswordApi> { UserForgotPasswordApiImpl(get()) }
    single<OrgApi> { OrgApiImpl(get()) }
    single<OrgProjectsApi> { OrgProjectsApiImpl(get()) }
    single<OrgUsersApi> { OrgUsersApiImpl(get()) }
    single<UserProjectApi> { UserProjectApiImpl(get()) }
    single<UserWorkApi> { UserWorkApiImpl(get()) }
    single<ChatApi> { ChatApiImpl(get()) }
    single<GroupApi> { GroupApiImpl(get()) }
    single<UserApi> { UserApiImpl(get()) }
    single<AttachmentApi> { AttachmentApiImpl(get()) }
    single<TaskApi> { TaskApiImpl(get()) }
    single<WalletApi> { WalletApiImpl(get()) }
    single { Settings() }
    single<StateApi> { StateApiImpl(get()) }
}

val useCaseModule = module {
    single { SaveSettingsUseCase(get()) }
    single { CurrentUserLoggedInUseCase(get()) }
}

val authApiUseCaseModule = module {
    single { ChangePasswordUseCase(get()) }
    single { ExistingOrgSignUpUseCase(get()) }
    single { FcmTokenUseCase(get()) }
    single { GetUserUseCase(get()) }
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get(), get(), get(), get(), get()) }
    single { NewOrgSignUpUseCase(get()) }
}

val chatApiUseCaseModule = module {
    single { GetOwnMessagesUseCase(get()) }
    single { CreateMessagesUseCase(get()) }
    single { GetGroupMessagesUseCase(get()) }
    single { GetPrivateMessagesUseCase(get()) }
    single { CreateGroupUseCase(get()) }
    single { GetMessagesByUidUseCase(get()) }
    single { CreateGroupMessagesUseCase(get()) }
    single { GetGroupUseCase(get()) }
    single { UploadAttachmentUseCase(get()) }
    single { DownloadAttachmentUseCase(get()) }
    single { CreateAiMessagesUseCase(get()) }
    single { CreateAssistantNotesUseCase(get()) }
    single { CreateAssistantRemindersUseCase(get()) }
    single { CreateAssistantToDosUseCase(get()) }
    single { GetAssistantNotesUseCase(get()) }
    single { GetAssistantRemindersUseCase(get()) }
    single { GetAssistantToDosUseCase(get()) }
}

val groupApiUseCaseModule = module {
    single { CreateGroupUseCase(get()) }
    single { GetGroupUseCase(get()) }
    single { AddMemberUseCase(get()) }
    single { RemoveMemberUseCase(get()) }
}

val orgApiUseCaseModule = module {
    single { FindOrgByIdentifierUseCase(get()) }
    single { FindOrgByIdUseCase(get()) }
}

val orgProjectsUseCaseModule = module {
    single { CreateProjectUseCase(get()) }
    single { DeleteProjectUseCase(get()) }
    single { FindProjectsInOrgUseCase(get()) }
    single { GetListOfUsersForAProjectUseCase(get()) }
    single { UpdateProjectUseCase(get()) }
    single { GetProjectsFromIdsUseCase(get()) }
}

val orgUsersApiUseCaseModule = module {
    single { FindUsersInOrgUseCase(get()) }
    single { GetAllContactsUseCase(get()) }
    single { GetUserDetailUseCase(get()) }
}

val forgotPasswordApiUseCaseModule = module {
    single { ForgotPasswordUseCase(get()) }
    single { ResetPasswordUseCase(get()) }
}

val userProjectUseCaseModule = module {
    single { AssignProjectsToUsersUseCase(get()) }
    single { DeleteWorkTimeUseCase(get()) }
    single { GetUserAssignedProjectsUseCase(get()) }
    single { LogWorkTimeUseCase(get()) }
}

val userWorkUseCaseModule = module {
    single { GetWorkLogsForDateRangeUseCase(get()) }
}

val userTaskUseCaseModule = module {
    single { SaveUserTasksUseCase(get()) }
    single { GetUserTasksUseCase(get()) }
}

val userWalletUseCaseModule = module {
    single { GenerateWalletUseCase(get()) }
    single { DecryptWalletUseCase(get()) }
    single { GetWalletUseCase(get()) }
    single { SaveWalletUseCase(get()) }
}

val stateUseCaseModule = module {
    single { GetRatesUseCase(get()) }
    single { GetRateUseCase(get()) }
    single { GetBalanceUseCase(get()) }
    single { GetTransactionsUseCase(get()) }
    single { GetContractsUseCase(get()) }
    single { GetGasLimitUseCase(get()) }
    single { GetGasPriceUseCase(get()) }
    single { GetNonceUseCase(get()) }
    single { PostBroadcastUseCase(get()) }
}

class SharedComponent : KoinComponent {
    fun provideHarvestUserLocal(): HarvestUserLocal = get()
    fun provideChatLocal(): ChatLocal = get()
    fun provideAttachmentLocal(): AttachmentLocal = get()
    fun provideSettings(): Settings = get()
    fun provideWalletLocal(): WalletLocal = get()
    fun provideTokenLocal(): TokenLocal = get()
}

class UseCasesComponent : KoinComponent {
    fun provideSaveSettingsUseCase(): SaveSettingsUseCase = get()
    fun providerUserLoggedInUseCase(): CurrentUserLoggedInUseCase = get()
}

class AuthApiUseCaseComponent : KoinComponent {
    fun provideChangePasswordUseCase(): ChangePasswordUseCase = get()
    fun provideExistingOrgSignUpUseCase(): ExistingOrgSignUpUseCase = get()
    fun provideFcmTokenUseCase(): FcmTokenUseCase = get()
    fun provideGetNetworkUserUseCase(): GetUserUseCase = get()
    fun provideLoginUseCase(): LoginUseCase = get()
    fun provideLogoutUseCase(): LogoutUseCase = get()
    fun provideNewOrgSignUpUseCase(): NewOrgSignUpUseCase = get()
}

class OrgApiUseCaseComponent : KoinComponent {
    fun provideFindOrgByIdentifier(): FindOrgByIdentifierUseCase = get()
    fun provideFindOrgById(): FindOrgByIdUseCase = get()
}

class OrgProjectsUseCaseComponent : KoinComponent {
    fun provideCreateProjectUseCase(): CreateProjectUseCase = get()
    fun provideUpdateProjectUseCase(): UpdateProjectUseCase = get()
    fun provideDeleteProjectUseCase(): DeleteProjectUseCase = get()
    fun provideFindProjectsInOrgUseCase(): FindProjectsInOrgUseCase = get()
    fun provideGetListOfUsersForAProjectUseCase(): GetListOfUsersForAProjectUseCase = get()
    fun provideGetProjectsFromIdsUseCase(): GetProjectsFromIdsUseCase = get()
}

class OrgUsersApiUseCaseComponent : KoinComponent {
    fun provideFindUsersInOrgUseCase(): FindUsersInOrgUseCase = get()
}

class ForgotPasswordApiUseCaseComponent : KoinComponent {
    fun provideForgotPasswordUseCase(): ForgotPasswordUseCase = get()
    fun provideResetPasswordUseCase(): ResetPasswordUseCase = get()
}

class UserProjectUseCaseComponent : KoinComponent {
    fun provideAssignProjectsToUsersUseCase(): AssignProjectsToUsersUseCase = get()
    fun provideLogWorkTimeUseCase(): LogWorkTimeUseCase = get()
    fun provideDeleteWorkTimeUseCase(): DeleteWorkTimeUseCase = get()
    fun provideGetUserAssignedProjectsUseCase(): GetUserAssignedProjectsUseCase = get()
}

class UserTaskUseCaseComponent : KoinComponent {
    fun provideSaveUserTasksUseCase(): SaveUserTasksUseCase = get()
    fun provideGetUserTasksUseCase(): GetUserTasksUseCase = get()
}

class StateUseCaseComponent : KoinComponent {
    fun provideGetRatesUseCase(): GetRatesUseCase = get()
    fun provideGetRateUseCase(): GetRateUseCase = get()
    fun provideGetBalanceUseCase(): GetBalanceUseCase = get()
    fun provideGetTransactionsUseCase(): GetTransactionsUseCase = get()
    fun provideGetContractsUseCase(): GetContractsUseCase = get()
    fun provideGetGasPriceUseCase(): GetGasPriceUseCase = get()
    fun provideGetGasLimitUseCase(): GetGasLimitUseCase = get()
    fun provideGetNonceUseCase(): GetNonceUseCase = get()
    fun provideBroadcastUseCase(): PostBroadcastUseCase = get()
}

class UserWalletUseCaseComponent : KoinComponent {
    fun provideGenerateWalletUseCase(): GenerateWalletUseCase = get()
    fun provideDecryptWalletUseCase(): DecryptWalletUseCase = get()
    fun provideGetWalletUseCase(): GetWalletUseCase = get()
    fun provideSaveWalletUseCase(): SaveWalletUseCase = get()
}

class UserWorkUseCaseComponent : KoinComponent {
    fun provideGetWorkLogsForDateRangeUseCase(): GetWorkLogsForDateRangeUseCase = get()
}

class ChatApiUseCaseComponent : KoinComponent {
    fun provideGetMessagesByRecipient(): GetOwnMessagesUseCase = get()
    fun provideGetMessagesByUid(): GetMessagesByUidUseCase = get()
    fun provideCreateMessages(): CreateMessagesUseCase = get()
    fun provideCreateGroupMessages(): CreateGroupMessagesUseCase = get()
    fun provideGroupMessagesByRecipient(): GetGroupMessagesUseCase = get()
    fun providePrivateMessagesByRecipient(): GetPrivateMessagesUseCase = get()
    fun provideCreateAiMessages(): CreateAiMessagesUseCase = get()

    //todo - move to another module
    fun provideUploadAttachment(): UploadAttachmentUseCase = get()
    fun provideDownloadAttachment(): DownloadAttachmentUseCase = get()
    fun provideCreateAssistantNotes(): CreateAssistantNotesUseCase = get()
    fun provideCreateAssistantReminders(): CreateAssistantRemindersUseCase = get()
    fun provideCreateAssistantTodos(): CreateAssistantToDosUseCase = get()
    fun provideGetAssistantNotes(): GetAssistantNotesUseCase = get()
    fun provideGetAssistantReminders(): GetAssistantRemindersUseCase = get()
    fun provideGetAssistantToDos(): GetAssistantToDosUseCase = get()
}

class GroupApiUseCaseComponent : KoinComponent {
    fun provideCreateGroup(): CreateGroupUseCase = get()
    fun provideGetGroup(): GetGroupUseCase = get()
    fun provideAddMemberGroup(): AddMemberUseCase = get()
    fun provideRemoveMemberGroup(): RemoveMemberUseCase = get()
}

class UserApiUseCaseComponent : KoinComponent {
    fun provideGetAllContacts(): GetAllContactsUseCase = get()
    fun provideGetUserDetail(): GetUserDetailUseCase = get()
}

fun httpClient(
    httpClientEngine: HttpClientEngine,
    settings: Settings,
    saveSettingsUseCase: SaveSettingsUseCase
) =
    HttpClient(httpClientEngine) {

        install(ContentNegotiation) {
            json(Json {
                isLenient = true; ignoreUnknownKeys = true; prettyPrint = true
            })
        }
        install(Auth) {
            this.bearer {
                sendWithoutRequest { request -> !request.url.encodedPath.startsWith("/public") }
                this.loadTokens {
                    BearerTokens(
                        settings.getString(Constants.JWT_TOKEN, ""),
                        settings.getString(Constants.REFRESH_TOKEN, "")
                    )
                }
                this.refreshTokens {
                    refreshToken(settings, saveSettingsUseCase)
                }
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        //install(WebSockets)
        install(HttpTimeout) {
            connectTimeoutMillis = 30000
            requestTimeoutMillis = 30000
        }
    }

private suspend fun RefreshTokensParams.refreshToken(
    settings: Settings,
    saveSettingsUseCase: SaveSettingsUseCase
): BearerTokens {
    try {
        val oldRefreshToken = settings.getString(Constants.REFRESH_TOKEN, "")
        val refreshTokensResponse =
            this.client.post("${Endpoint.SPRING_BOOT_BASE_URL}$REFRESH_TOKEN") {
                contentType(ContentType.Application.Json)
                markAsRefreshTokenRequest()
                setBody(LoginResponse(refreshToken = oldRefreshToken))
            }
        if (refreshTokensResponse.body<String>().isNotEmpty()) {
            val refreshTokens = refreshTokensResponse.body<LoginResponse>()
            saveSettingsUseCase.invoke(
                refreshTokens.token,
                refreshTokens.refreshToken
            )
            BearerTokens(
                settings.getString(Constants.JWT_TOKEN, ""),
                settings.getString(Constants.REFRESH_TOKEN, "")
            )
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return BearerTokens(
        "",
        ""
    )
}

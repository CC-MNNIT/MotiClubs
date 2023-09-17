package com.mnnit.moticlubs.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.mnnit.moticlubs.data.datasource.LocalDatabase
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.repository.RepositoryImpl
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.usecase.ChannelUseCases
import com.mnnit.moticlubs.domain.usecase.ClubUseCases
import com.mnnit.moticlubs.domain.usecase.MemberUseCases
import com.mnnit.moticlubs.domain.usecase.PostUseCases
import com.mnnit.moticlubs.domain.usecase.ReplyUseCases
import com.mnnit.moticlubs.domain.usecase.UrlUseCases
import com.mnnit.moticlubs.domain.usecase.UserUseCases
import com.mnnit.moticlubs.domain.usecase.ViewUseCases
import com.mnnit.moticlubs.domain.usecase.channel.AddChannel
import com.mnnit.moticlubs.domain.usecase.channel.DeleteChannel
import com.mnnit.moticlubs.domain.usecase.channel.GetAllChannels
import com.mnnit.moticlubs.domain.usecase.channel.GetChannel
import com.mnnit.moticlubs.domain.usecase.channel.UpdateChannel
import com.mnnit.moticlubs.domain.usecase.club.GetClubs
import com.mnnit.moticlubs.domain.usecase.club.UpdateClub
import com.mnnit.moticlubs.domain.usecase.member.AddMembers
import com.mnnit.moticlubs.domain.usecase.member.GetMembers
import com.mnnit.moticlubs.domain.usecase.member.RemoveMember
import com.mnnit.moticlubs.domain.usecase.post.DeletePost
import com.mnnit.moticlubs.domain.usecase.post.GetPosts
import com.mnnit.moticlubs.domain.usecase.post.SendPost
import com.mnnit.moticlubs.domain.usecase.post.UpdatePost
import com.mnnit.moticlubs.domain.usecase.reply.DeleteReply
import com.mnnit.moticlubs.domain.usecase.reply.GetReplies
import com.mnnit.moticlubs.domain.usecase.reply.SendReply
import com.mnnit.moticlubs.domain.usecase.urls.AddUrls
import com.mnnit.moticlubs.domain.usecase.urls.GetUrls
import com.mnnit.moticlubs.domain.usecase.user.GetAllAdmins
import com.mnnit.moticlubs.domain.usecase.user.GetAllUsers
import com.mnnit.moticlubs.domain.usecase.user.GetUser
import com.mnnit.moticlubs.domain.usecase.user.UpdateUser
import com.mnnit.moticlubs.domain.usecase.views.AddViews
import com.mnnit.moticlubs.domain.usecase.views.GetViews
import com.mnnit.moticlubs.domain.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build(),
            )
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideLocalDatabase(application: Application): LocalDatabase =
        Room.databaseBuilder(application, LocalDatabase::class.java, LocalDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideRepository(application: Application, apiService: ApiService, db: LocalDatabase): Repository =
        RepositoryImpl(db = db, apiService = apiService, application = application)

    @Provides
    @Singleton
    fun providePostUseCases(repository: Repository): PostUseCases =
        PostUseCases(
            getPosts = GetPosts(repository),
            sendPost = SendPost(repository),
            updatePost = UpdatePost(repository),
            deletePost = DeletePost(repository),
        )

    @Provides
    @Singleton
    fun provideMemberUseCases(repository: Repository): MemberUseCases =
        MemberUseCases(
            getMembers = GetMembers(repository),
            addMembers = AddMembers(repository),
            removeMember = RemoveMember(repository),
        )

    @Provides
    @Singleton
    fun provideUserUseCases(repository: Repository): UserUseCases =
        UserUseCases(
            getUser = GetUser(repository),
            updateUser = UpdateUser(repository),
            getAllAdmins = GetAllAdmins(repository),
            getAllUsers = GetAllUsers(repository),
        )

    @Provides
    @Singleton
    fun provideChannelUseCases(repository: Repository): ChannelUseCases =
        ChannelUseCases(
            getAllChannels = GetAllChannels(repository),
            getChannel = GetChannel(repository),
            addChannel = AddChannel(repository),
            updateChannel = UpdateChannel(repository),
            deleteChannel = DeleteChannel(repository),
        )

    @Provides
    @Singleton
    fun provideClubUseCases(repository: Repository): ClubUseCases =
        ClubUseCases(
            getClubs = GetClubs(repository),
            updateClub = UpdateClub(repository),
        )

    @Provides
    @Singleton
    fun provideViewUseCases(repository: Repository): ViewUseCases =
        ViewUseCases(
            getViews = GetViews(repository),
            addViews = AddViews(repository),
        )

    @Provides
    @Singleton
    fun provideUrlUseCases(repository: Repository): UrlUseCases =
        UrlUseCases(
            getUrls = GetUrls(repository),
            addUrls = AddUrls(repository),
        )

    @Provides
    @Singleton
    fun provideReplyUseCases(repository: Repository): ReplyUseCases =
        ReplyUseCases(
            getReplies = GetReplies(repository),
            sendReply = SendReply(repository),
            deleteReply = DeleteReply(repository),
        )
}

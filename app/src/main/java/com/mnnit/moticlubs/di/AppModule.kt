package com.mnnit.moticlubs.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.mnnit.moticlubs.data.data_source.LocalDatabase
import com.mnnit.moticlubs.data.network.ApiService
import com.mnnit.moticlubs.data.repository.RepositoryImpl
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.*
import com.mnnit.moticlubs.domain.use_case.channel.AddChannel
import com.mnnit.moticlubs.domain.use_case.channel.DeleteChannel
import com.mnnit.moticlubs.domain.use_case.channel.GetChannels
import com.mnnit.moticlubs.domain.use_case.channel.UpdateChannel
import com.mnnit.moticlubs.domain.use_case.club.GetAdmins
import com.mnnit.moticlubs.domain.use_case.club.GetClubs
import com.mnnit.moticlubs.domain.use_case.club.UpdateClub
import com.mnnit.moticlubs.domain.use_case.post.DeletePost
import com.mnnit.moticlubs.domain.use_case.post.GetPosts
import com.mnnit.moticlubs.domain.use_case.post.SendPost
import com.mnnit.moticlubs.domain.use_case.post.UpdatePost
import com.mnnit.moticlubs.domain.use_case.reply.DeleteReply
import com.mnnit.moticlubs.domain.use_case.reply.GetReplies
import com.mnnit.moticlubs.domain.use_case.reply.SendReply
import com.mnnit.moticlubs.domain.use_case.subscribe.GetSubscribers
import com.mnnit.moticlubs.domain.use_case.subscribe.SubscribeClub
import com.mnnit.moticlubs.domain.use_case.subscribe.UnsubscribeClub
import com.mnnit.moticlubs.domain.use_case.urls.AddUrls
import com.mnnit.moticlubs.domain.use_case.urls.GetUrls
import com.mnnit.moticlubs.domain.use_case.user.GetUser
import com.mnnit.moticlubs.domain.use_case.user.UpdateUser
import com.mnnit.moticlubs.domain.use_case.views.AddViews
import com.mnnit.moticlubs.domain.use_case.views.GetViews
import com.mnnit.moticlubs.domain.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

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
                    .build()
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
            deletePost = DeletePost(repository)
        )

    @Provides
    @Singleton
    fun provideSubscriberUseCases(repository: Repository): SubscriberUseCases =
        SubscriberUseCases(
            getSubscribers = GetSubscribers(repository),
            subscribeClub = SubscribeClub(repository),
            unsubscribeClub = UnsubscribeClub(repository)
        )

    @Provides
    @Singleton
    fun provideUserUseCases(repository: Repository): UserUseCases =
        UserUseCases(
            getUser = GetUser(repository),
            updateUser = UpdateUser(repository)
        )

    @Provides
    @Singleton
    fun provideChannelUseCases(repository: Repository): ChannelUseCases =
        ChannelUseCases(
            getChannels = GetChannels(repository),
            addChannel = AddChannel(repository),
            updateChannel = UpdateChannel(repository),
            deleteChannel = DeleteChannel(repository)
        )

    @Provides
    @Singleton
    fun provideClubUseCases(repository: Repository): ClubUseCases =
        ClubUseCases(
            getClubs = GetClubs(repository),
            getAdmins = GetAdmins(repository),
            updateClub = UpdateClub(repository)
        )

    @Provides
    @Singleton
    fun provideViewUseCases(repository: Repository): ViewUseCases =
        ViewUseCases(
            getViews = GetViews(repository),
            addViews = AddViews(repository)
        )

    @Provides
    @Singleton
    fun provideUrlUseCases(repository: Repository): UrlUseCases =
        UrlUseCases(
            getUrls = GetUrls(repository),
            addUrls = AddUrls(repository)
        )

    @Provides
    @Singleton
    fun provideReplyUseCases(repository: Repository): ReplyUseCases =
        ReplyUseCases(
            getReplies = GetReplies(repository),
            sendReply = SendReply(repository),
            deleteReply = DeleteReply(repository)
        )
}

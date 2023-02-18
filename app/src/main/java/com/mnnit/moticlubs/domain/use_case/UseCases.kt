package com.mnnit.moticlubs.domain.use_case

data class PostUseCases(
    val getPosts: GetPosts,
    val sendPost: SendPost,
    val updatePost: UpdatePost,
    val deletePost: DeletePost
)

data class SubscriberUseCases(
    val getSubscribers: GetSubscribers,
    val subscribeClub: SubscribeClub,
    val unsubscribeClub: UnsubscribeClub
)

data class UserUseCases(
    val getUser: GetUser,
    val updateUser: UpdateUser
)

data class ChannelUseCases(
    val getChannels: GetChannels,
    val updateChannel: UpdateChannel,
    val addChannel: AddChannel,
    val deleteChannel: DeleteChannel
)

data class ClubUseCases(
    val getClubs: GetClubs,
    val getAdmins: GetAdmins,
    val updateClub: UpdateClub
)

data class UrlUseCases(
    val getUrls: GetUrls,
    val addUrls: AddUrls
)

data class ViewUseCases(
    val getViews: GetViews,
    val addViews: AddViews
)

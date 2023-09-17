package com.mnnit.moticlubs.domain.usecase

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

data class PostUseCases(
    val getPosts: GetPosts,
    val sendPost: SendPost,
    val updatePost: UpdatePost,
    val deletePost: DeletePost,
)

data class MemberUseCases(
    val getMembers: GetMembers,
    val addMembers: AddMembers,
    val removeMember: RemoveMember,
)

data class UserUseCases(
    val getUser: GetUser,
    val updateUser: UpdateUser,
    val getAllAdmins: GetAllAdmins,
    val getAllUsers: GetAllUsers,
)

data class ChannelUseCases(
    val getAllChannels: GetAllChannels,
    val getChannel: GetChannel,
    val updateChannel: UpdateChannel,
    val addChannel: AddChannel,
    val deleteChannel: DeleteChannel,
)

data class ClubUseCases(
    val getClubs: GetClubs,
    val updateClub: UpdateClub,
)

data class UrlUseCases(
    val getUrls: GetUrls,
    val addUrls: AddUrls,
)

data class ViewUseCases(
    val getViews: GetViews,
    val addViews: AddViews,
)

data class ReplyUseCases(
    val getReplies: GetReplies,
    val sendReply: SendReply,
    val deleteReply: DeleteReply,
)

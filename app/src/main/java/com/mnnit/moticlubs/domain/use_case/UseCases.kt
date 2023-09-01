package com.mnnit.moticlubs.domain.use_case

import com.mnnit.moticlubs.domain.use_case.channel.AddChannel
import com.mnnit.moticlubs.domain.use_case.channel.DeleteChannel
import com.mnnit.moticlubs.domain.use_case.channel.GetAllChannels
import com.mnnit.moticlubs.domain.use_case.channel.GetChannel
import com.mnnit.moticlubs.domain.use_case.channel.UpdateChannel
import com.mnnit.moticlubs.domain.use_case.user.GetAllAdmins
import com.mnnit.moticlubs.domain.use_case.club.GetClubs
import com.mnnit.moticlubs.domain.use_case.club.UpdateClub
import com.mnnit.moticlubs.domain.use_case.member.AddMembers
import com.mnnit.moticlubs.domain.use_case.member.GetMembers
import com.mnnit.moticlubs.domain.use_case.member.RemoveMembers
import com.mnnit.moticlubs.domain.use_case.post.DeletePost
import com.mnnit.moticlubs.domain.use_case.post.GetPosts
import com.mnnit.moticlubs.domain.use_case.post.SendPost
import com.mnnit.moticlubs.domain.use_case.post.UpdatePost
import com.mnnit.moticlubs.domain.use_case.reply.DeleteReply
import com.mnnit.moticlubs.domain.use_case.reply.GetReplies
import com.mnnit.moticlubs.domain.use_case.reply.SendReply
import com.mnnit.moticlubs.domain.use_case.urls.AddUrls
import com.mnnit.moticlubs.domain.use_case.urls.GetUrls
import com.mnnit.moticlubs.domain.use_case.user.GetUser
import com.mnnit.moticlubs.domain.use_case.user.UpdateUser
import com.mnnit.moticlubs.domain.use_case.views.AddViews
import com.mnnit.moticlubs.domain.use_case.views.GetViews

data class PostUseCases(
    val getPosts: GetPosts,
    val sendPost: SendPost,
    val updatePost: UpdatePost,
    val deletePost: DeletePost
)

data class MemberUseCases(
    val getMembers: GetMembers,
    val addMembers: AddMembers,
    val removeMembers: RemoveMembers
)

data class UserUseCases(
    val getUser: GetUser,
    val updateUser: UpdateUser,
    val getAllAdmins: GetAllAdmins,
)

data class ChannelUseCases(
    val getAllChannels: GetAllChannels,
    val getChannel: GetChannel,
    val updateChannel: UpdateChannel,
    val addChannel: AddChannel,
    val deleteChannel: DeleteChannel
)

data class ClubUseCases(
    val getClubs: GetClubs,
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

data class ReplyUseCases(
    val getReplies: GetReplies,
    val sendReply: SendReply,
    val deleteReply: DeleteReply
)

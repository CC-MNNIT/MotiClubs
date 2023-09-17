package com.mnnit.moticlubs.domain.util

import com.mnnit.moticlubs.data.network.dto.AdminDetailDto
import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.PostDto
import com.mnnit.moticlubs.data.network.dto.ReplyDto
import com.mnnit.moticlubs.data.network.dto.UrlResponseModel
import com.mnnit.moticlubs.data.network.dto.UserDto
import com.mnnit.moticlubs.data.network.dto.ViewDto
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View

fun UserDto.mapToDomain(): User =
    User(
        userId = this.uid,
        regNo = this.regNo,
        name = this.name,
        email = this.email,
        course = this.course,
        branch = this.branch,
        avatar = this.avatar,
        contact = this.contact,
    )

fun AdminDetailDto.mapToDomain(): User =
    User(
        userId = this.uid,
        regNo = this.regNo,
        name = this.name,
        email = this.email,
        course = this.course,
        branch = this.branch,
        avatar = this.avatar,
        contact = this.contact,
    )

fun ClubModel.mapToDomain(): Club =
    Club(
        clubId = this.clubId,
        name = this.name,
        description = this.description,
        avatar = this.avatar,
        summary = this.summary,
    )

fun ChannelDto.mapToDomain(): Channel =
    Channel(
        channelId = this.channelId,
        clubId = this.clubId,
        name = this.name,
        private = if (this.private) 1 else 0,
    )

fun Channel.mapFromDomain(): ChannelDto =
    ChannelDto(
        channelId = this.channelId,
        clubId = this.clubId,
        name = this.name,
        private = this.private == 1,
    )

fun PostDto.mapToDomain(page: Int): Post =
    Post(
        postId = this.postId,
        channelId = this.channelId,
        updated = this.updated,
        pageNo = page,
        message = this.message,
        userId = this.userId,
    )

fun Post.mapFromDomain(): PostDto =
    PostDto(
        postId = this.postId,
        channelId = this.channelId,
        updated = this.updated,
        message = this.message,
        userId = this.userId,
    )

fun ViewDto.mapToDomain(): View =
    View(
        userId = this.userId,
        postId = this.postId,
    )

fun UrlResponseModel.mapToDomain(): Url =
    Url(
        urlId = this.urlId,
        name = this.name,
        colorCode = this.color,
        url = this.url,
        clubId = this.clubId,
    )

fun ReplyDto.mapToDomain(page: Int): Reply =
    Reply(
        postId = this.postId,
        userId = this.userId,
        message = this.message,
        time = this.time,
        pageNo = page,
    )

fun Reply.mapFromDomain(): ReplyDto =
    ReplyDto(
        postId = this.postId,
        userId = this.userId,
        message = this.message,
        time = this.time,
    )

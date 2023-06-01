package com.mnnit.moticlubs.domain.util

import com.mnnit.moticlubs.data.network.dto.AdminDetailDto
import com.mnnit.moticlubs.data.network.dto.ChannelDto
import com.mnnit.moticlubs.data.network.dto.ClubModel
import com.mnnit.moticlubs.data.network.dto.PostDto
import com.mnnit.moticlubs.data.network.dto.ReplyDto
import com.mnnit.moticlubs.data.network.dto.SendPostDto
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
        userID = this.uid,
        regNo = this.regNo,
        name = this.name,
        email = this.email,
        course = this.course,
        phoneNumber = this.phone,
        avatar = this.avatar
    )

fun AdminDetailDto.mapToDomain(): User =
    User(
        userID = this.uid,
        regNo = this.regNo,
        name = this.name,
        email = this.email,
        course = this.course,
        phoneNumber = this.phoneNumber,
        avatar = this.avatar
    )

fun ClubModel.mapToDomain(): Club =
    Club(
        clubID = this.id,
        name = this.name,
        description = this.description,
        avatar = this.avatar,
        summary = this.summary,
    )

fun ChannelDto.mapToDomain(): Channel =
    Channel(
        channelID = this.channelID,
        clubID = this.clubID,
        name = this.name
    )

fun PostDto.mapToDomain(page: Int): Post =
    Post(
        postID = this.postID,
        channelID = this.channelID,
        pageNo = page,
        message = this.message,
        time = this.time,
        userID = this.userID
    )

fun Post.mapFromDomain(clubID: Int, general: Int): SendPostDto =
    SendPostDto(
        postID = this.postID,
        channelID = this.channelID,
        clubID = clubID,
        message = this.message,
        time = this.time,
        userID = this.userID,
        general = general
    )

fun ViewDto.mapToDomain(): View =
    View(
        userID = this.userID,
        postID = this.postID
    )

fun UrlResponseModel.mapToDomain(): Url =
    Url(
        urlID = this.urlID,
        clubID = this.clubID,
        name = this.name,
        colorCode = this.color,
        url = this.url,
    )

fun ReplyDto.mapToDomain(): Reply =
    Reply(
        postID = this.postID,
        userID = this.userID,
        repliedToUID = this.toUID,
        message = this.message,
        time = this.time
    )

fun Reply.mapFromDomain(): ReplyDto =
    ReplyDto(
        postID = this.postID,
        userID = this.userID,
        toUID = this.repliedToUID,
        message = this.message,
        time = this.time
    )

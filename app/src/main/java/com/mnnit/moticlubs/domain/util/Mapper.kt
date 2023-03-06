package com.mnnit.moticlubs.domain.util

import com.mnnit.moticlubs.data.network.dto.*
import com.mnnit.moticlubs.domain.model.*

fun AdminDetailResponse.mapToDomain(): User =
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

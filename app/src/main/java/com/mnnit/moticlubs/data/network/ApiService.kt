package com.mnnit.moticlubs.data.network

import com.mnnit.moticlubs.data.network.api.*

interface ApiService : UserApi, ClubApi, PostsApi, ChannelsApi, UrlApi, ViewsApi

// TODO: Return all the attrs of User for admin and respective clubID as well
// TODO: Remove clubID from posts
// TODO: Fetch posts from channelID
// TODO: Get user details should fetch all information
// TODO: Post, Channel and Url will have creation time as primary key
// TODO: Fetch all the subscriber model (uid, cid)

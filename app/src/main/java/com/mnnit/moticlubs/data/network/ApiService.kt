package com.mnnit.moticlubs.data.network

import com.mnnit.moticlubs.data.network.api.ChannelsApi
import com.mnnit.moticlubs.data.network.api.ClubApi
import com.mnnit.moticlubs.data.network.api.GithubApi
import com.mnnit.moticlubs.data.network.api.PostsApi
import com.mnnit.moticlubs.data.network.api.ReplyApi
import com.mnnit.moticlubs.data.network.api.UrlApi
import com.mnnit.moticlubs.data.network.api.UserApi
import com.mnnit.moticlubs.data.network.api.ViewsApi

interface ApiService :
    UserApi,
    ClubApi,
    PostsApi,
    ChannelsApi,
    UrlApi,
    ViewsApi,
    ReplyApi,
    GithubApi

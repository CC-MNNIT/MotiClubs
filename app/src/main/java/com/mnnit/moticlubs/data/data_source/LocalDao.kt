package com.mnnit.moticlubs.data.data_source

import androidx.room.Dao

@Dao
interface LocalDao : AdminDao, ChannelDao, ClubDao, PostDao, SubscriberDao, UrlDao, UserDao, ViewDao, ReplyDao

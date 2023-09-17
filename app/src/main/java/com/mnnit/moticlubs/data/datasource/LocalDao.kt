package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao

@Dao
interface LocalDao :
    AdminDao,
    ChannelDao,
    ClubDao,
    PostDao,
    MemberDao,
    UrlDao,
    UserDao,
    ViewDao,
    ReplyDao,
    StampDao

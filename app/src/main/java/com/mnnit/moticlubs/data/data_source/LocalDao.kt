package com.mnnit.moticlubs.data.data_source

import androidx.room.Dao

@Dao
interface LocalDao : AdminDao,
    ChannelDao,
    ClubDao,
    PostDao,
    MemberDao,
    UrlDao,
    UserDao,
    ViewDao,
    ReplyDao,
    StampDao

package com.mnnit.moticlubs.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mnnit.moticlubs.domain.model.Admin
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Post
import com.mnnit.moticlubs.domain.model.Reply
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.model.View

@Database(
    entities = [
        Admin::class,
        Channel::class,
        Club::class,
        Post::class,
        Member::class,
        Url::class,
        User::class,
        View::class,
        Reply::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract val dao: LocalDao

    companion object {
        const val DATABASE_NAME = "moti_clubs_mnnit_db"
    }
}

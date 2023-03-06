package com.mnnit.moticlubs.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mnnit.moticlubs.domain.model.*

@Database(
    entities = [
        Admin::class,
        Channel::class,
        Club::class,
        Post::class,
        Subscriber::class,
        Url::class,
        User::class,
        View::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {

    abstract val dao: LocalDao

    companion object {
        const val DATABASE_NAME = "moti_clubs_mnnit_db"
    }
}

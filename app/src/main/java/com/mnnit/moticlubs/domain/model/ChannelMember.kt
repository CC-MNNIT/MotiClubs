package com.mnnit.moticlubs.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class ChannelMember(
    @ColumnInfo(name = "uid")
    val userId: Long,

    @ColumnInfo(name = "chid")
    val channelId: Long,

    @ColumnInfo(name = "cid")
    val clubId: Long,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "private")
    val private: Int,
) : Parcelable

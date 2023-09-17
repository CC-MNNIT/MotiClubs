package com.mnnit.moticlubs.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mnnit.moticlubs.domain.model.Member

@Dao
interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMember(member: Member)

    @Query("SELECT * FROM member WHERE member.chid = :channelId")
    suspend fun getMembers(channelId: Long): List<Member>

    @Delete
    suspend fun deleteMember(member: Member)
}

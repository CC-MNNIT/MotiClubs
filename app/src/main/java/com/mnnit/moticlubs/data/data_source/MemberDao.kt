package com.mnnit.moticlubs.data.data_source

import androidx.room.*
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

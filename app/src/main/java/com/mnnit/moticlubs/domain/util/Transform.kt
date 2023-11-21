package com.mnnit.moticlubs.domain.util

import com.mnnit.moticlubs.domain.model.AdminUser
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.ChannelMember
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User

fun List<Club>.applySorting(
    admins: List<AdminUser>,
    channelMembers: List<ChannelMember>,
    userId: Long,
): List<Club> = sortedWith(
    compareBy(
        { club -> club.clubId != 1L && club.clubId != 2L },
        { club -> !admins.any { admin -> admin.userId == userId && admin.clubId == club.clubId } },
        { club -> !channelMembers.any { member -> member.clubId == club.clubId } },
        { club -> club.clubId },
    ),
)

fun List<Channel>.populate(channelMap: PublishedMap<Long, PublishedList<Channel>>) {
    forEach { channel -> channelMap.value[channel.clubId] = publishedStateListOf() }
    forEach { channel -> channelMap.value[channel.clubId]?.value?.add(channel) }
}

fun List<Member>.applySorting(
    admins: List<AdminUser>,
    clubId: Long,
    memberInfo: PublishedMap<Long, User>,
): List<Member> = sortedWith(
    compareBy(
        { member ->
            !admins.any { admin ->
                admin.userId == member.userId && admin.clubId == clubId
            }
        },
        { member -> memberInfo.value[member.userId]?.name ?: "" },
    ),
)

package com.mnnit.moticlubs.domain.util

import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.ChannelMember
import com.mnnit.moticlubs.domain.model.Club

fun List<Club>.applySorting(channelMembers: List<ChannelMember>): List<Club> = sortedWith(
    compareBy(
        { club -> club.clubId != 1L },
        { club -> !channelMembers.any { member -> member.clubId == club.clubId } },
        { club -> club.clubId },
    ),
)

fun List<Channel>.populate(channelMap: PublishedMap<Long, PublishedList<Channel>>) {
    forEach { channel -> channelMap.value[channel.clubId] = publishedStateListOf() }
    forEach { channel -> channelMap.value[channel.clubId]?.value?.add(channel) }
}

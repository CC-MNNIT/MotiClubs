package com.mnnit.moticlubs.domain.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mnnit.moticlubs.ui.components.OtherLinkComposeModel
import com.mnnit.moticlubs.ui.components.SocialLinkComposeModel

@Entity(tableName = "url")
data class Url(
    @PrimaryKey
    @ColumnInfo(name = "urlid", index = true)
    val urlID: Long,

    @ColumnInfo(name = "cid", index = true)
    val clubID: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "color")
    val colorCode: String = "FFFFFF",

    @ColumnInfo(name = "url")
    val url: String
) {
    constructor() : this(-1L, -1, "", "FFFFFF", "")

    fun mapToOtherLinkModel(): OtherLinkComposeModel = OtherLinkComposeModel(
        urlID, clubID,
        mutableStateOf(TextFieldValue("$name\\$url")),
        mutableStateOf("#$colorCode"),
        mutableStateOf(Color(android.graphics.Color.parseColor("#$colorCode")))
    )

    fun mapToSocialLinkModel(): SocialLinkComposeModel = SocialLinkComposeModel(
        urlID, clubID, name,
        mutableStateOf(TextFieldValue(url)),
        mutableStateOf("#$colorCode"),
        mutableStateOf(Color(android.graphics.Color.parseColor("#$colorCode")))
    )

    fun getLinkBadge(): String {
        return "[![$name](https://img.shields.io/badge/$name-%23$colorCode.svg?style=for-the-badge)]($url) "
    }
}

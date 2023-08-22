package com.mnnit.moticlubs.domain.util

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mnnit.moticlubs.data.network.dto.UrlModel
import com.mnnit.moticlubs.domain.model.Url
import com.mnnit.moticlubs.ui.components.MarkdownText

abstract class LinkComposeModel {
    abstract fun getUrl(): String
    abstract fun getName(): String
    fun validUrl(): Boolean {
        val url = getUrl()
        return url.isNotEmpty() && url.matches(Patterns.WEB_URL.toRegex())
    }
}

data class SocialLinkComposeModel(
    val urlID: Long = -1L,
    var clubID: Long,
    var urlName: String,
    val urlFieldValue: MutableState<TextFieldValue>,
    val colorCode: MutableState<String>,
    val color: MutableState<Color>,
) : LinkComposeModel() {

    companion object {
        val socialLinkNames = listOf("facebook", "instagram", "twitter", "github")
        private val socialColors = listOf("3578E5", "E1306C", "1DA1F2", "242424")
    }

    constructor() : this(
        -1,
        -1,
        "",
        mutableStateOf(TextFieldValue("")),
        mutableStateOf(""),
        mutableStateOf(Color.White)
    )

    fun mapToUrlModel(): UrlModel {
        return UrlModel(
            urlId = if (urlID == -1L) System.currentTimeMillis() else urlID,
            name = urlName.trim(),
            color = socialColors[socialLinkNames.indexOf(urlName.trim())],
            url = urlFieldValue.value.text.toLowerCase(LocaleList.current).trim()
        )
    }

    override fun getUrl(): String = urlFieldValue.value.text

    override fun getName(): String = urlName
}

data class OtherLinkComposeModel(
    val urlID: Long = -1L,
    val clubID: Long,
    val fieldValue: MutableState<TextFieldValue>,
    val colorCode: MutableState<String>,
    val color: MutableState<Color>,
) : LinkComposeModel() {

    constructor() : this(
        -1,
        -1,
        mutableStateOf(TextFieldValue("\\")),
        mutableStateOf("FFFFFF"),
        mutableStateOf(Color.White)
    )

    fun mapToUrlModel(): UrlModel {
        val tokens = fieldValue.value.text.split("\\")
        return UrlModel(
            urlId = if (urlID == -1L) System.currentTimeMillis() else urlID,
            name = tokens[0].trim(),
            color = colorCode.value.replace("#", ""),
            url = tokens[1].toLowerCase(LocaleList.current).trim()
        )
    }

    override fun getUrl(): String = fieldValue.value.text.split("\\")[1].trim()
    override fun getName(): String = fieldValue.value.text.split("\\")[0].trim()
}

fun List<Url>.toStringBadges(): String {
    val sb = StringBuilder()
    forEach { sb.append(it.getLinkBadge()) }
    return sb.toString()
}

@Composable
fun Links(isAdmin: Boolean, linksHeader: String, links: List<Url>, onClick: () -> Unit = {}) {
    Text(modifier = Modifier.padding(top = 16.dp), text = linksHeader, fontSize = 13.sp)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
        MarkdownText(
            markdown = links.toStringBadges(),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .fillMaxWidth(if (isAdmin) 0.8f else 1f)
        )
        if (isAdmin) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onClick() },
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(imageVector = Icons.Rounded.Edit, contentDescription = "")
            }
        }
    }
}

package com.mnnit.moticlubs.ui.components

import android.util.Patterns
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class LinkModel(var name: String, var url: String, var colorCode: String) {

    fun getLinkBadge(): String {
        return "[![$name](https://img.shields.io/badge/$name-%23$colorCode.svg?style=for-the-badge)]($url) "
    }
}

data class LinkComposeModel(
    val fieldValue: MutableState<TextFieldValue>,
    val colorCode: MutableState<String>,
    val color: MutableState<Color>,
) {

    constructor() : this(mutableStateOf(TextFieldValue("\\")), mutableStateOf(""), mutableStateOf(Color.White))

    fun getLinkModel(): LinkModel {
        val tokens = fieldValue.value.text.split("\\")
        return LinkModel(tokens[0].trim(), tokens[1].trim(), colorCode.value)
    }

    fun getUrl(): String = fieldValue.value.text.split("\\")[1].trim()
    fun getName(): String = fieldValue.value.text.split("\\")[0].trim()

    fun validUrl(): Boolean = getUrl().matches(Patterns.WEB_URL.toRegex())
}

fun List<LinkComposeModel>.shouldEnable(): Boolean {
    for (i in this.indices) {
        if (!this[i].validUrl() || this[i].getName().isEmpty()) return false
    }
    return true
}

fun List<LinkModel>.toStringBadges(): String {
    val sb = StringBuilder()
    forEach { sb.append(it.getLinkBadge()) }
    return sb.toString()
}

@Composable
fun Links(isAdmin: Boolean, linksHeader: String, links: List<LinkModel>, onClick: () -> Unit = {}) {
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

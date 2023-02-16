package com.mnnit.moticlubs.ui.components

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun MarkdownRender(
    modifier: Modifier = Modifier,
    mkd: String,
    imageReplacerMap: MutableMap<String, String> = mutableMapOf(),
    selectable: Boolean = false,
    disableLinkMovementMethod: Boolean = false,
    onImageClick: (url: String) -> Unit = {}
) {
    val colorScheme = getColorScheme()

    var preprocessText = mkd
    if (imageReplacerMap.isEmpty()) {
        mkd.lines().forEach {
            if (it.startsWith("<img src")) {
                val key = "[image_${imageReplacerMap.size}]"
                imageReplacerMap[key] = it
                preprocessText = preprocessText.replace(it, key)
            }
        }
    }

    var text = preprocessText
    imageReplacerMap.forEach { (key, value) ->
        text = text.replace(key.replace("\n", ""), value)
    }

    val list = mutableListOf<PreviewMarkdown>()
    val sb = StringBuilder()
    text.lines().forEach {
        if (it.startsWith("<img src")) {
            if (sb.isNotEmpty()) list.add(PreviewMarkdown.Text(sb.toString()))

            sb.clear()
            val matcher = Patterns.WEB_URL.matcher(it)
            if (matcher.find()) {
                list.add(PreviewMarkdown.Image(it.substring(matcher.start(), matcher.end())))
            }
        } else {
            sb.append(it).append("\n")
        }
    }
    if (sb.isNotEmpty()) list.add(PreviewMarkdown.Text(sb.toString()))

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(count = list.size) { idx ->
            if (list[idx] is PreviewMarkdown.Text) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MarkdownText(
                        markdown = list[idx].str,
                        color = contentColorFor(backgroundColor = colorScheme.background),
                        selectable = selectable,
                        disableLinkMovementMethod = disableLinkMovementMethod,
                        modifier = modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Image(
                        painter = LocalContext.current.getImageUrlPainter(url = list[idx].str), contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .heightIn(0.dp, 248.dp)
                            .clickable(true, onClick = { onImageClick(list[idx].str) }),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}
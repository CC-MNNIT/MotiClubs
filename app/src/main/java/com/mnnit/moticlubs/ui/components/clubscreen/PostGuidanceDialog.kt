package com.mnnit.moticlubs.ui.components.clubscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mnnit.moticlubs.ui.components.MarkdownText
import com.mnnit.moticlubs.ui.theme.getColorScheme

@Composable
fun PostGuidanceDialog(
    showDialog: MutableState<Boolean>
) {
    val colorScheme = getColorScheme()
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = { showDialog.value = false }, DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(128.dp, 512.dp)
            ) {
                MarkdownText(
                    markdown = MKD_GUIDE, modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState),
                    color = contentColorFor(backgroundColor = colorScheme.background)
                )
            }
        }
    }
}

private const val MKD_GUIDE = "<align><h2>Markdown Formatting</h2></align>\n\n" +
        "Here are some syntax you can use for formatting your post.\n\n" +
        "### Headers: \n" +
        "```\n" +
        "# Header 1\n" +
        "## Header 2\n" +
        "### Header 3\n" +
        "```\n" +
        "# Header 1\n" +
        "## Header 2\n" +
        "### Header 3\n\n" +
        "--------------------------\n" +
        "### Styles:\n" +
        "```\n" +
        "**bold** or 'B' icon\n" +
        "_italics_ or 'I' icon\n" +
        "~~strike~~ or 'T' icon\n" +
        "```\n" +
        "**bold**\n" +
        "_italics_\n" +
        "~~strike~~\n\n" +
        "-------------------------\n" +
        "### Listing:\n" +
        "```\n" +
        "- item 1\n" +
        "- item 2\n" +
        "- item 3\n" +
        "```\n" +
        "- item 1\n" +
        "- item 2\n" +
        "- item 3\n\n" +
        "-------------------------\n" +
        "### Table:\n" +
        "```\n" +
        "| Name | Reg No |\n" +
        "|----|----|\n" +
        "|Shashank|20204184|\n" +
        "|Hitesh|20204085|\n" +
        "|Amit|20204022|\n" +
        "```\n" +
        "| Name | Reg No |\n" +
        "|----|----|\n" +
        "|Shashank|20204184|\n" +
        "|Hitesh|20204085|\n" +
        "|Amit|20204022|\n\n" +
        "-------------------------\n" +
        "### Links:\n" +
        "```\n" +
        "[link_name](url)\n" +
        "Eg:\n" +
        "[Rick Roll](https://youtu.be/xvFZjo5PgG0)\n" +
        "```\n" +
        "[Rick Roll](https://youtu.be/xvFZjo5PgG0)\n\n" +
        "-------------------------\n" +
        "### Quoting:\n" +
        "```\n" +
        "> This is quoted text\n" +
        "```\n" +
        "> This is quoted text\n\n" +
        "**Good Luck !**"

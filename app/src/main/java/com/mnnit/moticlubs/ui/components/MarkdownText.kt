package com.mnnit.moticlubs.ui.components

import android.content.Context
import android.text.Layout
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AlignmentSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import io.noties.markwon.*
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.AsyncDrawableScheduler
import io.noties.markwon.image.DefaultMediaDecoder
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.gif.GifMediaDecoder
import io.noties.markwon.image.network.OkHttpNetworkSchemeHandler
import io.noties.markwon.image.picasso.PicassoImagesPlugin
import io.noties.markwon.image.picasso.PicassoImagesPlugin.PicassoStore
import io.noties.markwon.image.svg.SvgMediaDecoder
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.node.SoftLineBreak
import java.util.*

/**
 * Modified from and Credits to : https://github.com/jeziellago/compose-markdown
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    selectable: Boolean = false,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    // this option will disable all clicks on links, inside the markdown text
    // it also enable the parent view to receive the click event
    disableLinkMovementMethod: Boolean = false,
) {
    val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val context: Context = LocalContext.current
    val markdownRender: Markwon = remember { createMarkdownRender(context) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createTextView(
                context = ctx,
                color = color,
                selectable = selectable,
                defaultColor = defaultColor,
                fontSize = fontSize,
                fontResource = fontResource,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                viewId = viewId,
                onClick = onClick,
            )
        },
        update = { textView ->
            markdownRender.setMarkdown(textView, markdown)
            if (disableLinkMovementMethod && !selectable) {
                textView.movementMethod = null
            }
            if (!disableLinkMovementMethod && selectable) {
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    )
}

private fun createTextView(
    context: Context,
    color: Color = Color.Unspecified,
    defaultColor: Color,
    selectable: Boolean,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null
): TextView {

    val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
            textAlign = textAlign,
        )
    )
    return TextView(context).apply {
        onClick?.let { setOnClickListener { onClick() } }
        setTextColor(textColor.toArgb())
        setMaxLines(maxLines)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)
        setTextIsSelectable(selectable)

        viewId?.let { id = viewId }
        textAlign?.let { align ->
            textAlignment = when (align) {
                TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }

        fontResource?.let { font ->
            typeface = ResourcesCompat.getFont(context, font)
        }
    }
}

private fun createMarkdownRender(context: Context): Markwon {
    return Markwon.builder(context)
        .usePlugin(HtmlPlugin.create { plugin -> plugin.addHandler(TagAlignmentHandler()) })
        .usePlugin(PicassoImagesPlugin.create(object : PicassoStore {
            override fun load(drawable: AsyncDrawable): RequestCreator =
                Picasso.get().load(drawable.destination).tag(drawable)

            override fun cancel(drawable: AsyncDrawable) {
                Picasso.get().cancelTag(drawable)
            }
        }))
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                builder.on(SoftLineBreak::class.java) { visitor, _ -> visitor.forceNewLine() }
            }

            override fun beforeSetText(textView: TextView, markdown: Spanned) {
                AsyncDrawableScheduler.unschedule(textView)
            }

            override fun afterSetText(textView: TextView) {
                AsyncDrawableScheduler.schedule(textView)
            }
        })
        .usePlugin(ImagesPlugin.create { plugin ->
            plugin.addSchemeHandler(OkHttpNetworkSchemeHandler.create())
            plugin.addMediaDecoder(GifMediaDecoder.create(true))

            plugin.addMediaDecoder(SvgMediaDecoder.create(context.resources))
            plugin.addMediaDecoder(SvgMediaDecoder.create())

            plugin.defaultMediaDecoder(DefaultMediaDecoder.create(context.resources))
            plugin.defaultMediaDecoder(DefaultMediaDecoder.create())
        })
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(MarkwonInlineParserPlugin.create())
        .build()
}

private class TagAlignmentHandler : SimpleTagHandler() {
    override fun supportedTags(): MutableCollection<String> = Collections.singleton("align")

    override fun getSpans(configuration: MarkwonConfiguration, renderProps: RenderProps, tag: HtmlTag): Any {
        return AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
    }
}

sealed class PreviewMarkdown(val str: String) {
    class Text(text: String) : PreviewMarkdown(text)
    class Image(url: String) : PreviewMarkdown(url)
}

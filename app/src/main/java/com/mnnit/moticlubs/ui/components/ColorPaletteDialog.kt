package com.mnnit.moticlubs.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.mnnit.moticlubs.domain.util.OtherLinkComposeModel
import com.mnnit.moticlubs.domain.util.PublishedState
import com.mnnit.moticlubs.domain.util.publishedStateOf
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.theme.textColorFor

@Composable
fun ColorPaletteDialog(
    otherLinkComposeModel: OtherLinkComposeModel,
    show: PublishedState<Boolean>,
    modifier: Modifier = Modifier,
) {
    val colorScheme = getColorScheme()
    val controller = rememberColorPickerController().apply {
        this.setWheelColor(Color.Black)
    }

    val colorCode = remember { publishedStateOf("") }
    val color = remember { publishedStateOf(Color.White) }

    Dialog(onDismissRequest = { show.value = false }, DialogProperties()) {
        Box(
            modifier = modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorScheme.background),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Color Palette",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                )

                HsvColorPicker(
                    modifier = Modifier
                        .height(250.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        colorCode.value = colorEnvelope.hexCode.substring(2)
                            .replace("#", "")
                        color.value = colorEnvelope.color
                    },
                )

                Text(
                    text = "#${colorCode.value}",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = colorScheme.onBackground,
                )

                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Button(
                        onClick = { show.value = false },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.background),
                    ) {
                        Text(text = "Cancel", fontSize = 14.sp, color = colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.padding(16.dp))

                    Button(
                        onClick = {
                            show.value = false
                            otherLinkComposeModel.colorCode.value = colorCode.value
                            otherLinkComposeModel.color.value = color.value
                        },
                        modifier = Modifier.align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors(containerColor = color.value),
                    ) {
                        Text(
                            text = "Select",
                            fontSize = 14.sp,
                            color = textColorFor(color.value),
                        )
                    }
                }
            }
        }
    }
}

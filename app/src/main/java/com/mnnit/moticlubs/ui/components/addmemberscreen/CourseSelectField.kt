package com.mnnit.moticlubs.ui.components.addmemberscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.mnnit.moticlubs.ui.theme.getColorScheme
import com.mnnit.moticlubs.ui.viewmodel.AddMemberViewModel

@Composable
fun CourseSelectField(
    viewModel: AddMemberViewModel,
) {
    val colorScheme = getColorScheme()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.CenterEnd)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            value = viewModel.searchBranch.value,
            onValueChange = {},
            textStyle = TextStyle.Default.copy(fontSize = 15.sp),
            enabled = false,
            singleLine = false,
            readOnly = true,
            label = { Text(text = "Branch") },
            leadingIcon = {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 8.dp, end = 4.dp),
                    colors = CardDefaults.cardColors(colorScheme.surfaceColorAtElevation(2.dp)),
                    shape = RoundedCornerShape(20.dp),
                    enabled = !viewModel.isFetching,
                    onClick = { viewModel.courseDropDownExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentSize()
                    ) {
                        Text(
                            text = viewModel.searchCourse.value.ifEmpty { "Course" },
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 4.dp)
                                .wrapContentSize(),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.padding(1.dp))
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            null,
                            Modifier
                                .align(Alignment.CenterVertically)
                                .rotate(if (viewModel.courseDropDownExpanded) 180f else 0f)
                        )
                    }
                }
            },
            trailingIcon = {
                IconButton(
                    modifier = Modifier.size(42.dp),
                    onClick = { viewModel.branchDropDownExpanded = true },
                    enabled = !viewModel.isFetching,
                ) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        null,
                        Modifier.rotate(if (viewModel.branchDropDownExpanded) 180f else 0f)
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = colorScheme.onSurface,
                disabledLabelColor = colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = colorScheme.onSurfaceVariant,
                disabledBorderColor = colorScheme.outline
            )
        )

        DropdownMenu(
            modifier = Modifier.align(Alignment.TopStart),
            expanded = viewModel.courseDropDownExpanded,
            onDismissRequest = { viewModel.courseDropDownExpanded = false },
        ) {
            viewModel.courseList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 13.sp) },
                    onClick = {
                        viewModel.searchCourse.value = option
                        if (listOf(
                                "MCA",
                                "MBA",
                                "M.Sc.",
                            ).any { it == viewModel.searchCourse.value }
                        ) {
                            viewModel.searchBranch.value =
                                viewModel.branchMap[viewModel.searchCourse.value]?.first()
                                    ?: "..."
                        } else {
                            viewModel.searchBranch.value = ""
                        }
                        viewModel.courseDropDownExpanded = false
                        viewModel.filterSearch()
                    })
            }
        }

        DropdownMenu(
            modifier = Modifier.align(Alignment.CenterEnd),
            expanded = viewModel.branchDropDownExpanded,
            onDismissRequest = { viewModel.branchDropDownExpanded = false }
        ) {
            viewModel.branchMap[viewModel.searchCourse.value]?.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option, fontSize = 13.sp) },
                    onClick = {
                        viewModel.searchBranch.value = option
                        viewModel.branchDropDownExpanded = false
                        viewModel.filterSearch()
                    })
            }
        }
    }
}

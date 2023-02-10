package com.mnnit.moticlubs.ui.components

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mnnit.moticlubs.network.model.UserClubModel
import com.mnnit.moticlubs.ui.activity.AppViewModel
import com.mnnit.moticlubs.ui.screens.ClubScreenViewModel

@Composable
fun SubscriptionConfirmationDialog(
    viewModel: ClubScreenViewModel, appViewModel: AppViewModel,
    subscribe: Boolean
) {
    val context = LocalContext.current

    ConfirmationDialog(
        showDialog = viewModel.showSubsDialog,
        message = "Are you sure you want to ${if (subscribe) "subscribe" else "unsubscribe"} ?",
        positiveBtnText = if (subscribe) "Subscribe" else "Unsubscribe",
        imageVector = if (subscribe) Icons.Rounded.NotificationsActive else Icons.Outlined.NotificationsOff,
        onPositive = {
            viewModel.progressText.value = if (subscribe) "Subscribing ..." else "Unsubscribing ..."
            viewModel.showProgress.value = true
            if (subscribe) {
                viewModel.subscribeToClub(viewModel.clubNavModel.clubId, {
                    appViewModel.user.subscribed.add(UserClubModel(viewModel.clubNavModel.clubId))
                    viewModel.showProgress.value = false
                    viewModel.subscribed.value =
                        appViewModel.user.subscribed.any { it.clubID == viewModel.clubNavModel.clubId }

                    viewModel.fetchSubscriberCount()
                    Toast.makeText(context, "Subscribed", Toast.LENGTH_SHORT).show()
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error could not process request", Toast.LENGTH_SHORT).show()
                }
            } else {
                viewModel.unsubscribeToClub(viewModel.clubNavModel.clubId, {
                    appViewModel.user.subscribed.removeIf { it.clubID == viewModel.clubNavModel.clubId }
                    viewModel.showProgress.value = false
                    viewModel.subscribed.value =
                        appViewModel.user.subscribed.any { it.clubID == viewModel.clubNavModel.clubId }

                    viewModel.fetchSubscriberCount()
                    Toast.makeText(context, "Unsubscribed", Toast.LENGTH_SHORT).show()
                }) {
                    viewModel.showProgress.value = false
                    Toast.makeText(context, "$it: Error could not process request", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

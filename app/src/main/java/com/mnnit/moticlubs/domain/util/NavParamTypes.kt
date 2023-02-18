package com.mnnit.moticlubs.domain.util

import android.os.Build
import android.os.Bundle
import androidx.navigation.NavType
import com.google.gson.Gson
import com.mnnit.moticlubs.data.network.dto.ClubDetailModel
import com.mnnit.moticlubs.data.network.dto.ImageUrl
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.PostNotificationModel
import com.mnnit.moticlubs.domain.model.User

class ClubParamType : NavType<Club>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Club? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, Club::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): Club =
        Gson().fromJson(value, Club::class.java)

    override fun put(bundle: Bundle, key: String, value: Club) {
        bundle.putParcelable(key, value)
    }
}

class ChannelParamType : NavType<Channel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): Channel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, Channel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): Channel =
        Gson().fromJson(value, Channel::class.java)

    override fun put(bundle: Bundle, key: String, value: Channel) {
        bundle.putParcelable(key, value)
    }
}

class UserParamType : NavType<User>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): User? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, User::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): User =
        Gson().fromJson(value, User::class.java)

    override fun put(bundle: Bundle, key: String, value: User) {
        bundle.putParcelable(key, value)
    }
}

class PostParamType : NavType<PostNotificationModel>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): PostNotificationModel? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, PostNotificationModel::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): PostNotificationModel =
        Gson().fromJson(value, PostNotificationModel::class.java)

    override fun put(bundle: Bundle, key: String, value: PostNotificationModel) {
        bundle.putParcelable(key, value)
    }
}

class ImageUrlParamType : NavType<ImageUrl>(isNullableAllowed = false) {

    override fun get(bundle: Bundle, key: String): ImageUrl? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, ImageUrl::class.java)
        } else {
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): ImageUrl =
        Gson().fromJson(value, ImageUrl::class.java)

    override fun put(bundle: Bundle, key: String, value: ImageUrl) {
        bundle.putParcelable(key, value)
    }
}

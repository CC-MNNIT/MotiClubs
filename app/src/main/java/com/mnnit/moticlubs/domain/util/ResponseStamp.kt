package com.mnnit.moticlubs.domain.util

object ResponseStamp {

    abstract class StampKey(private var keyValue: String = "") {

        fun withKey(key: String): StampKey {
            keyValue = "$keyValue:$key"
            return this
        }

        fun getKey(): String = keyValue
    }

    val NONE get() = object : StampKey() {}

    val ADMIN get() = object : StampKey("ADMIN") {}
    val CHANNEL get() = object : StampKey("CHANNEL") {}
    val CLUB get() = object : StampKey("CLUB") {}
    val MEMBER get() = object : StampKey("MEMBER") {}
    val POST get() = object : StampKey("POST") {}
    val REPLY get() = object : StampKey("REPLY") {}
    val URL get() = object : StampKey("URL") {}
    val USER get() = object : StampKey("USER") {}
}

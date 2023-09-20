package com.mnnit.moticlubs.domain.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.StateFactoryMarker
import kotlin.reflect.KProperty

interface Published<T> {
    var value: T
}

data class PublishedState<T>(
    private var _value: MutableState<T>,
) : Published<T> {
    override var value
        get() = _value.value
        set(value) {
            _value.value = value
        }
}

data class PublishedList<T>(
    private var _value: SnapshotStateList<T>,
) : Published<SnapshotStateList<T>> {
    override var value
        get() = _value
        set(value) {
            _value.clear()
            _value.addAll(value)
        }

    fun apply(list: List<T>) {
        _value.clear()
        _value.addAll(list)
    }
}

data class PublishedMap<K, V>(
    private var _value: SnapshotStateMap<K, V>,
) : Published<SnapshotStateMap<K, V>> {
    override var value
        get() = _value
        set(value) {
            _value.clear()
            _value.putAll(value)
        }
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Published<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> Published<T>.getValue(thisObj: Any?, property: KProperty<*>): T = value

@StateFactoryMarker
fun <T> publishedStateOf(value: T): PublishedState<T> = PublishedState(mutableStateOf(value))

@StateFactoryMarker
fun <T> publishedStateListOf(vararg values: T): PublishedList<T> = PublishedList(mutableStateListOf(*values))

@StateFactoryMarker
fun <K, V> publishedStateMapOf(): PublishedMap<K, V> = PublishedMap(mutableStateMapOf())

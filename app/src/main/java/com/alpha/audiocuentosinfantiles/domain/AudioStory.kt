package com.alpha.audiocuentosinfantiles.domain

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class AudioStory(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val url_image: String = ""
) : Serializable

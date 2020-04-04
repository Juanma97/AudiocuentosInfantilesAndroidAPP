package com.alpha.audiocuentosinfantiles

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AudioCuento(
    val id:Int = 0,
    val title:String = "",
    val description:String = "",
    val url:String = "")

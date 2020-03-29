package com.alpha.audiocuentosinfantiles

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AudioCuento(
    var id:Int = 0,
    var title:String = "",
    var description:String = "",
    var url:String = ""){
}
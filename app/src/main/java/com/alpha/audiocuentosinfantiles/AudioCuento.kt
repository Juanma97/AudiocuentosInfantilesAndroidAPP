package com.alpha.audiocuentosinfantiles

class AudioCuento(title:String, description: String, url:String) {
    var title:String = ""
    var description:String = ""
    var url:String = ""

    init {
        this.title = title
        this.description = description
        this.url = url
    }
}
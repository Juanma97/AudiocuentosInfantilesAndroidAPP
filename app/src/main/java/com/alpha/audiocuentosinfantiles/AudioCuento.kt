package com.alpha.audiocuentosinfantiles

class AudioCuento(id:Int, title:String, description:String, url:String) {
    var id:Int = 0
    var title:String = ""
    var description:String = ""
    var url:String = ""

    init {
        this.id = id
        this.title = title
        this.description = description
        this.url = url
    }
}

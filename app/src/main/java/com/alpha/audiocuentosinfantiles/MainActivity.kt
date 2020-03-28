package com.alpha.audiocuentosinfantiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.GridView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val containerView: GridView = findViewById(R.id.containerView)
        val items:ArrayList<AudioCuento> = ArrayList<AudioCuento>()
        items.add(AudioCuento("Titulo 1", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 2", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 3", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 4", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 5", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 6", "Descripcion 1", "URL"))
        items.add(AudioCuento("Titulo 7", "Descripcion 1", "URL"))
        val adapter = AudioCuentoAdapter(this, items)

        containerView.adapter = adapter
    }
}

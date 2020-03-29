package com.alpha.audiocuentosinfantiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val containerView: GridView = findViewById(R.id.containerView)

        val adapter = AudioCuentoAdapter(this, FirebaseHelper().retrieve())

        containerView.adapter = adapter


    }


}

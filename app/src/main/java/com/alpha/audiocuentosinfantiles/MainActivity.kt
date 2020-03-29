package com.alpha.audiocuentosinfantiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Network.networkWork(this)){
            val containerView: GridView = findViewById(R.id.containerView)
            val dataConnection:DataConnection = FirebaseHelper()
            val adapter = AudioCuentoAdapter(this, dataConnection.retrieve())

            containerView.adapter = adapter
        }
    }
}

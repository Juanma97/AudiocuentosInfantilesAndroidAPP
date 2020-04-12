package com.alpha.audiocuentosinfantiles.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.alpha.audiocuentosinfantiles.R

class NoNetworkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network)

        val btnBackToMainActivity: Button = findViewById(R.id.btnBackToMainActivity)
        btnBackToMainActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

package com.alpha.audiocuentosinfantiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val containerView: GridView = findViewById(R.id.containerView)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("audiocuentos")
        val items: ArrayList<AudioCuento> = ArrayList()


        var context = this

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    var acuento: AudioCuento = ds.getValue(AudioCuento::class.java)!!
                    items.add(acuento)
                }
                val adapter = AudioCuentoAdapter(context, items)
                containerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}

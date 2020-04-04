package com.alpha.audiocuentosinfantiles

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    var adapter: AudioCuentoAdapter? = null
    var containerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        containerView = findViewById(R.id.containerView)
        containerView?.setHasFixedSize(true)
        layoutManager = GridLayoutManager(this, 2)
        containerView?.layoutManager = layoutManager

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("audiocuentos")
        val items = ArrayList<AudioCuento>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    var acuento: AudioCuento = ds.getValue(AudioCuento::class.java)!!
                    items.add(acuento)
                }
                adapter = AudioCuentoAdapter(items, object: ClickListener{
                    override fun onItemClick(view: View, index: Int) {
                        Toast.makeText(applicationContext, "Mensaje", Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext, DetailsActivity::class.java))
                    }

                })
                containerView?.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FIREBASE", "Cancelled" + error.message)
            }
        })



        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView:SearchView = findViewById(R.id.searchView)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Buscar audiocuento..."

        searchView.setOnQueryTextFocusChangeListener{ v, hasFocus ->

        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                adapter?.filter(p0!!)
                adapter?.notifyDataSetChanged()
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })
    }
}

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


class MainActivity : AppCompatActivity() {

    var adapter: AudioCuentoAdapter? = null
    var containerView: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var items: ArrayList<AudioCuento> = ArrayList()
        items.add(AudioCuento(1, "Title1", "", ""))
        items.add(AudioCuento(2, "Title2", "", ""))
        items.add(AudioCuento(3, "Title3", "", ""))
        items.add(AudioCuento(4, "Title4", "", ""))
        items.add(AudioCuento(5, "Title5", "", ""))

        containerView = findViewById(R.id.containerView)
        containerView?.setHasFixedSize(true)
        layoutManager = GridLayoutManager(this, 2)
        containerView?.layoutManager = layoutManager

        //val dataConnection:DataConnection = FirebaseHelper()

        adapter = AudioCuentoAdapter(this, items, object: ClickListener{
            override fun onItemClick(view: View, index: Int) {
                Toast.makeText(applicationContext, "Mensaje", Toast.LENGTH_LONG).show()
                startActivity(Intent(applicationContext, DetailsActivity::class.java))
            }

        })
        containerView?.adapter = adapter

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

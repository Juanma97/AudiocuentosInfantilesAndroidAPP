package com.alpha.audiocuentosinfantiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AudioCuentoAdapter(var context:Context, items:ArrayList<AudioCuento>) : BaseAdapter() {

    var items:ArrayList<AudioCuento>? = null
    var itemsCopy:ArrayList<AudioCuento>? = null

    init {
        this.items = ArrayList(items)
        this.itemsCopy = items
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        var holder: ViewHolder? = null

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.audiocuento, null)
            holder = ViewHolder(view)
            view.tag = holder
        }else{
            holder = view.tag as? ViewHolder
        }

        val item = items?.get(p0) as? AudioCuento
        holder?.title?.text = item?.title

        return view!!
    }

    override fun getItem(p0: Int): Any {
        return items?.get(p0)!!
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return items?.count()!!
    }

    fun filter(str:String){
        items?.clear()

        if(str.isEmpty()){
            items = ArrayList(itemsCopy)
            notifyDataSetChanged()
            return
        }

        var busqueda = str
        busqueda = busqueda.toLowerCase()
        for(item in itemsCopy!!){
            val nombre = item.title.toLowerCase()
            if(nombre.contains(busqueda)){
                items?.add(item)
            }
        }
    }

    private class ViewHolder(view:View){
        var title:TextView? = null
        var description:TextView? = null

        init {
            title = view.findViewById(R.id.title)
            description = view.findViewById(R.id.description)
        }
    }
}
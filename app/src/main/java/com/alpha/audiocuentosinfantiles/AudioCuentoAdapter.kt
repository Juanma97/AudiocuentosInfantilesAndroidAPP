package com.alpha.audiocuentosinfantiles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioCuentoAdapter(items:ArrayList<AudioCuento>, var listener: ClickListener) : RecyclerView.Adapter<AudioCuentoAdapter.ViewHolder>() {

    var items:ArrayList<AudioCuento>? = null
    var itemsCopy:ArrayList<AudioCuento>? = null

    init {
        this.items = ArrayList(items)
        this.itemsCopy = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audiocuento, parent, false)
        val viewHolder = ViewHolder(view, listener)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        holder.title?.text = item?.title
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
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

    class ViewHolder(view: View, listener: ClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener{
        var view = view
        var title:TextView? = null
        var listener:ClickListener? = null

        init {
            title = this.view.findViewById(R.id.title)
            this.listener = listener
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onItemClick(v!!, adapterPosition)
        }

    }
}
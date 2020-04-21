package com.alpha.audiocuentosinfantiles.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alpha.audiocuentosinfantiles.domain.AudioStory
import com.alpha.audiocuentosinfantiles.R
import com.bumptech.glide.Glide

class AudioStoryAdapter(
    var context: Context,
    items: ArrayList<AudioStory>,
    var listener: ClickListener
) : RecyclerView.Adapter<AudioStoryAdapter.ViewHolder>() {

    var items: ArrayList<AudioStory>? = null
    var itemsCopy: ArrayList<AudioStory>? = null

    init {
        this.items = ArrayList(items)
        this.itemsCopy = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audiocuento, parent, false)
        val viewHolder =
            ViewHolder(
                view,
                listener
            )
        return viewHolder
    }

    override fun getItemCount(): Int {
        return items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        holder.title?.text = item?.title
        if(!item?.url_image?.isEmpty()!!){
            Glide.with(context).load(item?.url_image).into(holder.image as ImageView)
        }else{
            Glide.with(context).load(R.drawable.music_disk).into(holder.image as ImageView)
        }
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    fun filter(str: String) {
        items?.clear()

        if (str.isEmpty()) {
            items = ArrayList(itemsCopy)
            notifyDataSetChanged()
            return
        }

        var busqueda = str
        busqueda = busqueda.toLowerCase()
        for (item in itemsCopy!!) {
            val nombre = item.title.toLowerCase()
            if (nombre.contains(busqueda)) {
                items?.add(item)
            }
        }
    }

    class ViewHolder(view: View, listener: ClickListener) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var view = view
        var title: TextView? = null
        var image: ImageView? = null
        var listener: ClickListener? = null

        init {
            title = this.view.findViewById(R.id.title)
            image = this.view.findViewById(R.id.audiocuento_item_image)
            this.listener = listener
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            this.listener?.onItemClick(v!!, adapterPosition)
        }

    }
}
package com.oashwe.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_photo_view.view.*
import kotlinx.android.synthetic.main.photo_view.view.*

class PhotoViewAdapter:ListAdapter<PhotoItem,PhotoViewHolder>(DIFFCALLBACK) {
    object DIFFCALLBACK:DiffUtil.ItemCallback<PhotoItem>() {
        override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
            return oldItem.photoId == newItem.photoId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        //LayoutInflater.from(parent.context).inflate(R.layout.fragment_photo_view,parent,false).apply {
        //第一次错在加载错了layout，该用作为适配器的view，而不是要用adapter的fragment
        LayoutInflater.from(parent.context).inflate(R.layout.photo_view,parent,false).apply {
            return PhotoViewHolder(this)
        }
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(getItem(position).fullUrl)
            .placeholder(R.drawable.viewphoto_placeholder)
            .into(holder.itemView.pagerPhoto)
    }
}
class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
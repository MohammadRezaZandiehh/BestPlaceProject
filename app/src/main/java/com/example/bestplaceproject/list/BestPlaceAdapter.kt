package com.example.bestplaceproject.list

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bestplaceproject.BestPlace
import com.example.bestplaceproject.R
import com.example.bestplaceproject.listener.OnRecyclerViewItemClicked
import java.io.File

class BestPlaceAdapter(var context: Context, var list: ArrayList<BestPlace>) :
    RecyclerView.Adapter<BestPlaceAdapter.BestPlaceViewHolder>() {
    var onRecyclerViewItemClicked: OnRecyclerViewItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestPlaceViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false)
        return BestPlaceViewHolder(view)
    }


    override fun onBindViewHolder(holder: BestPlaceViewHolder, position: Int) {
        var bestPlace = list[position]
        holder.textViewTitle.text = bestPlace.title

        var imageBitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver, Uri.fromFile(
                File(bestPlace.image)
            )
        )
        holder.imageViewPlace.setImageBitmap(imageBitmap)

        holder.itemView.setOnClickListener {
            onRecyclerViewItemClicked?.onBestPlaceItemClicked(position, bestPlace)
        }
    }


    override fun getItemCount() = list.size


    class BestPlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewTitle: TextView
        var imageViewPlace: ImageView

        init {
            textViewTitle = itemView.findViewById(R.id.textViewTitle)
            imageViewPlace = itemView.findViewById(R.id.imageViewPlace)
        }
    }

    fun setRecyclerViewItemClicked(onRecyclerViewItemClicked: OnRecyclerViewItemClicked) {
        this.onRecyclerViewItemClicked = onRecyclerViewItemClicked
    }


    fun updateAdapter (list: ArrayList<BestPlace>){
        this.list = list
        notifyDataSetChanged()
    }
}
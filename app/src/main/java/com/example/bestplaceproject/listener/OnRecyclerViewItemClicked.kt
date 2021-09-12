package com.example.bestplaceproject.listener

import com.example.bestplaceproject.BestPlace
import com.example.bestplaceproject.list.BestPlaceAdapter
import java.text.FieldPosition

interface OnRecyclerViewItemClicked {
    fun onBestPlaceItemClicked(position: Int, bestPlace: BestPlace)

}
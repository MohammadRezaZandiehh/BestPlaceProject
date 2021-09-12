package com.example.bestplaceproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bestplaceproject.databinding.ActivityMainBinding
import com.example.bestplaceproject.list.BestPlaceAdapter
import com.example.bestplaceproject.listener.OnRecyclerViewItemClicked
import com.example.bestplaceproject.listener.SwipeToDeleteCallback
import com.example.bestplaceproject.listener.SwipeToEditCallback

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
//    lateinit var bestPlaceAdapter: BestPlaceAdapter

    companion object {
        const val REQUEST_CODE_PLACE = 12123
        const val REQUEST_EDIT_PLACE = 12122
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this@MainActivity))
        setContentView(binding.root) // element e pedar ro mide  yani dar inja FreamLayout k too activity_main root e mast

        setSupportActionBar(binding.toolbarMain)

        setUpRecyclerView()

        binding.fabAddPlaceActivity.setOnClickListener {
            val intentToAddPlaceActivity = Intent(this@MainActivity, AddPlaceActivity::class.java)
            startActivityForResult(intentToAddPlaceActivity, REQUEST_CODE_PLACE)

        }
    }

    private fun setUpRecyclerView() {
        var db = DatabaseHelper(this)


        var bestPlaceAdapter = BestPlaceAdapter(this, db.getAllPlaces())
        binding.recyclerViewMain.adapter = bestPlaceAdapter
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)

//        var onRecyclerViewItemClicked = object : OnRecyclerViewItemClicked{
//            override fun onBestPlaceItemClicked(position: Int, bestPlace: BestPlace) {
//
//            }
//
//        }
        // fek konam mese bala ram mishod nevesht b jaye paein .
        bestPlaceAdapter.setRecyclerViewItemClicked(object : OnRecyclerViewItemClicked {
            override fun onBestPlaceItemClicked(position: Int, bestPlace: BestPlace) {
                val detailIntent = Intent(this@MainActivity, DetailsActivity::class.java)
                detailIntent.putExtra("bestplace", bestPlace)
                startActivity(detailIntent)
            }

        })


        val editSwipeCallback =
            object : SwipeToEditCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                viewHolder.absoluteAdapterPosition //int 0 1 2
//                bestPlaceAdapter.list.get(viewHolder.absoluteAdapterPosition).title //bestPlace
                    val id =
                        bestPlaceAdapter.list.get(viewHolder.absoluteAdapterPosition).id //bestPlace
//// // // // // // // // // // // // // // // // // // // -1 ro khodam gozashtm on nazasht.


                    val editIntent = Intent(this@MainActivity, AddPlaceActivity::class.java)
                    editIntent.putExtra("id", id)
                    // ba khate bala id e entekhab shode ro mibarim b AddPlaceActivity :)
                    startActivityForResult(editIntent, REQUEST_EDIT_PLACE)
                }

            }
        var itemTouchHelper = ItemTouchHelper(editSwipeCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewMain)


        var deleteSwipeCallback =
            object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val id = bestPlaceAdapter.list.get(viewHolder.absoluteAdapterPosition).id
                    val db = DatabaseHelper(this@MainActivity)
                    if (db.deletePlace(id)) {
                        updateRecyclerViewAdapter()
                    } else {
                        Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        var itemDeleteTouchHelper = ItemTouchHelper(deleteSwipeCallback)
        itemDeleteTouchHelper.attachToRecyclerView(binding.recyclerViewMain)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PLACE) {

                updateRecyclerViewAdapter()
            }



            if (requestCode == REQUEST_EDIT_PLACE) {
                updateRecyclerViewAdapter()

            }
        }
    }


    private fun updateRecyclerViewAdapter() {
        var db = DatabaseHelper(this)
        var adapter = binding.recyclerViewMain.adapter as BestPlaceAdapter
        adapter.updateAdapter(db.getAllPlaces())
    }
}
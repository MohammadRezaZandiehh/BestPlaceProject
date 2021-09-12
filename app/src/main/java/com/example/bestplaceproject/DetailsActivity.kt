package com.example.bestplaceproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        var data = intent?.extras?.get("bestplace") as BestPlace
        Toast.makeText(this, data.data, Toast.LENGTH_SHORT).show()
    }
}
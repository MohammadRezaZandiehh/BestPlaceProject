package com.example.bestplaceproject

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class BestPlace(
    val title: String,
    val image: String,
    val description: String,
    val data: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
):Parcelable {
    var id:String? = null

    init {
        this.id = UUID.randomUUID().toString()
    }
    //blocke bala :har objecti k az in class sakhte mishe , dar hengame sakhtesh ye id ham b tore khodkar vasash sakhte she .
}
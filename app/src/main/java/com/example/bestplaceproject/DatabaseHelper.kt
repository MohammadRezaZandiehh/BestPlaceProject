package com.example.bestplaceproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.net.IDN

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "bestplace", null, 1) {


    companion object {
        const val TABLE_NAME = "bestplacetable"
        const val KEY_ID = "KEY_ID"
        const val KEY_TITLE = "_title"
        const val KEY_IMAGE = "_image"
        const val KEY_DESCRIPTION = "_description"
        const val KEY_DATE = "_date"
        const val KEY_LOCATION = "_location"
        const val KEY_LATITUDE = "_latitude"
        const val KEY_LONGITUDE = "_longitude"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        var createTableBestPlace = ("create table " + TABLE_NAME + " ( "
                + KEY_ID + " text primary key, "
                + KEY_TITLE + " text, "
                + KEY_IMAGE + " text, "
                + KEY_DESCRIPTION + " text, "
                + KEY_DATE + " text, "
                + KEY_LOCATION + " text, "
                + KEY_LATITUDE + " text, "
                + KEY_LONGITUDE + " text )")

        db?.execSQL(createTableBestPlace)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists ${TABLE_NAME}")
        onCreate(db)
    }


//fun open
//fun close

    fun addPlace(bestPlace: BestPlace) {
        //mire ye databasi b ma mide k writable ast yani ghabele neveshtane in databas e sakhte shode .
        var writabledatabase = this.writableDatabase
        var contentValue = ContentValues()

        contentValue.put(KEY_ID, bestPlace.id)
        contentValue.put(KEY_TITLE, bestPlace.title)
        contentValue.put(KEY_IMAGE, bestPlace.image)
        contentValue.put(KEY_DESCRIPTION, bestPlace.description)
        contentValue.put(KEY_DATE, bestPlace.data)
        contentValue.put(KEY_LOCATION, bestPlace.location)
        contentValue.put(KEY_LATITUDE, bestPlace.latitude)
        contentValue.put(KEY_LONGITUDE, bestPlace.longitude)

//SQLiteDatabase.CONFLICT_REPLACE ::: miad mibine in id ro k ghablan dashtim !, pas miad in object ba in id ro replace mikone jaye object ghablie ba hamon id (id ro taghir nmide faghat object ha ro jaygozin e ham dg mikone :)
        // dar natije ghabeliat e update kardn e objecct haro faraham mikonen .
        writabledatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            contentValue,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        writabledatabase.close()
    }


    fun getPlaceById(id: String): BestPlace {
        var getQuery = "select * from ${TABLE_NAME} where KEY_ID = '$id'"
        var db = this.readableDatabase
        var cursor = db.rawQuery(getQuery, null)
        cursor.moveToNext()
        var bestPlace = BestPlace(
            cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
            cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
            cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(KEY_DATE)),
            cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
            cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)).toDouble(),
            cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)).toDouble()
        )
        bestPlace.id = cursor.getString(cursor.getColumnIndex(KEY_ID))
        return bestPlace

    }


    fun getAllPlaces(): ArrayList<BestPlace> {
        var getAllQuery = "select * from ${TABLE_NAME}"
        var db = this.readableDatabase
//readableDatabase : database ro baz mikone va mikhonatesh.
//vali dar balaye safhe k neveshtim  writable database ro baz mikone ta tosh ye chizi benevisim.

        var bestPlaceArray = ArrayList<BestPlace>()

        try {
            var cursor = db.rawQuery(getAllQuery, null)
            // cursor dar jadvale database : --> "be ye doone ghabl az khodesh eshare mikone ", vase hamin azz method e moveToNext estefade mikonim ta vaghti k (while) badesh dg chizi nabashe .

            while (cursor.moveToNext()) {

                var bestPlace = BestPlace(
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                    cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                    cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)).toDouble(),
                    cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)).toDouble()
                )
                bestPlace.id = cursor.getString(cursor.getColumnIndex(KEY_ID))
                bestPlaceArray.add(bestPlace)
            }
            if (!cursor.isClosed) cursor.close()

        } catch (ex: SQLiteException) {
            ex.printStackTrace()
        }
        db.close()

        return bestPlaceArray
    }


    fun deletePlace(id: String?): Boolean {
        if (id.isNullOrEmpty()) return false

        val db = this.writableDatabase //open
        val result = db.delete(TABLE_NAME, "$KEY_ID=?", arrayOf(id))
        db.close()


        return result == 1

    }
}
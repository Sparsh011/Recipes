package com.example.recipes.model.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "fav_dishes_table")
data class FavDish (
    @ColumnInfo val image: String,
    @ColumnInfo (name = "image_source") val imageSource: String,
    @ColumnInfo val title: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val ingredients: String,

    @ColumnInfo(name = "cooking_time") val  cookingTime: String,
    @ColumnInfo(name = "instructions") val directionToCook: String,
    @ColumnInfo(name = "favourite_dish") var favouriteDish: Boolean = false, // true -> 1, false -> 0. Will be used to retrieve favDish
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable





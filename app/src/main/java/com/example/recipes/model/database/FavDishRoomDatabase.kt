package com.example.recipes.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.recipes.model.entities.FavDish
import kotlin.synchronized as synchronized

@Database(entities = [FavDish::class], version = 1)
abstract class FavDishRoomDatabase : RoomDatabase() {

    abstract fun favDishDao() : FavDishDAO

    companion object{
        @Volatile // @Volatile means that writes to this field are immediately made visible to other threads. We need it to make sure that multiple threads always see the newest value, even when the cache system or compiler optimizations are at work. Reading from a volatile variable always returns the latest written value from this variable.
        private var INSTANCE : FavDishRoomDatabase? = null

        fun getDatabase(context: Context): FavDishRoomDatabase{

//            If the instance is null, then create the database, and if it is not null, then return it.
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDatabase::class.java,
                    "fav_dish_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
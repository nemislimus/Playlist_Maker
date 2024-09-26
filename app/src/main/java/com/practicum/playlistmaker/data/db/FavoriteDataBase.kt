package com.practicum.playlistmaker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.entity.TrackEntity


@Database(entities = [TrackEntity::class], version = 1)
abstract class FavoriteDataBase: RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    companion object {

        fun getFavoriteDatabase(context: Context): FavoriteDataBase {
            return Room.databaseBuilder(
                context,
                FavoriteDataBase::class.java,
                "favorite_database.db"
            )
                .fallbackToDestructiveMigration() //пока специально оставил
                .build()
        }

    }
}
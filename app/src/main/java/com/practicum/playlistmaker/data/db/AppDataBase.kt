package com.practicum.playlistmaker.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.data.db.dao.FavoriteTrackDao
import com.practicum.playlistmaker.data.db.entity.TrackEntity


@Database(entities = [TrackEntity::class], version = 1)
abstract class AppDataBase: RoomDatabase() {

    abstract fun favoriteTrackDao(): FavoriteTrackDao

    companion object {

        fun getAppDatabase(context: Context): AppDataBase {
            return Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                "favorite_database.db"
            )
                .build()
        }

    }
}
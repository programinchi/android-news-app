package com.example.ch10.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = true          // JSON skema -> app/schemas/ (lihat build.gradle.kts)
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    companion object {
        const val NAME = "news_database"

        // TODO [T2.1] Hapus getInstance() setelah DatabaseModule dibuat.
        //  @Singleton di Hilt sudah menjamin hanya ada satu instance.
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getInstance(context: Context): NewsDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    NAME
                )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

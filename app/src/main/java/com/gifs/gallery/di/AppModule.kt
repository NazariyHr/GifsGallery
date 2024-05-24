package com.gifs.gallery.di

import android.content.Context
import androidx.room.Room
import com.gifs.gallery.data.local.GifsDatabase
import com.gifs.gallery.data.remote.GiphyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideGiphyApi(): GiphyApi {
        return Retrofit.Builder()
            .baseUrl(GiphyApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideGifsDatabase(
        @ApplicationContext context: Context
    ): GifsDatabase {
        return Room.databaseBuilder(
            context,
            GifsDatabase::class.java,
            "gifs.db"
        ).build()
    }
}
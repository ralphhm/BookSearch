package de.rhm.booksearch.di

import android.app.Application
import dagger.Module
import dagger.Provides
import de.rhm.booksearch.api.OpenLibraryService
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application) = application

    @Provides
    @Singleton
    fun provideOpenLibraryService(): OpenLibraryService = Retrofit.Builder()
            .baseUrl("http://openlibrary.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build().create(OpenLibraryService::class.java)

}
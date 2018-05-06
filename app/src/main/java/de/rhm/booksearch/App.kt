package de.rhm.booksearch

import android.app.Activity
import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import de.rhm.booksearch.di.DaggerAppComponent
import javax.inject.Inject

class App: Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)
        Fresco.initialize(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector

}
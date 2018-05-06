package de.rhm.booksearch.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.rhm.booksearch.BookSearchActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindBookSearchActivity(): BookSearchActivity

}
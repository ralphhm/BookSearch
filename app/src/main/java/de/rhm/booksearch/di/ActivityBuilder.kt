package de.rhm.booksearch.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.rhm.booksearch.BookSearchActivity
import de.rhm.booksearch.book.BookDetailsActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindBookSearchActivity(): BookSearchActivity

    @ContributesAndroidInjector
    abstract fun bindBookDetailsActivity(): BookDetailsActivity

}
package de.rhm.booksearch.book

import android.arch.lifecycle.MutableLiveData
import de.rhm.booksearch.api.model.Book

class SelectedBook {

    val selectedBook = MutableLiveData<Book>()

    fun select(book: Book) {
        selectedBook.value = book
    }

}
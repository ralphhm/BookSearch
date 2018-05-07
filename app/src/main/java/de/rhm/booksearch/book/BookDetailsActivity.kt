package de.rhm.booksearch.book

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import de.rhm.booksearch.R
import de.rhm.booksearch.api.model.Book
import kotlinx.android.synthetic.main.activity_book_details.*
import kotlinx.android.synthetic.main.content_book_details.*
import javax.inject.Inject

class BookDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var selectedBook: SelectedBook

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        selectedBook.selectedBook.observe(this, Observer {
            bind(it!!)
        })
    }

    private fun bind(book: Book) {
        cover_image.setImageURI(book.largeCoverUrl)
        supportActionBar?.title = book.title
        author_names.editText?.setText(book.authorNames?.joinToString(", "))
        publish_date.editText?.setText(book.publishDates?.joinToString(", "))
    }

}

package de.rhm.booksearch.book

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection
import de.rhm.booksearch.FullScreenToggler
import de.rhm.booksearch.R
import de.rhm.booksearch.api.model.Book
import kotlinx.android.synthetic.main.activity_book_details.*
import kotlinx.android.synthetic.main.content_book_details.*
import javax.inject.Inject

class BookDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var selectedBook: SelectedBook
    private lateinit var toggler: FullScreenToggler

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggler = FullScreenToggler(supportActionBar!!, cover_image, author_names, publish_date, scrim)
        selectedBook.observe(this, Observer {
            bind(it!!)
        })
    }

    private fun bind(book: Book) {
        cover_image.setImageURI(book.largeCoverUrl)
        supportActionBar?.title = book.title
        author_names.editText?.setText(book.authorNames?.joinToString(", "))
        publish_date.editText?.setText(book.publishDates?.joinToString(", "))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        toggler.delayedHide(2000)
    }

}

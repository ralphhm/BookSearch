package de.rhm.booksearch

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import dagger.android.AndroidInjection
import de.rhm.booksearch.SearchUiState.*
import de.rhm.booksearch.api.model.Book
import de.rhm.booksearch.book.BookDetailsActivity
import de.rhm.booksearch.book.SelectedBook
import de.rhm.booksearch.di.TypedViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_book_search.*
import kotlinx.android.synthetic.main.content_book_search.*
import kotlinx.android.synthetic.main.item_book.*
import kotlinx.android.synthetic.main.item_info.*
import kotlinx.android.synthetic.main.item_loading.*
import javax.inject.Inject

class BookSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewHolderFactory: TypedViewModelFactory<SearchViewModel>
    private val disposable = CompositeDisposable()
    private val section = Section()
    @Inject
    lateinit var selectedBook: SelectedBook

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        setSupportActionBar(toolbar)
        content.adapter = GroupAdapter<ViewHolder>().apply {
            add(section)
            setOnItemClickListener { item, _ ->  (item as? BookItem)?.let {
                selectedBook.select(item.book)
                startActivity(Intent(this@BookSearchActivity, BookDetailsActivity::class.java))
            }}
        }
        with(ViewModelProviders.of(this, viewHolderFactory).get(SearchViewModel::class.java)) {
            uiState.subscribe { updateUi(it) }.let { disposable.add(it) }
            RxSearchView.queryTextChangeEvents(search_view)
                    .filter { it.isSubmitted }
                    .doOnNext { it.view().clearFocus() }
                    .map { SearchUiAction(it.queryText().toString()) }
                    .subscribe { publishSubject.onNext(it) }
                    .let { disposable.add(it) }
        }
    }

    private fun updateUi(state: SearchUiState) = when (state) {
        is Initial -> showPlaceholder(InfoItem(getString(R.string.instruct_search)))
        is Result -> {
            section.update(state.books.map { BookItem(it) })
            content.scrollToPosition(0)
        }
        is Failure -> showPlaceholder(InfoItem(state.message))
        is EmptyResult -> showPlaceholder(InfoItem(getString(R.string.no_books_found, state.query)))
        is SearchUiState.Loading -> showPlaceholder(LoadingItem(getString(R.string.searching_for, state.query)))
    }

    private fun showPlaceholder(item: Item) = with(section) {
        setPlaceholder(item)
        update(emptyList())
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

}

class BookItem(val book: Book) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) = with(viewHolder) {
        author_name.text = book.authorNames?.joinToString(", ")
        book_title.text = book.title
        cover_image.setImageURI(book.smallCoverUrl)
    }

    override fun getLayout() = R.layout.item_book

}

class InfoItem(private val text: String) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.info.text = text
    }

    override fun getLayout() = R.layout.item_info

}

class LoadingItem(private val info: String) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.loading_info.text = info
    }

    override fun getLayout() = R.layout.item_loading

}
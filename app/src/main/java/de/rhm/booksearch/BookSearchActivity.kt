package de.rhm.booksearch

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import dagger.android.AndroidInjection
import de.rhm.booksearch.SearchUiState.*
import de.rhm.booksearch.api.model.Book
import de.rhm.booksearch.di.TypedViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_book_search.*
import kotlinx.android.synthetic.main.content_book_search.*
import kotlinx.android.synthetic.main.item_book.*
import javax.inject.Inject

class BookSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewHolderFactory: TypedViewModelFactory<SearchViewModel>
    private val disposable = CompositeDisposable()
    private val section = Section()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        setSupportActionBar(toolbar)
        search_result.adapter = GroupAdapter<ViewHolder>().apply {
            add(section)
        }
        with(ViewModelProviders.of(this, viewHolderFactory).get(SearchViewModel::class.java)){
            uiState.subscribe { updateUi(it) }.let { disposable.add(it) }
            RxSearchView.queryTextChangeEvents(search_view)
                    .filter{it.isSubmitted}
                    .map { SearchUiAction(it.queryText().toString()) }
                    .subscribe{publishSubject.onNext(it)}
                    .let { disposable.add(it) }
        }
    }

    private fun updateUi(state: SearchUiState) {
        when(state) {
            is Result -> section.update(state.books.map { BookItem(it) })
            is Failure -> loading_error.text = state.message
            is EmptyResult -> empty_result.text = getString(R.string.no_books_found, state.query)
        }
        content.onlyShow(stateToContentView(state))
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun stateToContentView(state: SearchUiState) = when(state) {
        Initial -> initial_content
        Loading -> loading_content
        is Result -> search_result
        is Failure -> loading_error
        is EmptyResult -> empty_result
    }

}

private fun ViewGroup.onlyShow(view: View) = onlyShow(view.id)

private fun ViewGroup.onlyShow(viewId: Int) = views.forEach{ view ->
    view.visibility = if(view.id == viewId) VISIBLE else GONE
}

private val ViewGroup.views get() = (0 until childCount).map{getChildAt(it)}

class BookItem(private val book: Book): Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) = with(viewHolder) {
        author_name.text = book.authorNames?.joinToString(", ")
        book_title.text = book.title
        cover_image.setImageURI(book.smallCoverUrl)
    }

    override fun getLayout() = R.layout.item_book

}
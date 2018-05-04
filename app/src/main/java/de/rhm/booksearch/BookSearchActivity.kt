package de.rhm.booksearch

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import de.rhm.booksearch.BookSearchUiState.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_book_search.*
import kotlinx.android.synthetic.main.content_book_search.*

class BookSearchActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)
        setSupportActionBar(toolbar)
        with(ViewModelProviders.of(this).get(BookSearchViewModel::class.java)){
            uiState.subscribe { updateUi(it) }.let { disposable.add(it) }
            RxSearchView.queryTextChanges(search_view)
                    .skipInitialValue()
                    .map { BookSearchUiAction("$it") }
                    .subscribe{publishSubject.onNext(it)}
                    .let { disposable.add(it) }
        }
    }

    private fun updateUi(state: BookSearchUiState) {
        content.onlyShow(stateToContentView(state))
        when(state) {
            is Failure -> loading_error.text = state.message
            is EmptyResult -> empty_result.text = "Didn't find books for \"${state.query}\""
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    private fun stateToContentView(state: BookSearchUiState) = when(state) {
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

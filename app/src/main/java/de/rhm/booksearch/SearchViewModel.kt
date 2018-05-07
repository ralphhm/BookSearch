package de.rhm.booksearch

import android.arch.lifecycle.ViewModel
import de.rhm.booksearch.api.OpenLibraryService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class SearchViewModel @Inject constructor(apiService: OpenLibraryService) : ViewModel() {
    val publishSubject = PublishSubject.create<SearchUiAction>()
    val uiState: Observable<out SearchUiState> = publishSubject
            .switchMap { action ->
                when (action.criterion) {
                    Author -> apiService.searchBooks(author = action.query)
                    Title -> apiService.searchBooks(title = action.query)
                }.toObservable().map<SearchUiState> { result ->
                    when {
                        result.books.isEmpty() -> SearchUiState.EmptyResult(action.query)
                        else -> SearchUiState.Result(result.books)
                    }
                }.startWith(SearchUiState.Loading(action.query)).onErrorReturn { SearchUiState.Failure(action.query, it) }
            }
            .startWith(SearchUiState.Initial)
            .replay(1)
            .autoConnect()
            .observeOn(AndroidSchedulers.mainThread())
}
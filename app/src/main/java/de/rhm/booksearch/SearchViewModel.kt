package de.rhm.booksearch

import android.arch.lifecycle.ViewModel
import de.rhm.booksearch.api.OpenLibraryService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SearchViewModel: ViewModel() {
    private val apiService = Retrofit.Builder()
            .baseUrl("http://openlibrary.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build().create(OpenLibraryService::class.java)
    val publishSubject = PublishSubject.create<SearchUiAction>()
    val uiState: Observable<out SearchUiState> = publishSubject
            .distinctUntilChanged()
            .switchMap { action ->
                apiService.searchBooks(action.query).toObservable()
                        .map<SearchUiState> { result ->
                            when {
                                result.books.isEmpty() -> SearchUiState.EmptyResult(action.query)
                                else -> SearchUiState.Result(result.books)
                            }
                        }
                        .startWith(SearchUiState.Loading)
                        .onErrorReturn { SearchUiState.Failure(it.localizedMessage) }
            }
            .startWith(SearchUiState.Initial)
            .replay(1)
            .autoConnect()
            .observeOn(AndroidSchedulers.mainThread())
}
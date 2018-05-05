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

class BookSearchViewModel: ViewModel() {
    private val apiService = Retrofit.Builder()
            .baseUrl("http://openlibrary.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build().create(OpenLibraryService::class.java)
    val publishSubject = PublishSubject.create<BookSearchUiAction>()
    val uiState: Observable<out BookSearchUiState> = publishSubject
            .distinctUntilChanged()
            .switchMap { action ->
                apiService.searchBooks(action.query).toObservable()
                        .map<BookSearchUiState> {result ->
                            when {
                                result.books.isEmpty() -> BookSearchUiState.EmptyResult(action.query)
                                else -> BookSearchUiState.Result()
                            }
                        }
                        .startWith(BookSearchUiState.Loading)
                        .onErrorReturn { BookSearchUiState.Failure(it.localizedMessage) }
            }
            .startWith(BookSearchUiState.Initial)
            .replay(1)
            .autoConnect()
            .observeOn(AndroidSchedulers.mainThread())
}
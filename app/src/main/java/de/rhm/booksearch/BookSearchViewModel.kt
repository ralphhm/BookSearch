package de.rhm.booksearch

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class BookSearchViewModel: ViewModel() {
    val publishSubject = PublishSubject.create<BookSearchUiAction>()
    val uiState: Observable<out BookSearchUiState> = publishSubject
            .distinctUntilChanged()
            .switchMap { Observable.just<BookSearchUiState>(BookSearchUiState.Loading, BookSearchUiState.EmptyResult(it.query)).zipWith(Observable.interval(5, TimeUnit.SECONDS), BiFunction<BookSearchUiState, Long, BookSearchUiState> { t1, _ -> t1 }) }
            .startWith(BookSearchUiState.Initial)
            .replay(1)
            .autoConnect()
            .observeOn(AndroidSchedulers.mainThread())
}
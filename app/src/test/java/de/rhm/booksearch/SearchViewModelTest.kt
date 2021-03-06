package de.rhm.booksearch

import com.nhaarman.mockito_kotlin.*
import de.rhm.booksearch.api.OpenLibraryService
import de.rhm.booksearch.api.model.Book
import de.rhm.booksearch.api.model.SearchResult
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

const val query = "Test"

class SearchViewModelTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        }
    }

    val service = mock<OpenLibraryService>()
    val searchViewModel = SearchViewModel(service)
    val uiStateObserver = TestObserver<SearchUiState>()
    val searchUiAction = SearchUiAction(query, Title)

    @Before
    fun setupTest() {
        searchViewModel.uiState.subscribe(uiStateObserver)
    }

    @Test
    fun initialSubscription_returnsInitState() {
        uiStateObserver.assertValue(SearchUiState.Initial)
    }

    @Test
    fun searchAction_triggersBookSearch() {
        searchViewModel.publishSubject.onNext(searchUiAction)
        verify(service).searchBooks(eq(query), eq(null))
    }

    @Test
    fun searchAction_triggersLoadingState() {
        whenever(service.searchBooks(anyOrNull(), anyOrNull())).thenReturn(Single.never())
        searchViewModel.publishSubject.onNext(searchUiAction)
        uiStateObserver.assertLastValue(SearchUiState.Loading(query))
    }

    @Test
    fun searchAction_triggersEmptyState_whenResultIsEmpty() {
        whenever(service.searchBooks(anyOrNull(), anyOrNull())).thenReturn(Single.just(SearchResult(emptyList())))
        searchViewModel.publishSubject.onNext(searchUiAction)
        uiStateObserver.assertLastValue(SearchUiState.EmptyResult(query))
    }

    @Test
    fun searchAction_triggersResultState_whenResultIsNotEmpty() {
        val books = listOf(Book())
        whenever(service.searchBooks(anyOrNull(), anyOrNull())).thenReturn(Single.just(SearchResult(books)))
        searchViewModel.publishSubject.onNext(searchUiAction)
        uiStateObserver.assertLastValue(SearchUiState.Result(books))
    }

    @Test
    fun searchAction_triggersFailureState_whenException() {
        val exception = Exception()
        whenever(service.searchBooks(anyOrNull(), anyOrNull())).thenReturn(Single.error(exception))
        searchViewModel.publishSubject.onNext(searchUiAction)
        uiStateObserver.assertLastValue(SearchUiState.Failure(query, exception))
    }

    @Test
    fun subscription_triggersLastState_whenResubscribed() {
        val books = listOf(Book())
        whenever(service.searchBooks(anyOrNull(), anyOrNull())).thenReturn(Single.just(SearchResult(books)))
        searchViewModel.publishSubject.onNext(searchUiAction)
        TestObserver<SearchUiState>().also {
            searchViewModel.uiState.subscribe(it)
        }.assertLastValue(SearchUiState.Result(books))
    }
}

fun <T> TestObserver<T>.assertLastValue(value: T) = assertValueAt(valueCount() - 1, value)
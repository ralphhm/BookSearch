package de.rhm.booksearch

import de.rhm.booksearch.api.model.Book

sealed class SearchUiState {
    object Initial: SearchUiState()
    class Loading(val query: String) : SearchUiState()
    class Result(val books: List<Book>) : SearchUiState()
    class EmptyResult(val query: String): SearchUiState()
    class Failure(val query: String, val throwable: Throwable): SearchUiState()
}
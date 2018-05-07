package de.rhm.booksearch

import de.rhm.booksearch.api.model.Book

sealed class SearchUiState {
    object Initial: SearchUiState()
    data class Loading(val query: String) : SearchUiState()
    data class Result(val books: List<Book>) : SearchUiState()
    data class EmptyResult(val query: String): SearchUiState()
    data class Failure(val query: String, val throwable: Throwable): SearchUiState()
}
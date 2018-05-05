package de.rhm.booksearch

import de.rhm.booksearch.api.Book

sealed class BookSearchUiState {
    object Initial: BookSearchUiState()
    object Loading : BookSearchUiState()
    class Result(val books: List<Book>) : BookSearchUiState()
    class EmptyResult(val query: String): BookSearchUiState()
    class Failure(val message: String): BookSearchUiState()
}
package de.rhm.booksearch

sealed class BookSearchUiState {
    object Initial: BookSearchUiState()
    object Loading : BookSearchUiState()
    class Result : BookSearchUiState()
    class EmptyResult(val query: String): BookSearchUiState()
    class Failure(val message: String): BookSearchUiState()
}
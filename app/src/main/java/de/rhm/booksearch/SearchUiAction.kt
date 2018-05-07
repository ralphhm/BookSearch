package de.rhm.booksearch

data class SearchUiAction(val query: String, val criterion: Criterion)

sealed class Criterion
object Title: Criterion()
object Author: Criterion()
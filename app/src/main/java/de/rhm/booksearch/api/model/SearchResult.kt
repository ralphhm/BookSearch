package de.rhm.booksearch.api.model

import com.google.gson.annotations.SerializedName

class SearchResult(@SerializedName("docs") val books: List<Book>)
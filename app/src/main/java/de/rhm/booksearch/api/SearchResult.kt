package de.rhm.booksearch.api

import com.google.gson.annotations.SerializedName

class SearchResult(@SerializedName("docs") val books: List<Book>)
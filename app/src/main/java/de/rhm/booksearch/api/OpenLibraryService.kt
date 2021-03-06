package de.rhm.booksearch.api

import de.rhm.booksearch.api.model.SearchResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {

    @GET("search.json")
    fun searchBooks(
            @Query("title") title: String? = null,
            @Query("author") author: String? = null
    ): Single<SearchResult>

}
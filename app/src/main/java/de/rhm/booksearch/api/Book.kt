package de.rhm.booksearch.api

import com.google.gson.annotations.SerializedName

class Book(
        @SerializedName("cover_i")
        val coverId: String,
        @SerializedName("author_name")
        val authorNames: List<String>,
        @SerializedName("title")
        val title: String
)
package de.rhm.booksearch.api.model

import com.google.gson.annotations.SerializedName

class Book(
        @SerializedName("cover_i")
        private val coverId: String,
        @SerializedName("author_name")
        val authorNames: List<String>?,
        @SerializedName("title")
        val title: String
) {
        val smallCoverUrl get() = "http://covers.openlibrary.org/b/ID/$coverId-S.jpg"
}
package com.example.nba.data.remote.dto

data class ApiPage<T>(
    val data: List<T>,
    val meta: PageMeta? = null
)

data class PageMeta(
    val next_cursor: Long? = null,
    val per_page: Int? = null
)

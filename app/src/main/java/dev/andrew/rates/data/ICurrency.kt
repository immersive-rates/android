package dev.andrew.rates.data

interface ICurrency {
    val name: String
    @Deprecated("xyi")
    val sourceId: Int
}
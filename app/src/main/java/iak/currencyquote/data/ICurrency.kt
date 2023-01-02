package iak.currencyquote.data

interface ICurrency {
    val name: String
    @Deprecated("xyi")
    val sourceId: Int
}
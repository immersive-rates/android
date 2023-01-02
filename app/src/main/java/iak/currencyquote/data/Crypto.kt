package iak.currencyquote.data

data class Crypto constructor(
    override val name: String,
    override val sourceId: Int
) : ICurrency

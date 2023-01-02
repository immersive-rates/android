package iak.currencyquote.data

class Fiat constructor(
    override val name: String,
    override val sourceId: Int
) : ICurrency

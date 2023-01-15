package dev.andrew.rates.data

class Fiat constructor(
    override val name: String,
    override val sourceId: Int
) : ICurrency

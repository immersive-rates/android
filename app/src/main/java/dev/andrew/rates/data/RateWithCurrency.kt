package dev.andrew.rates.data

data class RateWithCurrency(
    val rate: Float,
    val exchange: ICurrency,
    val resulting: ICurrency
)

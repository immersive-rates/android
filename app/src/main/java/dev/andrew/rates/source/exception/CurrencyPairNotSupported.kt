package dev.andrew.rates.source.exception

import dev.andrew.rates.data.ICurrency
import java.lang.RuntimeException

class CurrencyPairNotSupported(
    val first: ICurrency,
    val second: ICurrency
): RuntimeException()
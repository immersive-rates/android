package dev.andrew.rates.data

object CurrencyBuilder {
    fun buildFiat(name: String, sourceId: Int): Fiat {
        return Fiat(name.uppercase(), sourceId)
    }

    fun buildCrypto(name: String, sourceId: Int): Crypto {
        return Crypto(name.uppercase(), sourceId)
    }
}
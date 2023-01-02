package iak.currencyquote.source.currency

interface IPingableSource {
    fun pingPong(): Long
}
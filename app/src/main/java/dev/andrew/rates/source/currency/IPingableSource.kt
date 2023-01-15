package dev.andrew.rates.source.currency

interface IPingableSource {
    fun pingPong(): Long
}
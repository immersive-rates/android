package iak.currencyquote.ui

import com.google.android.material.chip.Chip
import iak.currencyquote.R

fun Chip.bindPrimaryCIndicator() {
    setChipStrokeWidthResource(R.dimen.exchangeChipStroke)
    setChipStrokeColorResource(R.color.primary_currency)
}
fun Chip.bindSecondaryCIndicator() {
    setChipStrokeWidthResource(R.dimen.exchangeChipStroke)
    setChipStrokeColorResource(R.color.secondary_currency)
}
fun Chip.bindPrimaryCSelectionIndicator() {
    setChipStrokeWidthResource(R.dimen.currencyChipStroke)
    setChipStrokeColorResource(R.color.primary_currency)
//    setChipBackgroundColorResource(R.color.primary_currency)
}
fun Chip.bindSecondaryCSelectionIndicator() {
    setChipStrokeWidthResource(R.dimen.currencyChipStroke)
    setChipStrokeColorResource(R.color.secondary_currency)
//    setChipBackgroundColorResource(R.color.secondary_currency)
}
fun Chip.unbindSelectionIndicator() {
    chipStrokeWidth = 0f
    chipStrokeColor = null
}
fun Chip.bindCurrentSelectionIndicator() {
    requestFocus()
}
fun Chip.bindInactiveIndicator() {
    setChipBackgroundColorResource(R.color.disabled_currency)
}
package iak.currencyquote.helper

import iak.currencyquote.data.ICurrency
import java.util.*

object CurrencyHelper {
    private data class LocalCurrency(
        val code: String,
        val symbol: String
    )
    // https://gist.github.com/FUZIK/7d884a0f99e4119b2e50db0f8e8facf0
    // and TODO update https://worldpopulationreview.com/country-rankings/major-countries-currencies
    // this need more symbols!
    private val CURRENCY_ARRAY = arrayOf(
        LocalCurrency(code = "AFN", symbol="Af or Afs (pl.)"),
        LocalCurrency(code = "EUR",  symbol="€"),
        LocalCurrency(code = "ALL", symbol="Lek"),
        LocalCurrency(code = "DZD", symbol="DA"),
        LocalCurrency(code = "AOA", symbol="Kz"),
        LocalCurrency(code = "XCD", symbol="$"),

        LocalCurrency(code = "ARS", symbol="$"),
        LocalCurrency(code = "AMD", symbol="֏"),
        LocalCurrency(code = "AWG", symbol="ƒ"),
        LocalCurrency(code = "SHP", symbol="£"),
        LocalCurrency(code = "AUD", symbol="$"),
        LocalCurrency(code = "AZN", symbol="₼"),
        LocalCurrency(code = "BSD", symbol="$"),
        LocalCurrency(code = "BHD", symbol="BD"),
        LocalCurrency(code = "BDT", symbol="৳"),
        LocalCurrency(code = "BBD", symbol="$"),
        LocalCurrency(code = "BYN", symbol="Rbl or Rbls (pl.)"),
        LocalCurrency(code = "BZD", symbol="$"),
        LocalCurrency(code = "XOF", symbol="Fr"),
        LocalCurrency(code = "BMD", symbol="$"),
        LocalCurrency(code = "BTN", symbol="Nu"),
        LocalCurrency(code = "BOB", symbol="Bs"),
        LocalCurrency(code = "USD", symbol="$"),
        LocalCurrency(code = "BAM", symbol="KM"),
        LocalCurrency(code = "BWP", symbol="P"),
        LocalCurrency(code = "BRL", symbol="R$"),


        LocalCurrency(code = "BND", symbol="$"),
        LocalCurrency(code = "BGN", symbol="Lev"),

        LocalCurrency(code = "BIF", symbol="Fr"),
        LocalCurrency(code = "KHR", symbol="CR"),
        LocalCurrency(code = "XAF", symbol="Fr"),
        LocalCurrency(code = "CAD", symbol="$"),
        LocalCurrency(code = "CVE", symbol="$"),
        LocalCurrency(code = "KYD", symbol="$"),


        LocalCurrency(code = "CLP", symbol="$"),
        LocalCurrency(code = "CNY", symbol="¥"),
        LocalCurrency(code = "COP", symbol="$"),
        LocalCurrency(code = "KMF", symbol="Fr"),
        LocalCurrency(code = "CDF", symbol="Fr"),

        LocalCurrency(code = "CRC", symbol="₡"),

        LocalCurrency(code = "HRK", symbol="kn"),
        LocalCurrency(code = "CUP", symbol="$"),
        LocalCurrency(code = "ANG", symbol="ƒ"),
        LocalCurrency(code = "CZK", symbol="Kč"),
        LocalCurrency(code = "DKK", symbol="kr"),
        LocalCurrency(code = "DJF", symbol="Fr"),

        LocalCurrency(code = "DOP", symbol="$"),


        LocalCurrency(code = "EGP", symbol="LE"),


        LocalCurrency(code = "ERN", symbol="Nkf"),
        LocalCurrency(code = "SZL", symbol="L or E (pl.)"),
        LocalCurrency(code = "ETB", symbol="Br"),
        LocalCurrency(code = "FKP", symbol="£"),
        LocalCurrency(code = "DKK", symbol="kr"),
        LocalCurrency(code = "FJD", symbol="$"),
        LocalCurrency(code = "XPF", symbol="Fr"),

        LocalCurrency(code = "GMD", symbol="D"),
        LocalCurrency(code = "GEL", symbol="₾"),
        LocalCurrency(code = "GHS", symbol="₵"),
        LocalCurrency(code = "GIP", symbol="£"),
        LocalCurrency(code = "DKK", symbol="kr"),

        LocalCurrency(code = "GTQ", symbol="Q"),
        LocalCurrency(code = "GNF", symbol="Fr"),

        LocalCurrency(code = "GYD", symbol="$"),
        LocalCurrency(code = "HTG", symbol="G"),
        LocalCurrency(code = "HNL", symbol="L"),
        LocalCurrency(code = "HKD", symbol="$"),
        LocalCurrency(code = "HUF", symbol="Ft"),
        LocalCurrency(code = "ISK", symbol="kr"),
        LocalCurrency(code = "INR", symbol="₹"),
        LocalCurrency(code = "IDR", symbol="Rp"),
        LocalCurrency(code = "IRR", symbol="Rl or Rls (pl.)"),
        LocalCurrency(code = "IQD", symbol="ID"),
        LocalCurrency(code = "ILS", symbol="₪"),
        LocalCurrency(code = "JMD", symbol="$"),
        LocalCurrency(code = "JPY", symbol="¥"),
        LocalCurrency(code = "JOD", symbol="JD"),
        LocalCurrency(code = "KZT", symbol="₸"),
        LocalCurrency(code = "KES", symbol="Sh or Shs (pl.)"),
        LocalCurrency(code = "KPW", symbol="₩"),
        LocalCurrency(code = "KWD", symbol="KD"),
        LocalCurrency(code = "KGS", symbol="som"),
        LocalCurrency(code = "LAK", symbol="₭"),
        LocalCurrency(code = "LBP", symbol="LL"),
        LocalCurrency(code = "LSL", symbol="L or M (pl.)"),
        LocalCurrency(code = "LRD", symbol="$"),
        LocalCurrency(code = "LYD", symbol="LD"),
        LocalCurrency(code = "CHF", symbol="Fr"),
        LocalCurrency(code = "MOP", symbol="MOP$"),
        LocalCurrency(code = "MGA", symbol="Ar"),
        LocalCurrency(code = "MWK", symbol="K"),
        LocalCurrency(code = "MYR", symbol="RM"),
        LocalCurrency(code = "MVR", symbol="Rf"),


        LocalCurrency(code = "MRU", symbol="UM"),
        LocalCurrency(code = "MUR", symbol="Re or Rs (pl.)"),
        LocalCurrency(code = "MXN", symbol="$"),

        LocalCurrency(code = "MDL", symbol="Leu or Lei (pl.)"),
        LocalCurrency(code = "MNT", symbol="₮"),

        LocalCurrency(code = "MAD", symbol="DH"),
        LocalCurrency(code = "MZN", symbol="Mt"),
        LocalCurrency(code = "MMK", symbol="K or Ks (pl.)"),
        LocalCurrency(code = "NAD", symbol="$"),
        LocalCurrency(code = "NPR", symbol="Re or Rs (pl.)"),
        LocalCurrency(code = "XPF", symbol="Fr"),
        LocalCurrency(code = "NZD", symbol="$"),
        LocalCurrency(code = "NIO", symbol="C$"),

        LocalCurrency(code = "NGN", symbol="₦"),
        LocalCurrency(code = "NZD", symbol="$"),
        LocalCurrency(code = "MKD", symbol="DEN"),
        LocalCurrency(code = "TRY", symbol="₺"),
        LocalCurrency(code = "NOK", symbol="kr"),
        LocalCurrency(code = "OMR", symbol="RO"),
        LocalCurrency(code = "PKR", symbol="Re or Rs (pl.)"),

        LocalCurrency(code = "ILS", symbol="₪"),
        LocalCurrency(code = "PAB", symbol="B/"),
        LocalCurrency(code = "PGK", symbol="K"),
        LocalCurrency(code = "PYG", symbol="₲"),
        LocalCurrency(code = "PEN", symbol="S/"),
        LocalCurrency(code = "PHP", symbol="₱"),
        LocalCurrency(code = "NZD", symbol="$"),
        LocalCurrency(code = "PLN", symbol="zł"),
        LocalCurrency(code = "QAR", symbol="QR"),
        LocalCurrency(code = "RON", symbol="Leu or Lei (pl.)"),
        LocalCurrency(code = "RUB", symbol="₽"),
        LocalCurrency(code = "RWF", symbol="Fr"),

        LocalCurrency(code = "WST", symbol="$"),
        LocalCurrency(code = "STN", symbol="Db"),
        LocalCurrency(code = "SAR", symbol="Rl or Rls (pl.)"),

        LocalCurrency(code = "RSD", symbol="DIN"),
        LocalCurrency(code = "SCR", symbol="Re or Rs (pl.)"),
        LocalCurrency(code = "SLE", symbol="Le"),
        LocalCurrency(code = "SGD", symbol="$"),

        LocalCurrency(code = "ANG", symbol="ƒ"),
        LocalCurrency(code = "SBD", symbol="$"),
        LocalCurrency(code = "SOS", symbol="Sh or Shs (pl.)"),
        LocalCurrency(code = "ZAR", symbol="R"),
        LocalCurrency(code = "RUB", symbol="₽"),
        LocalCurrency(code = "LKR", symbol="Re or Rs (pl.)"),
        LocalCurrency(code = "SDG", symbol="LS"),
        LocalCurrency(code = "SRD", symbol="$"),
        LocalCurrency(code = "SEK", symbol="kr"),
        LocalCurrency(code = "CHF", symbol="Fr"),
        LocalCurrency(code = "SYP", symbol="LS"),
        LocalCurrency(code = "TWD", symbol="$"),
        LocalCurrency(code = "TJS", symbol="SM"),
        LocalCurrency(code = "TZS", symbol="Sh or Shs (pl.)"),
        LocalCurrency(code = "THB", symbol="฿"),

        LocalCurrency(code = "TOP", symbol="T$"),
        LocalCurrency(code = "TTD", symbol="$"),
        LocalCurrency(code = "TND", symbol="DT"),
        LocalCurrency(code = "TRY", symbol="₺"),
        LocalCurrency(code = "TMT", symbol="m"),

        LocalCurrency(code = "UGX", symbol="Sh or Shs (pl.)"),
        LocalCurrency(code = "UAH", symbol="₴"),
        LocalCurrency(code = "AED", symbol="Dh or Dhs (pl.)"),
        LocalCurrency(code = "GBP", symbol="£"),

        LocalCurrency(code = "UYU", symbol="$"),
        LocalCurrency(code = "UZS", symbol="soum"),
        LocalCurrency(code = "VUV", symbol="VT"),
        LocalCurrency(code = "VES", symbol="Bs.S"),
        LocalCurrency(code = "VND", symbol="₫"),
        LocalCurrency(code = "XPF", symbol="Fr"),
        LocalCurrency(code = "YER", symbol="Rl or Rls (pl.)"),
        LocalCurrency(code = "ZMW", symbol="K")
    )

    /*
    From https://www.xe.com/popularity.php
    let result = ""
    Array.from(document.querySelectorAll('.pLine')).forEach(e => result += '"' + e.querySelector('.sym').innerText + '",\n')
    console.log(result)
     */
    private val CURRENCY_POPULARITY_ARRAY = arrayOf(
        "USD",
        "EUR",
        "GBP",
        "INR",
        "AUD",
        "CAD",
        "ZAR",
        "NZD",
        "JPY",
        "SGD",
        "CNY",
        "CHF",
        "MYR",
        "HUF",
        "HKD",
        "AED",
        "THB",
        "KRW",
        "SEK",
        "PHP",
        "ILS",
        "TRY",
        "PLN",
        "MXN",
        "NOK",
        "DKK",
        "KWD",
        "BRL",
        "IDR",
        "TWD",
        "SAR",
        "PKR",
        "CZK",
        "IQD",
        "RUB",
        "KES",
        "JOD",
        "CLP",
        "ARS",
        "EGP",
        "VND",
        "QAR",
        "XAU",
        "RON",
        "COP",
        "OMR",
        "BGN",
        "XAG",
        "TND",
        "MAD",
        "BHD",
        "ISK",
        "LKR",
        "HRK",
        "GEL",
        "UAH",
        "IRR",
        "NGN",
        "XAF",
        "SKK",
        "LVL",
        "LTL",
        "BDT",
        "MUR",
        "CYP",
        "ALL",
        "FJD",
        "MTL",
        "PEN",
        "XOF",
        "BOB",
        "BYR",
        "UYU",
        "BAM",
        "UZS",
        "KZT",
        "MKD",
        "MDL",
        "AMD",
        "KGS",
        "TJS",
        "TMT",
        "VEF",
        "DOP",
        "JMD",
        "DZD",
        "XPF",
        "UGX",
        "CRC",
        "ZMK",
        "BND",
        "LYD",
        "XCD",
        "TRL",
        "BBD",
        "TTD",
        "LBP",
        "SYP",
        "KHR",
        "NPR",
        "ROL",
        "GTQ",
        "XPD",
        "MOP",
        "PYG",
        "XPT",
        "BWP",
        "TZS",
        "ANG",
        "BSD",
        "HNL",
        "NAD",
        "ETB",
        "ZWL",
        "BMD",
        "CUP",
        "NIO",
        "MWK",
        "PAB",
        "VUV",
        "HTG",
        "XDR",
        "GYD",
        "PGK",
        "WST",
        "LAK",
        "BZD",
        "YER",
        "GMD",
        "KYD",
        "MNT",
        "KPW",
        "SVC",
        "SDG",
        "MGA",
        "FKP",
        "CVE",
        "AOA",
        "SCR",
        "MMK",
        "GNF",
        "SLL",
        "AWG",
        "MVR",
        "RWF",
        "SBD",
        "TOP",
        "GIP",
        "DJF",
        "SOS",
        "LRD",
        "MRO",
        "BIF",
        "GHS",
        "BTN",
        "SRD",
        "SZL",
        "LSL",
        "SHP",
        "KMF",
        "STD",
        "ERN",
        "MZN",
        "AZN",
        "AFN",
        "CSD"
    )

    fun isFiat(code: String) =
        CURRENCY_ARRAY.any { code.uppercase() == it.code}
    
    fun isCrypto(code: String): Boolean = !isFiat(code)

    private fun getSystemCurrency(code: String) = try {
        Currency.getInstance(code)
    } catch (e: Exception) {
        null
    }

    fun getCurrencySymbol(code: String): String {
        val local = CURRENCY_ARRAY.firstOrNull { it.code == code.uppercase() }
        if (local != null) {
            return local.symbol
        }
        val system = getSystemCurrency(code)
        if (system != null) {
            return system.symbol
        }
        return code
    }

    fun sortFiatByPopularity(list: List<ICurrency>): List<ICurrency> {
        return list.sortedBy {
            CURRENCY_POPULARITY_ARRAY.indexOf(it.name)?.let { index ->
                if (index == -1) {
                    1000
                } else {
                    index
                }
            }
        }
    }
}
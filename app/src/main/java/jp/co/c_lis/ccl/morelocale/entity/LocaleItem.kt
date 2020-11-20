package jp.co.c_lis.ccl.morelocale.entity

data class LocaleItem(
        private var label: String? = null,
        val hasLabel: Boolean = false,
        val country: String,
        val language: String? = null
)

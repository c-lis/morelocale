package jp.co.c_lis.ccl.morelocale.entity

data class LocaleItem(
        val hasLabel: Boolean = false,
        val country: String,
        val language: String? = null
) {
    val label: String
        get() {
            return if (language != null) {
                "$country $language"
            } else {
                country
            }
        }

}

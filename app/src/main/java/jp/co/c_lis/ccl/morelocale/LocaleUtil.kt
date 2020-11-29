package jp.co.c_lis.ccl.morelocale

import java.util.Locale

fun equals(obj1: Locale, obj2: Locale): Boolean {
    if (obj1 === obj2) {
        return true
    }

    if (obj1.language != obj2.language) {
        return false
    }

    if (obj1.country != obj2.country) {
        return false
    }

    if (obj1.variant != obj2.variant) {
        return false
    }

    return true
}

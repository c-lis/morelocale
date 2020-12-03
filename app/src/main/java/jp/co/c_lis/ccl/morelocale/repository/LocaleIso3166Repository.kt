package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import jp.co.c_lis.ccl.morelocale.entity.Type

class LocaleIso3166Repository(
        applicationContext: Context
) : LocaleIsoRepository(applicationContext) {

    override val type: Type
        get() = Type.Iso3166

}

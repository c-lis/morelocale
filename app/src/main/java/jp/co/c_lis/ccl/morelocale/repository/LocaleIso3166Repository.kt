package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import jp.co.c_lis.ccl.morelocale.entity.Type

class LocaleIso3166Repository(
        application: Application
) : LocaleIsoRepository(application) {

    override val type: Type
        get() = Type.Iso3166

}

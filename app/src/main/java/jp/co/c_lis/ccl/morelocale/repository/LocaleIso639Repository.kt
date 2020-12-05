package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import jp.co.c_lis.ccl.morelocale.entity.Type

class LocaleIso639Repository(
        application: Application
) : LocaleIsoRepository(application) {

    override val type: Type
        get() = Type.Iso639
}

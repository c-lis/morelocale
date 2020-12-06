package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import android.content.res.Resources
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocaleIso3166Repository(
        application: Application
) : LocaleIsoRepository(application) {

    override val type: Type
        get() = Type.Iso3166

    override suspend fun init(resources: Resources) = withContext(Dispatchers.IO) {
        init(resources.getStringArray(R.array.iso_3166_title),
                resources.getStringArray(R.array.iso_3166_value)
        )
    }

}

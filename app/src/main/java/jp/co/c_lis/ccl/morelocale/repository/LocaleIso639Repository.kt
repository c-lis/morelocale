package jp.co.c_lis.ccl.morelocale.repository

import android.app.Application
import android.content.res.Resources
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocaleIso639Repository(
        application: Application
) : LocaleIsoRepository(application) {

    override val type: Type
        get() = Type.Iso639

    override suspend fun init(resources: Resources) = withContext(Dispatchers.IO) {
        init(resources.getStringArray(R.array.iso_639_title),
                resources.getStringArray(R.array.iso_639_value)
        )
    }

}

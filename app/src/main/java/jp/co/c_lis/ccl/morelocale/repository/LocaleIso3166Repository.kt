package jp.co.c_lis.ccl.morelocale.repository

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.entity.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

class LocaleIso3166Repository(
    applicationContext: Context
) : LocaleIsoRepository(applicationContext) {

    override val type: Type
        get() = Type.Iso3166

    override suspend fun init(resources: Resources) = withContext(Dispatchers.IO) {
        init(resources.getStringArray(R.array.iso_3166_title),
                resources.getStringArray(R.array.iso_3166_value)
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LocaleIso3166RepositoryModule {

    @Singleton
    @Provides
    fun provideLocaleIso3166Repository(
        @ApplicationContext applicationContext: Context,
    ): LocaleIso3166Repository {
        return LocaleIso3166Repository(
            applicationContext
        )
    }
}

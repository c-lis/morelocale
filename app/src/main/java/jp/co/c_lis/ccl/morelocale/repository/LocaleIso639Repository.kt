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

class LocaleIso639Repository(
    applicationContext: Context
) : LocaleIsoRepository(applicationContext) {

    override val type: Type
        get() = Type.Iso639

    override suspend fun init(resources: Resources) = withContext(Dispatchers.IO) {
        init(resources.getStringArray(R.array.iso_639_title),
                resources.getStringArray(R.array.iso_639_value)
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object LocaleIso639RepositoryModule {

    @Singleton
    @Provides
    fun provideLocaleIso639Repository(
        @ApplicationContext applicationContext: Context,
    ): LocaleIso639Repository {
        return LocaleIso639Repository(
            applicationContext
        )
    }
}

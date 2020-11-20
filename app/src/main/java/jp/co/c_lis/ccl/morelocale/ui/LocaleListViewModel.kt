package jp.co.c_lis.ccl.morelocale.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LocaleListViewModel(
        private val localeListRepository: LocaleRepository,
        private val coroutineScope: CoroutineScope,
) : ViewModel() {

    val currentLocale = MutableLiveData<LocaleItem>()
    val localeList = MutableLiveData<List<LocaleItem>>()

    fun loadCurrentLocale(context: Context) {
        coroutineScope.launch {
            @Suppress("DEPRECATION")
            currentLocale.postValue(createLocale(context.resources.configuration.locale))
        }
    }

    fun loadLocaleList(context: Context) {
        coroutineScope.launch {
            localeList.postValue(localeListRepository.getAll(context.assets))
        }
    }

}

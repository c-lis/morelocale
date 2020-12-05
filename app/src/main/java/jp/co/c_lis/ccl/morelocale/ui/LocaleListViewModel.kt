package jp.co.c_lis.ccl.morelocale.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.entity.createLocale
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import jp.co.c_lis.morelocale.MoreLocale
import kotlinx.coroutines.launch

class LocaleListViewModel(
        private val localeListRepository: LocaleRepository
) : ViewModel() {

    val currentLocale = MutableLiveData<LocaleItem>()
    val localeList = MutableLiveData<List<LocaleItem>>()

    fun loadCurrentLocale(context: Context) {
        viewModelScope.launch {
            @Suppress("DEPRECATION")
            currentLocale.postValue(createLocale(MoreLocale.getLocale(context.resources.configuration)))
        }
    }

    fun loadLocaleList(context: Context) {
        viewModelScope.launch {
            localeList.postValue(localeListRepository.getAll(context.assets))
        }
    }

    fun addLocale(localeItem: LocaleItem) {
        viewModelScope.launch {
            localeListRepository.create(localeItem)
            val list = localeListRepository.findAll()

            localeList.postValue(list)
        }
    }

    fun editLocale(localeItem: LocaleItem) {
        viewModelScope.launch {
            localeListRepository.update(localeItem)
            localeList.postValue(localeListRepository.findAll())
        }
    }

    fun deleteLocale(localeItem: LocaleItem) {
        viewModelScope.launch {
            localeListRepository.delete(localeItem)
            localeList.postValue(localeListRepository.findAll())
        }
    }

}

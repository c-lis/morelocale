package jp.co.c_lis.ccl.morelocale.ui.locale_iso_list

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.repository.LocaleIsoRepository
import kotlinx.coroutines.launch

class LocaleIsoListViewModel(
        private val localeIsoRepository: LocaleIsoRepository
) : ViewModel() {

    val localeIsoList = MutableLiveData<List<LocaleIsoItem>>()

    fun init(resources: Resources) {
        viewModelScope.launch {
            localeIsoList.postValue(localeIsoRepository.findAll(resources))
        }
    }

    fun search(searchString: String?) {
        viewModelScope.launch {
            val list = localeIsoRepository.findMatchLabel(searchString)
            localeIsoList.postValue(list)
        }
    }
}

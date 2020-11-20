package jp.co.c_lis.ccl.morelocale.ui

import android.content.res.AssetManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LocaleListViewModel(
        private val localeListRepository: LocaleRepository,
        private val coroutineScope: CoroutineScope,
) : ViewModel() {

    val localeList = MutableLiveData<List<LocaleItem>>()

    fun showLocaleList(assetManager: AssetManager) {
        coroutineScope.launch {
            localeList.postValue(localeListRepository.getAll(assetManager))
        }
    }

}

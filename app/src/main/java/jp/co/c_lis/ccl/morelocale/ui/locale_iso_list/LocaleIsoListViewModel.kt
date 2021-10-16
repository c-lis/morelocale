package jp.co.c_lis.ccl.morelocale.ui.locale_iso_list

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.c_lis.ccl.morelocale.entity.LocaleIsoItem
import jp.co.c_lis.ccl.morelocale.entity.Type
import jp.co.c_lis.ccl.morelocale.repository.LocaleIso3166Repository
import jp.co.c_lis.ccl.morelocale.repository.LocaleIso639Repository
import jp.co.c_lis.ccl.morelocale.repository.LocaleIsoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocaleIsoListViewModel @Inject constructor(
    private val localeIso639Repository: LocaleIso639Repository,
    private val localeIso3166Repository: LocaleIso3166Repository,
) : ViewModel() {

    var localeType = Type.Iso639
        set(value) {
            field = value
            localeIsoRepository = when (field) {
                Type.Iso639 -> localeIso639Repository
                Type.Iso3166 -> localeIso3166Repository
            }
        }

    private lateinit var localeIsoRepository: LocaleIsoRepository

    val localeIsoList = MutableLiveData<List<LocaleIsoItem>>()

    fun init(resources: Resources) {
        viewModelScope.launch {
            localeIsoList.postValue(localeIsoRepository.findAll(resources))
        }
    }

    fun search(searchString: String?) {
        viewModelScope.launch {
            val likeText = searchString?.let {
                "$it%"
            } ?: ""
            val list = localeIsoRepository.findMatchLabel(likeText)
            localeIsoList.postValue(list)
        }
    }
}

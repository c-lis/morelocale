package jp.co.c_lis.ccl.morelocale.ui.locale_iso_list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLocaleSelectBinding
import jp.co.c_lis.ccl.morelocale.entity.Type
import jp.co.c_lis.ccl.morelocale.repository.LocaleIso3166Repository
import jp.co.c_lis.ccl.morelocale.repository.LocaleIso639Repository
import jp.co.c_lis.ccl.morelocale.repository.LocaleIsoRepository
import jp.co.c_lis.ccl.morelocale.ui.LocaleSelectorDialog

class LocaleIsoListFragment : Fragment(R.layout.fragment_locale_select) {

    companion object {
        val TAG = LocaleSelectorDialog::class.simpleName

        const val RESULT_KEY_LOCALE = "result_key_locale"

        private const val KEY_LOCALE_TYPE = "key_locale_type"

        fun getIso639Instance(): LocaleIsoListFragment {
            val args = Bundle().apply {
                putInt(KEY_LOCALE_TYPE, Type.Iso639.ordinal)
            }

            return LocaleIsoListFragment().also {
                it.arguments = args
            }
        }

        fun getIso3166Instance(): LocaleIsoListFragment {
            val args = Bundle().apply {
                putInt(KEY_LOCALE_TYPE, Type.Iso3166.ordinal)
            }

            return LocaleIsoListFragment().also {
                it.arguments = args
            }
        }
    }

    private var localeType = Type.Iso639

    private val viewModel by viewModels<LocaleIsoListViewModel> {
        val repository = when (localeType) {
            Type.Iso3166 -> LocaleIso3166Repository(requireContext().applicationContext)
            Type.Iso639 -> LocaleIso639Repository(requireContext().applicationContext)
        }
        LocaleIsoListViewModelProvider(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localeType = Type.values()[requireArguments().getInt(KEY_LOCALE_TYPE)]
    }

    var binding: FragmentLocaleSelectBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LocaleIsoListAdapter(layoutInflater, lifecycleScope) { localeIsoItem ->
            setFragmentResult(localeType.name, bundleOf(RESULT_KEY_LOCALE to localeIsoItem.value))
        }

        val binding = FragmentLocaleSelectBinding.bind(view).also {
            it.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
            )
            it.recyclerView.adapter = adapter

            binding = it
        }

        viewModel.localeIsoList.observe(viewLifecycleOwner, Observer {
            adapter.localeIsoItemList = it
        })

        viewModel.init(resources)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    inner class LocaleIsoListViewModelProvider(
            private val localeIsoRepository: LocaleIsoRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LocaleIsoListViewModel(localeIsoRepository) as T
        }
    }
}

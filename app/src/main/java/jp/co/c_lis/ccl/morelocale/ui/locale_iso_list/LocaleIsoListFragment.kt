package jp.co.c_lis.ccl.morelocale.ui.locale_iso_list

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
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

class LocaleIsoListFragment : Fragment(R.layout.fragment_locale_select) {

    companion object {
        val TAG = LocaleIsoListFragment::class.simpleName

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
        val application = requireContext().applicationContext as Application
        val repository = when (localeType) {
            Type.Iso3166 -> LocaleIso3166Repository(application)
            Type.Iso639 -> LocaleIso639Repository(application)
        }
        LocaleIsoListViewModelProvider(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localeType = Type.values()[requireArguments().getInt(KEY_LOCALE_TYPE)]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setHasOptionsMenu(true)
    }

    var binding: FragmentLocaleSelectBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LocaleIsoListAdapter(layoutInflater, lifecycleScope) { localeIsoItem ->
            setFragmentResult(localeType.name, bundleOf(RESULT_KEY_LOCALE to localeIsoItem.value))
            parentFragmentManager.popBackStack()
        }

        binding = FragmentLocaleSelectBinding.bind(view).also { binding ->
            binding.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
            )
            binding.recyclerView.adapter = adapter

            if (requireContext() is AppCompatActivity) {
                setupActionBar(requireContext() as AppCompatActivity, binding.toolbar)
            }
        }

        viewModel.localeIsoList.observe(viewLifecycleOwner, {
            adapter.localeIsoItemList = it
            binding?.progress?.visibility = View.GONE
        })

        viewModel.init(resources)
    }

    private fun setupActionBar(activity: AppCompatActivity, toolbar: Toolbar) {
        val title = getString(localeType.titleRes)
        val isoTitle = getString(localeType.isoTitle)
        toolbar.title = "$title - $isoTitle"

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.also {
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private val searchQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            viewModel.search(newText)
            return true
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()

        inflater.inflate(R.menu.fragment_locale_iso_list, menu)

        val searchMenu = menu.findItem(R.id.menu_search)
        val searchView = searchMenu.actionView as SearchView
        searchView.setOnQueryTextListener(searchQueryTextListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

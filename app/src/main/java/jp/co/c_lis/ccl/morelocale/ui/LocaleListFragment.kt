package jp.co.c_lis.ccl.morelocale.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLocaleListBinding
import jp.co.c_lis.ccl.morelocale.entity.LocaleItem
import jp.co.c_lis.ccl.morelocale.repository.LocaleRepository
import jp.co.c_lis.ccl.morelocale.ui.help.PermissionRequiredDialog
import jp.co.c_lis.ccl.morelocale.ui.license.LicenseActivity
import jp.co.c_lis.ccl.morelocale.widget.WrapContentLinearLayoutManager
import jp.co.c_lis.morelocale.MoreLocale
import timber.log.Timber
import java.lang.reflect.InvocationTargetException

class LocaleListFragment : Fragment() {

    private var binding: FragmentLocaleListBinding? = null

    private val viewModel by viewModels<LocaleListViewModel> {
        LocaleListViewModelProvider(LocaleRepository(requireContext().applicationContext))
    }

    companion object {
        val TAG = LocaleListFragment::class.java.simpleName

        fun getInstance(): LocaleListFragment {
            return LocaleListFragment()
        }
    }

    private val menuCallback = object : LocaleListAdapter.MenuCallback {
        override fun onEdit(localeItem: LocaleItem) {
            EditLocaleDialog.getEditInstance(localeItem)
                    .show(parentFragmentManager, EditLocaleDialog.TAG)
        }

        override fun onDelete(localeItem: LocaleItem) {
            viewModel.deleteLocale(localeItem)
        }

    }

    private var adapter: LocaleListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setHasOptionsMenu(true)

        adapter = LocaleListAdapter(
                LayoutInflater.from(context),
                lifecycleScope,
                menuCallback
        ) { localeItem ->
            Timber.d("Change locale ${localeItem.displayName}")
            setLocale(localeItem)
        }
    }

    private fun setLocale(localeItem: LocaleItem) {
        try {
            MoreLocale.setLocale(localeItem.locale)
        } catch (e: InvocationTargetException) {
            Timber.e(e, "InvocationTargetException")
            PermissionRequiredDialog.getInstance()
                    .show(parentFragmentManager, PermissionRequiredDialog.TAG)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_locale_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(EditLocaleDialog.MODE.ADD.name) { requestKey, bundle ->
            val localeItemAdded = bundle.getParcelable<LocaleItem>(EditLocaleDialog.RESULT_KEY_LOCALE_ITEM)
                    ?: return@setFragmentResultListener
            Timber.d("Fragment Result $requestKey ${localeItemAdded.displayName}")
            viewModel.addLocale(localeItemAdded)
        }
        setFragmentResultListener(EditLocaleDialog.MODE.EDIT.name) { requestKey, bundle ->
            Timber.d("Fragment Result $requestKey")
            val localeItemEdited = bundle.getParcelable<LocaleItem>(EditLocaleDialog.RESULT_KEY_LOCALE_ITEM)
                    ?: return@setFragmentResultListener
            viewModel.editLocale(localeItemEdited)
        }
        setFragmentResultListener(EditLocaleDialog.MODE.SET.name) { requestKey, bundle ->
            Timber.d("Fragment Result $requestKey")
            val localeItem = bundle.getParcelable<LocaleItem>(EditLocaleDialog.RESULT_KEY_LOCALE_ITEM)
                    ?: return@setFragmentResultListener
            setLocale(localeItem)
        }

        viewModel.currentLocale.observe(viewLifecycleOwner, { currentLocale ->
            binding?.also {
                it.currentLocale = currentLocale

            }
        })

        viewModel.localeList.observe(viewLifecycleOwner, { localeItemList ->
            adapter?.also {
                Timber.d("localeItemList size ${localeItemList.size}")
                it.localeItemList = localeItemList
                it.notifyDataSetChanged()
            }
        })

        binding = FragmentLocaleListBinding.bind(view).also {
            it.recyclerView.layoutManager = WrapContentLinearLayoutManager(
                    requireContext(), LinearLayoutManager.VERTICAL, false)
            it.recyclerView.adapter = adapter
            it.customLocale.setOnClickListener {
                EditLocaleDialog.getSetInstance()
                        .show(parentFragmentManager, EditLocaleDialog.TAG)
            }
        }

        viewModel.loadCurrentLocale(requireContext())
        viewModel.loadLocaleList(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.fragment_locale_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_locale -> {
                EditLocaleDialog.getAddInstance()
                        .show(parentFragmentManager, EditLocaleDialog.TAG)

            }
            R.id.menu_license -> {
                startActivity(Intent(requireContext(), LicenseActivity::class.java))
            }
            R.id.menu_about -> {
                AboutDialog.getInstance()
                        .show(parentFragmentManager, AboutDialog.TAG)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    inner class LocaleListViewModelProvider(
            private val localeRepository: LocaleRepository
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LocaleListViewModel(localeRepository) as T
        }
    }
}

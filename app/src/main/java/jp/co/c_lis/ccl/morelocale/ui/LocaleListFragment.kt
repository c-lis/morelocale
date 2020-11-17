package jp.co.c_lis.ccl.morelocale.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLocaleListBinding

class LocaleListFragment : Fragment() {

    private var binding: FragmentLocaleListBinding? = null

    private val viewModel by viewModels<LocaleListViewModel> {
        LocaleListViewModelProvider()
    }

    companion object {
        val TAG = LocaleListFragment::class.java.simpleName

        fun getInstance(): LocaleListFragment {
            return LocaleListFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_locale_list, container, false)

        binding = FragmentLocaleListBinding.bind(view)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    class LocaleListViewModelProvider : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LocaleListViewModel() as T
        }
    }

}

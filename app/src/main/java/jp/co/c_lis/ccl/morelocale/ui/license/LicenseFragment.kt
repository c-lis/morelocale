package jp.co.c_lis.ccl.morelocale.ui.license

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.FragmentLicenseBinding
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLicenseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LicenseFragment : Fragment(R.layout.fragment_license) {

    companion object {
        val TAG = LicenseFragment::class.java.simpleName

        fun getInstance(): LicenseFragment {
            return LicenseFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        setHasOptionsMenu(true)

        if (context is AppCompatActivity) {
            context.supportActionBar?.also {
                it.setHomeButtonEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    private var binding: FragmentLicenseBinding? = null
    private var adapter: Adapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = Adapter(layoutInflater, LICENSES, requireContext().assets, lifecycleScope)

        val binding = FragmentLicenseBinding.bind(view).also { binding ->
            binding.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.adapter = adapter
            this.binding = binding
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()

    }

    class Adapter(
            private val inflater: LayoutInflater,
            private val LICENSES: List<License>,
            private val assetManager: AssetManager,
            private val coroutineScope: CoroutineScope
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val cacheBin = HashMap<License, String>()

        fun destroy() {
            cacheBin.clear()
        }

        override fun getItemCount() = LICENSES.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return Holder(
                    inflater.inflate(R.layout.list_item_license, parent, false),
                    cacheBin
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is Holder) {
                holder.bind(LICENSES[position], assetManager, coroutineScope)
            }
        }

        override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
            super.onViewRecycled(holder)

            if (holder is Holder) {
                holder.unbind()
            }
        }

        class Holder(
                itemView: View,
                private val cacheBin: HashMap<License, String>
        ) : RecyclerView.ViewHolder(itemView) {
            private val binding = ListItemLicenseBinding.bind(itemView)

            private suspend fun loadLicenseText(
                    assetManager: AssetManager,
                    license: License
            ) = withContext(Dispatchers.IO) {
                if (cacheBin.containsKey(license)) {
                    return@withContext cacheBin[license]
                }

                assetManager.open(license.licensePathOnAsset).use {
                    val licenseText = it.bufferedReader().readText()
                    cacheBin[license] = licenseText
                    return@withContext licenseText
                }
            }

            fun bind(license: License, assetManager: AssetManager, coroutineScope: CoroutineScope) {
                binding.licenseData = license

                coroutineScope.launch {
                    val licenseText = loadLicenseText(assetManager, license)
                    binding.licenseText = licenseText
                }
            }

            fun unbind() {
                binding.unbind()
            }
        }
    }
}

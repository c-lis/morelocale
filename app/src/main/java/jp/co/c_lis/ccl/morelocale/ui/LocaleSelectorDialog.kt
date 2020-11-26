package jp.co.c_lis.ccl.morelocale.ui

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.co.c_lis.ccl.morelocale.R
import jp.co.c_lis.ccl.morelocale.databinding.DialogLocaleSelectorBinding
import jp.co.c_lis.ccl.morelocale.databinding.ListItemLocaleCandidateBinding
import timber.log.Timber

class LocaleSelectorDialog : AppCompatDialogFragment() {

    enum class LocaleType(val titleRes: Int, val titleResIds: Int, val valuesResIds: Int) {
        Iso639(R.string.iso639, R.array.iso_639_title, R.array.iso_639_value),
        Iso3166(R.string.iso3166, R.array.iso_3166_title, R.array.iso_3166_value),
    }

    companion object {
        val TAG = LocaleSelectorDialog::class.simpleName

        const val RESULT_KEY_LOCALE = "result_key_locale"

        private const val KEY_LOCALE_TYPE = "key_locale_type"

        fun getIso639Instance(): LocaleSelectorDialog {
            val args = Bundle().apply {
                putInt(KEY_LOCALE_TYPE, LocaleType.Iso639.ordinal)
            }

            return LocaleSelectorDialog().also {
                it.arguments = args
            }
        }

        fun getIso3166Instance(): LocaleSelectorDialog {
            val args = Bundle().apply {
                putInt(KEY_LOCALE_TYPE, LocaleType.Iso3166.ordinal)
            }

            return LocaleSelectorDialog().also {
                it.arguments = args
            }
        }
    }

    private var binding: DialogLocaleSelectorBinding? = null

    private var localeType = LocaleType.Iso639

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localeType = LocaleType.values()[requireArguments().getInt(KEY_LOCALE_TYPE)]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val adapter = Adapter(resources, localeType, layoutInflater) { localeStr ->
            setFragmentResult(localeType.name, bundleOf(RESULT_KEY_LOCALE to localeStr))
            dismiss()
        }

        val binding = DialogLocaleSelectorBinding.inflate(layoutInflater).also { binding ->
            binding.recyclerView.layoutManager = LinearLayoutManager(
                    requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.recyclerView.adapter = adapter
            this.binding = binding
        }

        return AlertDialog.Builder(requireContext())
                .setTitle(localeType.titleRes)
                .setView(binding.root)
                .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
    }

    class Adapter(
            resources: Resources,
            localeType: LocaleType,
            private val inflater: LayoutInflater,
            private val onSelect: (localeStr: String) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val titles = resources.getStringArray(localeType.titleResIds)
        private val values = resources.getStringArray(localeType.valuesResIds)

        init {
            Timber.d("titles ${titles.size}")
        }

        override fun getItemCount() = titles.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return Holder(
                    inflater.inflate(R.layout.list_item_locale_candidate, parent, false),
                    onSelect
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is Holder) {
                holder.bind(titles[position], values[position])
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
                private val onSelect: (localeStr: String) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {
            private val binding = ListItemLocaleCandidateBinding.bind(itemView)

            fun bind(title: String, value: String) {
                binding.title = title
                binding.root.setOnClickListener {
                    onSelect(value)
                }
            }

            fun unbind() {
                binding.unbind()
            }
        }
    }
}

package kr.eg.egiwon.pdfsample.base

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<IDENTIFIER : BaseIdentifier, VDB : ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val bindingId: Int,
    private val viewModels: Map<Int?, BaseViewModel> = mapOf()
) : ListAdapter<IDENTIFIER, BaseListAdapterViewHolder<VDB>>(object :
    DiffUtil.ItemCallback<IDENTIFIER>() {

    override fun areItemsTheSame(oldItem: IDENTIFIER, newItem: IDENTIFIER): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: IDENTIFIER, newItem: IDENTIFIER): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseListAdapterViewHolder<VDB> =
        object : BaseListAdapterViewHolder<VDB>(parent, layoutResId, bindingId, viewModels) {}

    override fun onBindViewHolder(
        holderListAdapter: BaseListAdapterViewHolder<VDB>,
        position: Int
    ) =
        holderListAdapter.onBindViewHolder(getItem(position))

    override fun onViewRecycled(holderListAdapter: BaseListAdapterViewHolder<VDB>) {
        super.onViewRecycled(holderListAdapter)
        holderListAdapter.onRecycledViewHolder()
    }

    fun replaceAll(items: List<IDENTIFIER>?) {
        if (items != null) {
            submitList(items)
        }
    }

}
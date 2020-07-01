package kr.eg.egiwon.pdfsample.filebrowser

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.egiwon.scopedstorageexample.base.BaseAdapter
import com.egiwon.scopedstorageexample.base.BaseViewHolder
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.databinding.ItemFileBinding
import kr.eg.egiwon.pdfsample.filebrowser.model.DocumentItem

class FileViewAdapter(
    @LayoutRes private val layoutResId: Int = R.layout.item_file,
    private val viewModel: FileBrowserViewModel
) : BaseAdapter<DocumentItem, ItemFileBinding>(
    layoutResId
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemFileBinding> = FileViewHolder(layoutResId, parent)

    inner class FileViewHolder(
        @LayoutRes private val layoutResId: Int,
        parent: ViewGroup
    ) : BaseViewHolder<ItemFileBinding>(
        layoutResId, parent
    ) {

        override fun onBind(item: Any?) = binding.bind(item)

        private fun ItemFileBinding.bind(item: Any?) {
            vm = viewModel
            documentItem = item as DocumentItem
            executePendingBindings()
        }
    }
}
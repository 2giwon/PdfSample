package kr.eg.egiwon.pdfsample.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.eg.egiwon.pdfsample.base.BaseAdapter2
import kr.eg.egiwon.pdfsample.base.BaseIdentifier


@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceItems")
fun RecyclerView.replaceItems(items: List<BaseIdentifier>?) {
    (adapter as? BaseAdapter2)?.run {
        replaceItems(items)
        notifyDataSetChanged()
    }
}
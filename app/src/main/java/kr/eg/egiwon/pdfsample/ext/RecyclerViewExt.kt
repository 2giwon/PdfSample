package kr.eg.egiwon.pdfsample.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.eg.egiwon.pdfsample.base.BaseAdapter


@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceItems")
fun RecyclerView.replaceItems(items: List<Any>?) {
    (adapter as? BaseAdapter<Any, *>)?.run {
        replaceItems(items)
        notifyDataSetChanged()
    }
}
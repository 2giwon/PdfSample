package kr.eg.egiwon.pdfsample.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

abstract class BaseAdapter2(
    @LayoutRes private val layoutResId: Int,
    private val bindingId: Int?,
    private val viewModels: Map<Int?, BaseViewModel> = mapOf()
) : RecyclerView.Adapter<BindingViewHolder>(), AutoUpdatable {

    private val list: List<BaseIdentifier> by Delegates.observable(emptyList()) { kProp, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val binding = DataBindingUtil.bind<ViewDataBinding>(view)!!
        return BindingViewHolder(binding, bindingId, viewModels)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size


}
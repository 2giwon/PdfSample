package kr.eg.egiwon.pdfsample.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates
import kotlin.reflect.KClass

abstract class BaseAdapter2(
    private val bindingId: Int?,
    private val viewModels: Map<Int?, BaseViewModel> = mapOf(),
    private val mapper: Map<KClass<out BaseIdentifier>, Int> = mapOf()
) : RecyclerView.Adapter<BindingViewHolder>(), AutoUpdatable {

    private var list: List<BaseIdentifier> by Delegates.observable(emptyList()) { _, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val binding = DataBindingUtil.bind<ViewDataBinding>(view)!!
        return BindingViewHolder(binding, bindingId, viewModels)
    }

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun replaceItems(items: List<BaseIdentifier>?) {
        if (items != null) {
            list = items
        }
    }

    override fun getItemViewType(position: Int): Int {
        return requireNotNull(mapper[list[position]::class])
    }
}
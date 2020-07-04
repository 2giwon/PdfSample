package kr.eg.egiwon.pdfsample.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BindingViewHolder(
    private val binding: ViewDataBinding,
    private val bindingId: Int?,
    private val viewModels: Map<Int?, BaseViewModel> = mapOf()
) : RecyclerView.ViewHolder(binding.root) {

    open fun onBind(item: BaseIdentifier?) {
        if (bindingId == null) return
        if (item == null) return

        binding.run {
            viewModels.forEach {
                if (it.key == null) return@forEach
                setVariable(requireNotNull(it.key), it.value)
            }

            setVariable(bindingId, item)
            executePendingBindings()
        }
    }

}
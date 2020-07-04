package kr.eg.egiwon.pdfsample.filebrowser

//class FileViewAdapter(
//    @LayoutRes private val layoutResId: Int = R.layout.item_file,
//    private val viewModel: FileBrowserViewModel
//) : BaseAdapter2(layoutResId) {
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): BindingViewHolder = FileViewHolder()
//
//    inner class FileViewHolder(
//        private val binding: ItemFileBinding
//    ) : BindingViewHolder(binding) {
//
//        override fun onBind(baseIdentifier: BaseIdentifier) = binding.bind(baseIdentifier)
//
//        private fun ItemFileBinding.bind(item: BaseIdentifier?) {
//            vm = viewModel
//            documentItem = item as DocumentItem
//            executePendingBindings()
//        }
//    }
//}
package com.example.cardapioapp.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cardapioapp.databinding.ItemProductBinding
import com.example.cardapioapp.databinding.ItemSectionBinding

class MenuAdapter(
    private val onItemClicked: (MenuItem.Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<MenuItem>()

    private companion object {
        const val VIEW_TYPE_SECTION = 0
        const val VIEW_TYPE_PRODUCT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SECTION -> {
                val binding = ItemSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SectionViewHolder(binding)
            }
            VIEW_TYPE_PRODUCT -> {
                val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProductViewHolder(binding, onItemClicked)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is MenuItem.Section -> (holder as SectionViewHolder).bind(item)
            is MenuItem.Product -> (holder as ProductViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MenuItem.Section -> VIEW_TYPE_SECTION
            is MenuItem.Product -> VIEW_TYPE_PRODUCT
        }
    }

    fun setItems(newItems: List<MenuItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class SectionViewHolder(
    private val binding: ItemSectionBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(section: MenuItem.Section) {
        binding.sectionTitle.text = section.title
    }
}

class ProductViewHolder(
    private val binding: ItemProductBinding,
    private val onItemClicked: (MenuItem.Product) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var currentProduct: MenuItem.Product? = null

    init {
        itemView.setOnClickListener {
            currentProduct?.let { onItemClicked(it) }
        }
    }

    fun bind(product: MenuItem.Product) {
        currentProduct = product
        binding.productName.text = product.name
        binding.productPrice.text = "R$ " + String.format("%.2f", product.price)
    }
}

package com.example.cardapioapp.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cardapioapp.productdetails.ProductDTO
import com.example.cardapioapp.databinding.ActivityOrderAdapterBinding

class OrderAdapter (private val orderViewModel: OrderViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<ProductDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ActivityOrderAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, orderViewModel)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = items[position]
        (holder as OrderViewHolder).bind(product)
    }

    fun setItems(newItems: List<ProductDTO>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

class OrderViewHolder(
    private val binding: ActivityOrderAdapterBinding,
    private val orderViewModel: OrderViewModel
) : RecyclerView.ViewHolder(binding.root)  {

    fun bind(product: ProductDTO) {
        binding.produtoNome.text = product.name
        binding.produtoQuantidade.text = product.quantity.toString()
        binding.produtoValorUnitario.text = String.format("%.2f", product.price)
        binding.produtoValorTotal.text = String.format("%.2f", (product.price * product.quantity))

        binding.btnIncrease.setOnClickListener {
            orderViewModel.alterItemQuantity(product.name, 1)
        }

        binding.btnDecrease.setOnClickListener {
            orderViewModel.alterItemQuantity(product.name, 2)
        }

        binding.btnRemove.setOnClickListener {
            orderViewModel.removeItem(product.name)
        }
    }
}

package com.example.finapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.R
import com.example.finapp.data.TransactionEntity
import com.example.finapp.data.TransactionType
import com.example.finapp.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter :
    ListAdapter<TransactionEntity, TransactionAdapter.VH>(DIFF) {

        private val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        private val verde   = Color.parseColor("#388E3C")
        private val vermelho = Color.parseColor("#D32F2F")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) =
            holder.bind(getItem(position))

        inner class VH(private val b: ItemTransactionBinding) :
            RecyclerView.ViewHolder(b.root) {

            fun bind(tx: TransactionEntity) = with(b) {
                tvDescricao.text = tx.description
                tvTipo.text      = if (tx.type == TransactionType.CREDIT) "Crédito" else "Débito"
                tvValor.text     = nf.format(tx.value)

                if (tx.type == TransactionType.CREDIT) {
                    tvValor.text = nf.format(tx.value)
                    tvValor.setTextColor(verde)
                    ivTipo.setImageResource(R.drawable.ic_credit)
                } else {
                    tvValor.text = "-" + nf.format(tx.value)
                    tvValor.setTextColor(vermelho)
                    ivTipo.setImageResource(R.drawable.ic_debit)
                }

                if (tx.type == TransactionType.CREDIT) {
                    ivTipo.setImageResource(R.drawable.ic_credit)
                    tvValor.setTextColor(verde)
                } else {
                    ivTipo.setImageResource(R.drawable.ic_debit)
                    tvValor.setTextColor(vermelho)
                }
            }
        }

        companion object {
            private val DIFF = object : DiffUtil.ItemCallback<TransactionEntity>() {
                override fun areItemsTheSame(o: TransactionEntity, n: TransactionEntity) = o.id == n.id
                override fun areContentsTheSame(o: TransactionEntity, n: TransactionEntity) = o == n
            }
        }
}

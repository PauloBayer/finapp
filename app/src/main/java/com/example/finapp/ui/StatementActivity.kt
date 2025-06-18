package com.example.finapp.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.R
import com.example.finapp.adapter.TransactionAdapter
import com.example.finapp.data.AppDatabase
import com.example.finapp.data.TransactionType
import com.example.finapp.databinding.ActivityStatementBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class StatementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatementBinding
    private val dao by lazy { AppDatabase.get(this).dao() }
    private val adapter = TransactionAdapter()
    private val nf = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(this@StatementActivity)
            adapter = this@StatementActivity.adapter
            // Espaço vertical de 8 dp entre os itens
            addItemDecoration(
                VerticalSpace(resources.getDimensionPixelSize(R.dimen.item_spacing))
            )
        }

        // Spinner de filtro
        val filtros = listOf(
            getString(R.string.label_todos)  to null,
            getString(R.string.label_debito) to TransactionType.DEBIT,
            getString(R.string.label_credito) to TransactionType.CREDIT
        )

        val filtroAdapter = ArrayAdapter(
            this,
            R.layout.item_dropdown,
            filtros.map { it.first }
        ).apply { setDropDownViewResource(R.layout.item_dropdown) }

        binding.spinnerFiltro.adapter = filtroAdapter

        binding.spinnerFiltro.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    observarTransacoes(filtros[position].second)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        // Saldo em tempo real
        lifecycleScope.launch {
            dao.balance().collectLatest { binding.tvSaldo.text = nf.format(it) }
        }

        // Lista inicial
        observarTransacoes(null)
    }

    private fun observarTransacoes(type: TransactionType?) {
        lifecycleScope.launch {
            (type?.let { dao.getByType(it) } ?: dao.getAll())
                .collectLatest { adapter.submitList(it) }
        }
    }

    // ItemDecoration simples para espaçamento vertical
    private class VerticalSpace(private val px: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, v: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.bottom = px
        }
    }
}

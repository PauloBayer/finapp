package com.example.finapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finapp.R
import com.example.finapp.data.AppDatabase
import com.example.finapp.data.TransactionEntity
import com.example.finapp.data.TransactionType
import com.example.finapp.databinding.ActivityTransactionFormBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class TransactionFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionFormBinding
    private val dao by lazy { AppDatabase.get(this).dao() }
    private var selectedType = TransactionType.DEBIT
    private val nfBR = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Exibe saldo atual no topo
        lifecycleScope.launch {
            dao.balance().collectLatest { saldo ->
                binding.tvValorHint.text = nfBR.format(saldo)
            }
        }

        // Spinner Débito / Crédito
        val labels = listOf(
            getString(R.string.label_debito),
            getString(R.string.label_credito)
        )
        val spinnerAdapter = ArrayAdapter(this, R.layout.item_dropdown, labels).apply {
            setDropDownViewResource(R.layout.item_dropdown)
        }
        binding.spinnerTipo.adapter = spinnerAdapter
        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedType = if (pos == 0) TransactionType.DEBIT else TransactionType.CREDIT
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Máscara monetária
        setupCurrencyMask()

        // Botão Cadastrar
        binding.btnCadastrar.setOnClickListener {
            val raw   = binding.etValor.text.toString().replace(Regex("[^0-9]"), "")
            val valor = raw.toDoubleOrNull()?.div(100)
            val desc  = binding.etDescricao.text.toString().trim()

            when {
                valor == null || valor <= 0 -> toast("Valor inválido")
                desc.isEmpty()              -> toast("Descrição obrigatória")
                else -> lifecycleScope.launch {
                    dao.insert(
                        TransactionEntity(
                            type = selectedType,
                            description = desc,
                            value = valor
                        )
                    )
                    toast("Transação salva!")
                    finish()
                }
            }
        }
    }

    // Aplica formatação R$ enquanto o usuário digita
    private fun setupCurrencyMask() {
        val watcher = object : TextWatcher {
            private var current = ""
            override fun afterTextChanged(s: Editable) {
                if (s.toString() == current) return
                binding.etValor.removeTextChangedListener(this)

                val digits = s.toString().replace(Regex("[^0-9]"), "")
                val value  = digits.toDoubleOrNull()?.div(100) ?: 0.0
                current = nfBR.format(value)

                binding.etValor.setText(current)
                binding.etValor.setSelection(current.length)
                binding.etValor.addTextChangedListener(this)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etValor.addTextChangedListener(watcher)
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

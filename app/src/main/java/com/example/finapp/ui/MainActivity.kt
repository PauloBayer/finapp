package com.example.finapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.finapp.databinding.ActivityMainBinding
import androidx.core.view.WindowInsetsCompat
import com.example.finapp.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.finapp.data.AppDatabase
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding
    private val dao by lazy { AppDatabase.get(this).dao() }
    private val formatadorMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        configurarSaldo()
        configurarBotoes()
    }

    private fun configurarSaldo() {
        lifecycleScope.launch {
            dao.balance().collectLatest { saldo ->
                bind.tvSaldoAtual.text = formatadorMoeda.format(saldo)
            }
        }
    }

    private fun configurarBotoes() {
        bind.btnCadastro.setOnClickListener {
            startActivity(Intent(this, TransactionFormActivity::class.java))
        }

        bind.btnExtrato.setOnClickListener {
            startActivity(Intent(this, StatementActivity::class.java))
        }

        bind.btnSair.setOnClickListener { finishAffinity() }
    }
}
package com.example.finapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.finapp.databinding.ActivityMainBinding
import androidx.core.view.WindowInsetsCompat
import com.example.finapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.btnCadastro.setOnClickListener {
            startActivity(Intent(this, TransactionFormActivity::class.java))
        }

        bind.btnExtrato.setOnClickListener {
            startActivity(Intent(this, StatementActivity::class.java))
        }

        bind.btnSair.setOnClickListener { finishAffinity() }
    }
}
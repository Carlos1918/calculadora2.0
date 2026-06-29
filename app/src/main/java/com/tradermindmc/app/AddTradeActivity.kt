package com.tradermindmc.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tradermindmc.app.database.TradeDatabase
import com.tradermindmc.app.model.Trade
import kotlinx.coroutines.launch

class AddTradeActivity : AppCompatActivity() {

    private val pairs = listOf(
        "EURUSD","GBPUSD","AUDUSD","NZDUSD","USDCAD","USDCHF","USDJPY",
        "EURGBP","EURJPY","EURCHF","EURCAD","EURAUD","EURNZD",
        "GBPJPY","GBPCHF","GBPCAD","GBPAUD","GBPNZD",
        "AUDJPY","AUDCAD","AUDCHF","AUDNZD",
        "NZDJPY","NZDCAD","NZDCHF","CADJPY","CHFJPY",
        "XAUUSD (Oro)","XAGUSD (Plata)",
        "Boom 1000","Boom 500","Crash 1000","Crash 500","Crash 600",
        "Step Index","Volatility 10","Volatility 25","Volatility 50",
        "Volatility 75","Volatility 100","Range Break 100","Range Break 200"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trade)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_trade)

        val pairSpinner = findViewById<Spinner>(R.id.spinner_pair)
        val directionSpinner = findViewById<Spinner>(R.id.spinner_direction)
        val lotInput = findViewById<EditText>(R.id.input_lot)
        val entryInput = findViewById<EditText>(R.id.input_entry)
        val slInput = findViewById<EditText>(R.id.input_sl)
        val tpInput = findViewById<EditText>(R.id.input_tp)
        val resultInput = findViewById<EditText>(R.id.input_result)
        val pipsInput = findViewById<EditText>(R.id.input_pips)
        val notesInput = findViewById<EditText>(R.id.input_notes)
        val saveBtn = findViewById<Button>(R.id.btn_save)

        // Setup pair spinner
        val pairAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pairs)
        pairAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pairSpinner.adapter = pairAdapter

        // Setup direction spinner
        val directions = listOf(getString(R.string.buy), getString(R.string.sell))
        val dirAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, directions)
        dirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        directionSpinner.adapter = dirAdapter

        saveBtn.setOnClickListener {
            val pair = pairSpinner.selectedItem.toString()
            val direction = if (directionSpinner.selectedItemPosition == 0) "BUY" else "SELL"
            val lot = lotInput.text.toString().toDoubleOrNull() ?: 0.0
            val entry = entryInput.text.toString().toDoubleOrNull() ?: 0.0
            val sl = slInput.text.toString().toDoubleOrNull() ?: 0.0
            val tp = tpInput.text.toString().toDoubleOrNull() ?: 0.0
            val result = resultInput.text.toString().toDoubleOrNull() ?: 0.0
            val pips = pipsInput.text.toString().toDoubleOrNull() ?: 0.0
            val notes = notesInput.text.toString()

            val trade = Trade(
                pair = pair,
                direction = direction,
                lotSize = lot,
                entryPrice = entry,
                stopLoss = sl,
                takeProfit = tp,
                result = result,
                pips = pips,
                notes = notes
            )

            lifecycleScope.launch {
                TradeDatabase.getDatabase(this@AddTradeActivity).tradeDao().insert(trade)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

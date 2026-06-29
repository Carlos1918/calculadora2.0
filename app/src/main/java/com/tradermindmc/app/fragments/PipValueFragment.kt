package com.tradermindmc.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class PipValueFragment : Fragment() {

    private lateinit var adView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pip_value, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val symbolSpinner = view.findViewById<Spinner>(R.id.spinner_symbol)
        val lotInput = view.findViewById<EditText>(R.id.input_lot)
        val pipsInput = view.findViewById<EditText>(R.id.input_pips)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val pipValueText = view.findViewById<TextView>(R.id.text_pip_value)
        val totalValueText = view.findViewById<TextView>(R.id.text_total_value)

        val symbols = arrayOf("EURUSD", "GBPUSD", "USDJPY", "XAUUSD", "Boom 1000", "Crash 1000", "Crash 600", "Crash 500")
        val pipValues = mapOf(
            "EURUSD" to 10.0, "GBPUSD" to 10.0, "USDJPY" to 10.0,
            "XAUUSD" to 10.0, "Boom 1000" to 1.0, "Crash 1000" to 1.0,
            "Crash 600" to 0.6, "Crash 500" to 0.5
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, symbols)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        symbolSpinner.adapter = adapter

        calculateBtn.setOnClickListener {
            val lot = lotInput.text.toString().toDoubleOrNull()
            val pips = pipsInput.text.toString().toDoubleOrNull()
            val symbol = symbolSpinner.selectedItem.toString()
            val basePipValue = pipValues[symbol] ?: 10.0

            if (lot == null || pips == null) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pipValue = lot * basePipValue
            val total = pipValue * pips

            resultCard.visibility = View.VISIBLE
            pipValueText.text = String.format("$%.4f / pip", pipValue)
            totalValueText.text = String.format("%.0f pips = $%.2f en total", pips, total)
        }

        adView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onPause() { adView.pause(); super.onPause() }
    override fun onResume() { super.onResume(); adView.resume() }
    override fun onDestroy() { adView.destroy(); super.onDestroy() }
}

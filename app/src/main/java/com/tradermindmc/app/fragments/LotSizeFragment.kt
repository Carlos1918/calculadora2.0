package com.tradermindmc.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class LotSizeFragment : Fragment() {

    private lateinit var adView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lot_size, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val balanceInput = view.findViewById<EditText>(R.id.input_balance)
        val riskInput = view.findViewById<EditText>(R.id.input_risk)
        val slInput = view.findViewById<EditText>(R.id.input_sl)
        val pairSpinner = view.findViewById<Spinner>(R.id.spinner_pair)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val resultValue = view.findViewById<TextView>(R.id.result_value)
        val resultSubtitle = view.findViewById<TextView>(R.id.result_subtitle)

        // Configurar spinner de pares
        val pairs = arrayOf(
            "EURUSD / GBPUSD",
            "USDJPY",
            "XAUUSD (Oro)",
            "Boom 1000",
            "Crash 1000",
            "Crash 600",
            "Crash 500"
        )
        val pipValues = mapOf(
            "EURUSD / GBPUSD" to 10.0,
            "USDJPY" to 10.0,
            "XAUUSD (Oro)" to 10.0,
            "Boom 1000" to 1.0,
            "Crash 1000" to 1.0,
            "Crash 600" to 0.6,
            "Crash 500" to 0.5
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pairs)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pairSpinner.adapter = adapter

        calculateBtn.setOnClickListener {
            val balance = balanceInput.text.toString().toDoubleOrNull()
            val risk = riskInput.text.toString().toDoubleOrNull()
            val sl = slInput.text.toString().toDoubleOrNull()
            val selectedPair = pairSpinner.selectedItem.toString()
            val pipValue = pipValues[selectedPair] ?: 10.0

            if (balance == null || risk == null || sl == null || sl == 0.0) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val riskAmount = balance * (risk / 100)
            val lotSize = riskAmount / (sl * pipValue)

            resultCard.visibility = View.VISIBLE
            resultValue.text = String.format("%.2f lotes", lotSize)
            resultSubtitle.text = String.format("Arriesgando $%.2f de tu cuenta", riskAmount)
        }

        // Cargar anuncio
        adView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}

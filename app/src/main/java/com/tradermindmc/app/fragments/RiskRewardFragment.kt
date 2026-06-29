package com.tradermindmc.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class RiskRewardFragment : Fragment() {

    private lateinit var adView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_reward, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entryInput = view.findViewById<EditText>(R.id.input_entry)
        val slInput = view.findViewById<EditText>(R.id.input_sl)
        val tpInput = view.findViewById<EditText>(R.id.input_tp)
        val directionSpinner = view.findViewById<Spinner>(R.id.spinner_direction)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val riskPipsText = view.findViewById<TextView>(R.id.text_risk_pips)
        val profitPipsText = view.findViewById<TextView>(R.id.text_profit_pips)
        val ratioText = view.findViewById<TextView>(R.id.text_ratio)
        val messageText = view.findViewById<TextView>(R.id.text_message)

        val directions = arrayOf("Compra (Buy)", "Venta (Sell)")
        val dirAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, directions)
        dirAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        directionSpinner.adapter = dirAdapter

        calculateBtn.setOnClickListener {
            val entry = entryInput.text.toString().toDoubleOrNull()
            val sl = slInput.text.toString().toDoubleOrNull()
            val tp = tpInput.text.toString().toDoubleOrNull()

            if (entry == null || sl == null || tp == null) {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isBuy = directionSpinner.selectedItemPosition == 0
            val riskPips: Double
            val profitPips: Double

            if (isBuy) {
                riskPips = Math.abs(entry - sl) * 10000
                profitPips = Math.abs(tp - entry) * 10000
            } else {
                riskPips = Math.abs(sl - entry) * 10000
                profitPips = Math.abs(entry - tp) * 10000
            }

            val ratio = if (riskPips > 0) profitPips / riskPips else 0.0

            resultCard.visibility = View.VISIBLE
            riskPipsText.text = String.format("%.1f pips", riskPips)
            profitPipsText.text = String.format("%.1f pips", profitPips)
            ratioText.text = String.format("1 : %.2f", ratio)

            when {
                ratio >= 2.0 -> {
                    ratioText.setTextColor(resources.getColor(R.color.green, null))
                    messageText.text = "✓ Excelente ratio — buena gestión de riesgo"
                }
                ratio >= 1.5 -> {
                    ratioText.setTextColor(resources.getColor(R.color.green, null))
                    messageText.text = "✓ Ratio aceptable — recomendado para operar"
                }
                ratio >= 1.0 -> {
                    ratioText.setTextColor(resources.getColor(R.color.orange, null))
                    messageText.text = "⚠ Ratio bajo — considera mejorar el take profit"
                }
                else -> {
                    ratioText.setTextColor(resources.getColor(R.color.red, null))
                    messageText.text = "✗ Ratio negativo — el riesgo supera el beneficio"
                }
            }
        }

        adView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onPause() { adView.pause(); super.onPause() }
    override fun onResume() { super.onResume(); adView.resume() }
    override fun onDestroy() { adView.destroy(); super.onDestroy() }
}

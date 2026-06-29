package com.tradermindmc.app.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tradermindmc.app.R
import com.tradermindmc.app.database.TradeDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("tradermind_prefs", Context.MODE_PRIVATE)

        val inputBalance = view.findViewById<EditText>(R.id.input_account_balance)
        val inputGoal = view.findViewById<EditText>(R.id.input_goal)
        val inputDrawdown = view.findViewById<EditText>(R.id.input_max_drawdown)
        val inputMonthly = view.findViewById<EditText>(R.id.input_monthly_target)
        val saveBtn = view.findViewById<Button>(R.id.btn_save_account)
        val tvProgress = view.findViewById<TextView>(R.id.tv_goal_progress)
        val tvDrawdownStatus = view.findViewById<TextView>(R.id.tv_drawdown_status)
        val tvMonthlyProfit = view.findViewById<TextView>(R.id.tv_monthly_profit)
        val tvProjection = view.findViewById<TextView>(R.id.tv_projection)

        // Load saved values
        inputBalance.setText(prefs.getString("balance", ""))
        inputGoal.setText(prefs.getString("goal", ""))
        inputDrawdown.setText(prefs.getString("drawdown", "5"))
        inputMonthly.setText(prefs.getString("monthly_target", ""))

        saveBtn.setOnClickListener {
            prefs.edit()
                .putString("balance", inputBalance.text.toString())
                .putString("goal", inputGoal.text.toString())
                .putString("drawdown", inputDrawdown.text.toString())
                .putString("monthly_target", inputMonthly.text.toString())
                .apply()
            Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show()
            updateStats(view)
        }

        updateStats(view)
    }

    private fun updateStats(view: View) {
        val prefs = requireContext().getSharedPreferences("tradermind_prefs", Context.MODE_PRIVATE)
        val balance = prefs.getString("balance", "0")?.toDoubleOrNull() ?: 0.0
        val goal = prefs.getString("goal", "0")?.toDoubleOrNull() ?: 0.0
        val maxDrawdown = prefs.getString("drawdown", "5")?.toDoubleOrNull() ?: 5.0
        val monthlyTarget = prefs.getString("monthly_target", "0")?.toDoubleOrNull() ?: 0.0

        val tvProgress = view.findViewById<TextView>(R.id.tv_goal_progress)
        val tvDrawdownStatus = view.findViewById<TextView>(R.id.tv_drawdown_status)
        val tvMonthlyProfit = view.findViewById<TextView>(R.id.tv_monthly_profit)
        val tvProjection = view.findViewById<TextView>(R.id.tv_projection)

        val db = TradeDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val totalProfit = db.tradeDao().getTotalProfit() ?: 0.0
            val currentBalance = balance + totalProfit

            // Goal progress
            if (goal > 0 && balance > 0) {
                val progress = ((currentBalance - balance) / (goal - balance) * 100).coerceIn(0.0, 100.0)
                activity?.runOnUiThread {
                    tvProgress.text = String.format(getString(R.string.goal_progress), currentBalance, goal, progress)
                    tvProgress.setTextColor(Color.parseColor(if (progress >= 50) "#059669" else "#4F46E5"))
                }
            }

            // Monthly profit
            val startOfMonth = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }.timeInMillis
            val monthlyTrades = db.tradeDao().getTradesSince(startOfMonth)
            val monthlyProfit = monthlyTrades.sumOf { it.result }

            // Drawdown check
            val drawdownAmount = balance * (maxDrawdown / 100)

            activity?.runOnUiThread {
                tvMonthlyProfit.text = String.format("%s$%.2f", if (monthlyProfit >= 0) "+" else "", monthlyProfit)
                tvMonthlyProfit.setTextColor(Color.parseColor(if (monthlyProfit >= 0) "#059669" else "#DC2626"))

                if (monthlyProfit < -drawdownAmount) {
                    tvDrawdownStatus.text = getString(R.string.drawdown_exceeded)
                    tvDrawdownStatus.setTextColor(Color.parseColor("#DC2626"))
                } else {
                    tvDrawdownStatus.text = String.format(getString(R.string.drawdown_safe), maxDrawdown)
                    tvDrawdownStatus.setTextColor(Color.parseColor("#059669"))
                }

                // Projection
                if (monthlyTarget > 0 && balance > 0) {
                    val months = if (monthlyTarget > 0) Math.ceil((goal - balance) / monthlyTarget).toInt() else 0
                    tvProjection.text = String.format(getString(R.string.projection_months), months, monthlyTarget)
                }
            }
        }
    }
}

package com.tradermindmc.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tradermindmc.app.R
import com.tradermindmc.app.database.TradeDatabase
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvWinrate = view.findViewById<TextView>(R.id.tv_winrate)
        val tvTotalTrades = view.findViewById<TextView>(R.id.tv_total_trades)
        val tvWins = view.findViewById<TextView>(R.id.tv_wins)
        val tvLosses = view.findViewById<TextView>(R.id.tv_losses)
        val tvTotalProfit = view.findViewById<TextView>(R.id.tv_total_profit)
        val tvBestTrade = view.findViewById<TextView>(R.id.tv_best_trade)
        val tvWorstTrade = view.findViewById<TextView>(R.id.tv_worst_trade)
        val tvStreak = view.findViewById<TextView>(R.id.tv_streak)

        val db = TradeDatabase.getDatabase(requireContext())

        db.tradeDao().getAllTrades().observe(viewLifecycleOwner) { trades ->
            lifecycleScope.launch {
                val total = db.tradeDao().getTotalTrades()
                val wins = db.tradeDao().getWinningTrades()
                val losses = total - wins
                val totalProfit = db.tradeDao().getTotalProfit() ?: 0.0
                val bestTrade = db.tradeDao().getBestTrade() ?: 0.0
                val worstTrade = db.tradeDao().getWorstTrade() ?: 0.0
                val winrate = if (total > 0) (wins.toDouble() / total * 100) else 0.0

                // Calculate current streak
                var streak = 0
                var streakType = ""
                if (trades.isNotEmpty()) {
                    val closedTrades = trades.filter { it.status == "CLOSED" }
                    if (closedTrades.isNotEmpty()) {
                        val firstResult = closedTrades[0].result
                        streakType = if (firstResult >= 0) "W" else "L"
                        for (t in closedTrades) {
                            if ((t.result >= 0 && streakType == "W") || (t.result < 0 && streakType == "L")) streak++
                            else break
                        }
                    }
                }

                activity?.runOnUiThread {
                    tvWinrate.text = String.format("%.1f%%", winrate)
                    tvWinrate.setTextColor(Color.parseColor(if (winrate >= 50) "#059669" else "#DC2626"))
                    tvTotalTrades.text = total.toString()
                    tvWins.text = wins.toString()
                    tvLosses.text = losses.toString()
                    tvTotalProfit.text = String.format("%s$%.2f", if (totalProfit >= 0) "+" else "", totalProfit)
                    tvTotalProfit.setTextColor(Color.parseColor(if (totalProfit >= 0) "#059669" else "#DC2626"))
                    tvBestTrade.text = String.format("+$%.2f", bestTrade)
                    tvWorstTrade.text = String.format("-$%.2f", Math.abs(worstTrade))
                    tvStreak.text = if (streak > 0) "$streak $streakType" else "-"
                    tvStreak.setTextColor(Color.parseColor(if (streakType == "W") "#059669" else "#DC2626"))
                }
            }
        }
    }
}

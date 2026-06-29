package com.tradermindmc.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradermindmc.app.fragments.LotSizeFragment
import com.tradermindmc.app.fragments.RiskRewardFragment
import com.tradermindmc.app.fragments.PipValueFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar AdMob
        MobileAds.initialize(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Mostrar primer fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LotSizeFragment())
                .commit()
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_lot_size -> LotSizeFragment()
                R.id.nav_risk_reward -> RiskRewardFragment()
                R.id.nav_pip_value -> PipValueFragment()
                else -> LotSizeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }
}

package com.tradermindmc.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradermindmc.app.fragments.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LotSizeFragment())
                .commit()
            bottomNav.selectedItemId = R.id.nav_lot_size
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_lot_size -> LotSizeFragment()
                R.id.nav_journal -> JournalFragment()
                R.id.nav_stats -> StatsFragment()
                R.id.nav_sessions -> SessionsFragment()
                R.id.nav_account -> AccountFragment()
                else -> LotSizeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }
}

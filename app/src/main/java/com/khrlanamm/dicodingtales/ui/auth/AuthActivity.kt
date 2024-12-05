package com.khrlanamm.dicodingtales.ui.auth

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.khrlanamm.dicodingtales.R
import com.khrlanamm.dicodingtales.ui.auth.helper.AuthAdapter
import com.khrlanamm.dicodingtales.ui.auth.login.LoginFragment
import com.khrlanamm.dicodingtales.ui.auth.register.RegisterFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var footer: ImageView
    var v: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        val authAdapter = AuthAdapter(this)
        viewPager.adapter = authAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.btn_login)
                1 -> getString(R.string.btn_register)
                else -> ""
            }
        }.attach()

        footer = findViewById(R.id.footer)
        footer.translationY = 300f
        footer.alpha = v

        footer.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(400)
            .start()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = tab.position
                    if (position == 0) {
                        val fragment = supportFragmentManager.findFragmentByTag("f0")
                        if (fragment is LoginFragment) {
                            fragment.onViewCreated(fragment.requireView(), null)
                        }
                    } else if (position == 1) {
                        val fragment = supportFragmentManager.findFragmentByTag("f1")
                        if (fragment is RegisterFragment) {
                            fragment.onViewCreated(fragment.requireView(), null)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    fun getViewPager(): ViewPager2 {
        return viewPager
    }
}

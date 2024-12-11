package com.khrlanamm.dicodingtales

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.khrlanamm.dicodingtales.data.local.pref.SessionManager
import com.khrlanamm.dicodingtales.databinding.ActivityMainBinding
import com.khrlanamm.dicodingtales.helper.HomeAdapter
import com.khrlanamm.dicodingtales.helper.HomeFactory
import com.khrlanamm.dicodingtales.helper.HomeViewModel
import com.khrlanamm.dicodingtales.helper.LoadingStateAdapter
import com.khrlanamm.dicodingtales.ui.auth.onboarding.OnboardingActivity
import com.khrlanamm.dicodingtales.ui.detail.DetailActivity
import com.khrlanamm.dicodingtales.ui.maps.MapsActivity
import com.khrlanamm.dicodingtales.ui.upload.UploadActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: HomeAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        HomeFactory.Companion.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setLogo(R.drawable.header_logo)
            setDisplayUseLogoEnabled(true)
        }

        setupRecyclerView()
        checkAuthentication()
        observeViewModel()

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val token = sessionManager.getAuthToken()
        if (token != null) {
            homeViewModel.stories(token)
        }
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            startActivity(intent)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@MainActivity.adapter
        }
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun checkAuthentication() {
        val token = sessionManager.getAuthToken()
        if (token == null) {
            navigateToOnboarding()
        } else {
            homeViewModel.stories(token)
        }
    }

    private fun observeViewModel() {
        val token = sessionManager.getAuthToken()
        if (token != null) {
            homeViewModel.stories(token).observe(this) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }
        homeViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun navigateToOnboarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                showLogoutConfirmation()
                true
            }

            R.id.action_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            R.id.action_map -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_logout))
            .setMessage(getString(R.string.confirm_logout_detail))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        sessionManager.clearAuthToken()
        navigateToOnboarding()
    }
}

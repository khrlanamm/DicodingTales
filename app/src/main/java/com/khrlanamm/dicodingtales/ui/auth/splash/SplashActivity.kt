package com.khrlanamm.dicodingtales.ui.auth.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.khrlanamm.dicodingtales.R
import com.khrlanamm.dicodingtales.databinding.ActivitySplashBinding
import com.khrlanamm.dicodingtales.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }, 7000)
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.txtSplash, View.ALPHA, 1f).setDuration(1000)
        val identity = ObjectAnimator.ofFloat(binding.imgIdentity, View.ALPHA, 1f).setDuration(1000)
        val img = ObjectAnimator.ofFloat(binding.imgSplash, View.ALPHA, 1f).setDuration(1000)
        val desc = ObjectAnimator.ofFloat(binding.txtDescSplash, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(title, identity, img, desc)
            start()
        }
    }


}
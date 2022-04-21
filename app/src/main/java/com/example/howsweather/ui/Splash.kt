package com.example.howsweather.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import com.example.howsweather.MainActivity
import com.example.howsweather.R
import com.example.howsweather.databinding.ActivitySplashBinding

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        var thread = Thread() {
            binding.splash.setAnimation(R.raw.splash)
        }.start()

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        }, 4500)
    }
}
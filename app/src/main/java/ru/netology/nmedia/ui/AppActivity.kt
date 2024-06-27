package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = findViewById<StatsView>(R.id.stats)

        view.postDelayed({
            view.data = listOf(
                0.25F,
                0.25F,
                0.25F,
                0.25F,
            )
        }, 100)

        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.animation))
    }
}
package ltd.royalgreen.pacecloud.mainactivitymodule

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.splash_activity.*
import kotlinx.coroutines.*
import ltd.royalgreen.pacecloud.R
import ltd.royalgreen.pacecloud.loginmodule.LoginActivity
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                runBlocking {
                    launch {
                        delay(1500L)
                    }

                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                    finish()
                }
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })

        logo.startAnimation(animation)
    }
}

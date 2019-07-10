package ltd.royalgreen.pacecloud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.splash_activity.*

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DISPLAY_LENGTH = 3000L
    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        if(savedInstanceState == null) animateLogo()
        //Handler for splash
        Handler().postDelayed(object: Runnable {
            override fun run() {
                stopAnimatingLogo()
            }

        }, SPLASH_DISPLAY_LENGTH)
    }

    private fun animateLogo() {
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        logo.startAnimation(animation)
    }

    private fun stopAnimatingLogo() {
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation_back)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }

            override fun onAnimationStart(p0: Animation?) {

            }
        })
        logo.startAnimation(animation)
    }
}

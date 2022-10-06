package com.enflash.mobile.storeapp.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.application.AppHelper
import com.enflash.mobile.storeapp.main.MainActivity
import com.enflash.mobile.storeapp.mqtt.ServiceActivity
import com.enflash.mobile.storeapp.utils.FileLog
import com.enflash.mobile.storeapp.utils.PreferencesManager


class SplashScreenActivity : AppCompatActivity() {

    private var cognitoUser: CognitoUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppThemeSplash)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        try{
            AppHelper.init(applicationContext)
            cognitoUser = AppHelper.getPool().currentUser
            startService(Intent(applicationContext, ServiceActivity::class.java))
        }catch (ex: Exception){
            if(ex.message != null){
                FileLog.writeToConsole(ex.message!!)
            }
        }
        Handler().postDelayed({
            if (cognitoUser != null) {
                val telephone = cognitoUser!!.userId
                if (telephone != null && telephone.isNotEmpty()) {
                    AppHelper.setUser(telephone)
                    cognitoUser!!.getSessionInBackground(object : AuthenticationHandler {
                        override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
                            if(PreferencesManager.getConfigStoreOperationStatus() == true) {
                                if (ServiceActivity.mqttManager!!.status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                                    ServiceActivity.connect()
                                }
                            }
                            val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        }

                        override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation, userId: String) {}
                        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation) {}
                        override fun authenticationChallenge(continuation: ChallengeContinuation) {}
                        override fun onFailure(exception: Exception) {
                            goTo()
                        }
                    })
                } else {
                    goTo()
                }
            } else {
                goTo()
            }
        }, SPLASH_TIME_OUT.toLong())
    }

    private fun goTo() {
        val i = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    companion object {
        const val SPLASH_TIME_OUT = 2000
        const val STARTUP_DELAY = 300
        const val ANIM_ITEM_DURATION = 1000
        const val ITEM_DELAY = 300
    }
}
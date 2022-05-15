package com.oncobuddy.app.views.activities

import com.oncobuddy.app.utils.base_classes.BaseActivity
import android.os.Bundle
import android.view.WindowManager
import com.oncobuddy.app.R
import com.oncobuddy.app.utils.CommonMethods
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.databinding.ActivitySplashNewBinding
import android.view.animation.LinearInterpolator
import com.oncobuddy.app.utils.Constants


/****************************************************************************
 * SplashActivity
 *
 * @CreatedDate:
 * @ModifiedBy: not yet
 * @ModifiedDate: not yet
 * @purpose:This Class is user for SplashActivity.
 */
class NovSplashActivity : BaseActivity(){

    private val SPLASH_TIME_OUT = 3000
    private lateinit var binding : ActivitySplashNewBinding
    private val screenRecordingLog = "scr_recording_log"
    private val SCREEN_RECORD_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        FourBaseCareApp.activityFromApp = this
        //startRecordingScreen()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_new)

        doAnimateAndRedirectToScreen()
        binding.tvVersionName.text = "Version "+getAppVersion(FourBaseCareApp.activityFromApp)
    }

    private fun setClickListeners() {
        binding.tvGetStarted.setOnClickListener(View.OnClickListener {
            gotoSignUpActivity()
        })

        binding.tvSignIn.setOnClickListener(View.OnClickListener {
            gotoSignInActivity()
        })
    }

    private fun doAnimateAndRedirectToScreen() {
        animateTitle()
        /*animateGetStarted()
        animateSignIn()
        setClickListeners()
*/

        Handler().postDelayed({ // Do delayed stuff!
            gotoRelatedActivity()
        }, SPLASH_TIME_OUT.toLong())


        if (FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")!!
                .isEmpty()
        ) {
            animateGetStarted()
            animateSignIn()
            setClickListeners()
        } else {
            if (!getUserObject().role.equals(Constants.ROLE_NEW_USER, ignoreCase = true)) {
                binding.tvGetStarted.visibility = View.GONE
                binding.linSignin.visibility = View.GONE
            }
        }
        if (!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")!!
                .isEmpty() && !getUserObject().role.equals(
                Constants.ROLE_NEW_USER,
                ignoreCase = true
            )
        ) {
            binding.tvGetStarted.visibility = View.GONE
            binding.linSignin.visibility = View.GONE


        } else {
            animateGetStarted()
            animateSignIn()
            setClickListeners()
        }

        /*animateGetStarted()
        animateSignIn()
        setClickListeners()*/
    }

    private fun animateSignIn() {
        binding.linSignin.alpha = 0.0f
        binding.linSignin.animate().apply {
            interpolator = LinearInterpolator()
            duration = 1000
            alpha(1f)
            startDelay = 1500
            start()
        }
    }

    private fun animateTitle() {
        binding.linBottom.alpha = 1.0f
        binding.linBottom.animate().apply {
            interpolator = LinearInterpolator()
            duration = 1000
            startDelay = 1500
            alpha(0f)
            start()
        }
    }

    private fun animateGetStarted() {
        binding.tvGetStarted.alpha = 0.0f
        binding.tvGetStarted.animate().apply {
            interpolator = LinearInterpolator()
            duration = 1000
            alpha(1f)
            startDelay = 1500
            start()
        }
    }

    private fun gotoSignInActivity() {
        val mLoginIntent =
            Intent(this@NovSplashActivity, NovSignInActivity::class.java)
        startActivity(mLoginIntent)
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        finish()
    }

    private fun gotoSignUpActivity() {
        val mLoginIntent =
            Intent(this@NovSplashActivity, NovSignupActivity::class.java)
        startActivity(mLoginIntent)
        overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        //finish()
    }


    override fun init() {}

    /**
     * Goto related activity
     * Takes role fromthe shared preferences and opens activity accordingly
     */
    private fun gotoRelatedActivity() {
        Log.d("auth_token_log", "came here")
        if (!FourBaseCareApp.sharedPreferences.getString(Constants.PREF_USER_OBJ, "")!!
                .isEmpty()
        ) {
            Log.d("auth_token_log", "Login is not empty")
            Log.d("auth_token_log", "Login called")
            Log.d("auth_token_log", "Login id " + getUserObject().userIdd)
            Log.d("auth_token_log", "Login name " + getUserObject().firstName)
            Log.d("token_log", "token created" + getUserObject().authToken)
            Log.d("auth_token_log", "Role "+getUserObject().role)
            if (getUserObject().role.equals(Constants.ROLE_PATIENT, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSplashActivity, PatientLandingActivity::class.java,true, true)
            }else if (getUserObject().role.equals(Constants.ROLE_PATIENT_CARE_GIVER, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                Log.d("linked_cc_log","Patient cg saved with cc "+getUserObject().patientConnected)
                Log.d("linked_cc_log","Patient cg saved with cc "+getUserObject().linkedCareCode)
                if(getUserObject().linkedCareCode.isNullOrEmpty()){
                    CommonMethods.changeActivity(this@NovSplashActivity, NovCGAccountSetupActivity::class.java,true, true)
                }else{
                    CommonMethods.changeActivity(this@NovSplashActivity, PatientLandingActivity::class.java,true, true)

                }

                //CommonMethods.changeActivity(this@NovSplashActivity, NovCGAccountSetupActivity::class.java,true, true)
            }
            else if (getUserObject().role.equals(
                    Constants.ROLE_CARE_COMPANION,
                    ignoreCase = true
                )
            ) {
                Constants.IS_DOCTOR_LOGGED_IN = false
                CommonMethods.changeActivity(this@NovSplashActivity, PatientLandingActivity::class.java,true, true)
            } else if (getUserObject().role.equals(Constants.ROLE_NEW_USER, ignoreCase = true)) {
                //mLoginIntent = Intent(this@NovSplashActivity, IntroductionScreensActivity::class.java)
            } else if (getUserObject().role.equals(Constants.ROLE_DOCTOR, ignoreCase = true)) {
                Constants.IS_DOCTOR_LOGGED_IN = true
                //mLoginIntent = Intent(this@NovSplashActivity, PatientLandingActivity::class.java)
                CommonMethods.changeActivity(this@NovSplashActivity, NovDoctorLandingActivity::class.java,true, true)
            }
        } else {
            //gotoSignInActivity()
       /*     Handler().postDelayed({ // Do delayed stuff!
                gotoSignInActivity()
            }, SPLASH_TIME_OUT.toLong())*/
        }

    }



    // Recording events
    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Start screen recording
                hbRecorder.startScreenRecording(data, resultCode, this)
                Log.d(screenRecordingLog, "Recording onactivity started ")
            } else {
                Log.d(screenRecordingLog, "Recording onactivity not okay")
            }
        }
    }*/



}
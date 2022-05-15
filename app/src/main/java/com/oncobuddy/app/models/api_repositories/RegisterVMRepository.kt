package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.RegistrationVMImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.nov_signup.SignupResponse
import com.oncobuddy.app.models.pojo.registration_process.NovRegistration
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * Responsitory to handle signup network process
 */
class RegisterVMRepository : RegistrationVMImplementor{

    private var registrationApicall : Call<SignupResponse?>? = null

    interface ApiCallBack<T>{
        fun onSuccess(responseData: Response<SignupResponse?>)
        fun onError(message : String?)
    }





    override fun addPatient(
        registrationInput: NovRegistration,
        callback: ApiCallBack<SignupResponse?>
    ) {

        registrationApicall = ApiClient.build()?.doPatientRegister(registrationInput)

        registrationApicall?.enqueue(object : Callback<SignupResponse?>{
            override fun onFailure(call: Call<SignupResponse?>?, t: Throwable?) {
                Log.d("signup_log", "Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<SignupResponse?>, response: Response<SignupResponse?>) {
                if(response.code() == 400){
                    Log.d("signup_log", "Catched")
                }

                if(response.isSuccessful){
                    callback.onSuccess(response)
                    Log.d("signup_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("signup_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("signup_log","API Failed here "+ignored.toString())
                    }

                    Log.d("signup_log", "Err2 "+response.message())
                }
            }


            /* override fun onResponse(call: Call<BaseResponse?>, response: Response<BaseResponse?>) {
                 response?.body()?.let {baseResponse ->
                     if(response.code() == 400){
                         Log.d("login_log", "Catched")
                     }

                     if(response.isSuccessful){
                         callback.onSuccess(baseResponse)
                         Log.d("login_log", "success2 "+response.message())
                     }else{
                         callback.onError(response.message())
                         Log.d("login_log", "Err2 "+response.message())
                     }
                 }
             }*/



        })



    }


    override fun cancel() {
          registrationApicall?.cancel()
    }
}
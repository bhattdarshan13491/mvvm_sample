package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.BaseImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.CareGiverInput
import com.oncobuddy.app.models.pojo.ComplaintInput
import com.oncobuddy.app.models.pojo.DeleteAccountInput
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.aws_credentials.AwsCredentialsResponse
import com.oncobuddy.app.models.pojo.bank_acct_details.BankAccountInput
import com.oncobuddy.app.models.pojo.care_giver_details.AddUserByCCInput
import com.oncobuddy.app.models.pojo.care_giver_details.AssignCGInput
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverResponse
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamResponse
import com.oncobuddy.app.models.pojo.daily_questions.AnswerQuestionInput
import com.oncobuddy.app.models.pojo.daily_questions.DailyQuestionsListResponse
import com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityInput
import com.oncobuddy.app.models.pojo.doctor_certification.AddCertificateInput
import com.oncobuddy.app.models.pojo.doctor_locations.DOctorLOcationListResponse
import com.oncobuddy.app.models.pojo.doctor_locations.EstablishmentInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.DoctorAvailibilityResponse
import com.oncobuddy.app.models.pojo.doctor_update.DoctorAvailabilityReq
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.ecrf_events.EcrfventsResponse
import com.oncobuddy.app.models.pojo.education_degrees.AddEducationInput
import com.oncobuddy.app.models.pojo.education_degrees.DegreesResponse
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactInput
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactsListResponse
import com.oncobuddy.app.models.pojo.genetic_report.GeneticResponse
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalListingResponse
import com.oncobuddy.app.models.pojo.invite.InviteInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetailsByCGResponse
import com.oncobuddy.app.models.pojo.patient_transactions.PatientTransactionsResponse
import com.oncobuddy.app.models.pojo.profile.CancerTypesListResponse
import com.oncobuddy.app.models.pojo.profile.WalletBalanceResponse
import com.oncobuddy.app.models.pojo.profile.WalletTransactionsResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import com.oncobuddy.app.utils.CameraCapturerCompat
import com.oncobuddy.app.utils.Constants
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Responsitory to handle all patient care giver and care companion profile related calls
 */
class ProfileVMRepository : ProfileVMImplementor {

    
    private var baseResponseCall : Call<BaseResponse?>? = null
    private var addCTResponseCall : Call<AddCareTakerResponse?>? = null
    private var deleteCGResponseCall : Call<BaseResponse?>? = null
    private var deletePatientResponseCall : Call<BaseResponse?>? = null
    private var verifyDoctorByCcCall : Call<BaseResponse?>? = null
    private var assignDoctorResponseCall : Call<BaseResponse?>? = null
    private var ecrfEventsResponseCall : Call<EcrfventsResponse?>? = null
    private var findDoctorResponseCall : Call<FindDoctorResponse?>? = null
    private var updateProfileResponseCall : Call<LoginResponse?>? = null
    private var cancerListResponseCall : Call<CancerTypesListResponse?>? = null
    private var hospitalListResponseCall : Call<HospitalListingResponse?>? = null
    private var dailyQuestionsListResponseCall : Call<DailyQuestionsListResponse?>? = null
    private var contactsListResponseCall : Call<EmergencyContactsListResponse?>? = null
    private var answerQuestionResponseCall : Call<BaseResponse?>? = null
    private var uploadFileToS3Call : Call<BaseResponse?>? = null
    private var doctorListingApiCall : Call<DoctorsListingResponse?>? = null
    private var walletBalanceResponse: Call<WalletBalanceResponse>? = null
    private var walletTransactionsResponse : Call<WalletTransactionsResponse>? = null
    private var withdrawBalanceResponse: Call<BaseResponse>? = null
    private var checkPatientOnEcrfResponse: Call<BaseResponse?>? = null
    private var getPatientPdfResponse: Call<BaseResponse?>? = null
    private var getDoctorTimingsCall : Call<DoctorAvailibilityResponse?>? = null
    private var getCareTakerResponse: Call<CareGiverResponse?>? = null
    private var getAwsCredentialsResponse: Call<AwsCredentialsResponse?>? = null
    private var connectionResponseCall : Call<BaseResponse?>? = null
    private var inviteDoctorResponse: Call<BaseResponse?>? =  null
    private var complaintResponseCall: Call<BaseResponse?>? =  null
    private var educationResponseCall: Call<DegreesResponse?>? =  null
    private var deleteEduResponseCall: Call<BaseResponse?>? =  null
    private var addEduResponseCall: Call<BaseResponse?>? =  null
    private var addCertificationResponseCall: Call<BaseResponse?>? =  null
    private var getDoctorAvailibilityResponseCall: Call<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>? =  null
    private var updateAvailibilityResponseCall: Call<BaseResponse?>? =  null
    private var getLocationsResponseCall: Call<DOctorLOcationListResponse?>? =  null
    private var addLocationResponseCall: Call<BaseResponse?>? =  null
    private var editLocationResponseCall: Call<BaseResponse?>? =  null
    private var deleteLocationResponseCall: Call<BaseResponse?>? =  null
    private var patientTransactionResponseCall: Call<PatientTransactionsResponse?>? =  null
    private var deleteAccountResponseCall: Call<BaseResponse?>? =  null
    private var careTeamDetailsResponseCall: Call<CareTeamResponse?>? =  null
    private var geneticReportResponseCall: Call<GeneticResponse?>? =  null
    private var addBankAccountResponseCall : Call<BaseResponse?>? = null
    private var patientDetailsByCGResponseCall : Call<PatientDetailsByCGResponse?>? = null
    private var assignPatientToCG : Call<BaseResponse?>? = null
    private var updateCGDetails : Call<BaseResponse?>? = null


    interface GeneticReportResponseApiCallBack<T>{
        fun onSuccess(responseData : GeneticResponse?)
        fun onError(message : String?)
    }

    interface UpdateCGAPiApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface PatientDetailsByCGResponseApiCallBack<T>{
        fun onSuccess(responseData : PatientDetailsByCGResponse?)
        fun onError(message : String?)
    }

    interface AddBankAccountResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AssignPatientTOCGResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface BaseResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface DeleteAccountResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface InvteResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface PatientTransactionsResponseApiCallBack<T>{
        fun onSuccess(responseData : PatientTransactionsResponse?)
        fun onError(message : String?)
    }

    interface CareTeamDetailsApiCallBack<T>{
        fun onSuccess(responseData : CareTeamResponse?)
        fun onError(message : String?)
    }

    interface ComplaintResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddEduResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddCertificationResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface EducationApiCallBack<T>{
        fun onSuccess(responseData : DegreesResponse?)
        fun onError(message : String?)
    }

    interface DeleteEducationApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddCareTakerResponseApiCallBack<T>{
        fun onSuccess(responseData : AddCareTakerResponse?)
        fun onError(message : String?)
    }

    interface ConnectionResponseCallBack<T>{
        fun onConnectionResponseSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface DeleteCGResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }
    interface DeletePatientResponseApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AwsCredentialsResponseApiCallBack<T>{
        fun onCredentialSuccess(responseData : AwsCredentialsResponse?)
        fun onError(message : String?)
    }

    interface DoctorTimingsApiCallBack<T>{
        fun onSuccess(responseData : DoctorAvailibilityResponse?)
        fun onError(message : String?)
    }

    interface CareTakerResponseApiCallBack<T>{
        fun onSuccess(responseData : CareGiverResponse?)
        fun onError(message : String?)
    }

    interface EcrfEventsApiCallBack<T>{
        fun onSuccess(responseData : EcrfventsResponse?)
        fun onError(message : String?)
    }

    interface AssignDoctorResponseApiCallBack<T>{
        fun onAssignDoctorSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddUserByCCResponseApiCallBack<T>{
        fun onAddUserSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface VerifyDoctorByCCResponseApiCallBack<T>{
        fun onVerifySuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface FindDoctorResponseApiCallBack<T>{
        fun onFindDoctorSuccess(responseData : FindDoctorResponse?)
        fun onError(message : String?)
    }

    interface LoginResponseApiCallBack<T>{
        fun onSuccess(responseData : LoginResponse?)
        fun onError(message : String?)
    }

    interface CancerListResponseApiCallBack<T>{
        fun onSuccess(responseData : CancerTypesListResponse?)
        fun onError(message : String?)
    }

    interface HospitalListResponseApiCallBack<T>{
        fun onSuccess(responseData : HospitalListingResponse?)
        fun onError(message : String?)
    }

    interface ContactsListResponseApiCallBack<T>{
        fun onSuccess(responseData : EmergencyContactsListResponse?)
        fun onError(message : String?)
    }

    interface DailyQuestionsListResponseApiCallBack<T>{
        fun onSuccess(responseData :DailyQuestionsListResponse?)
        fun onError(message : String?)
    }

    interface WalletBalanceApiCallBack<T>{
        fun onSuccess(responseData: WalletBalanceResponse?)
        fun onError(message: String?)
    }

    interface WalletTransactionsApiCallBack<T>{
        fun onSuccess(responseData: WalletTransactionsResponse?)
        fun onError(message: String?)
    }

    interface GetDoctorAvailibilityAPiCallBack<T>{
        fun onSuccess(responseData : com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?)
        fun onError(message : String?)
    }

    interface UpdateDoctorAvailibilityAPiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddDoctorLocationAPiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface EditDoctorLocationAPiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface DeleteDoctorLocationAPiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface GetDoctorLocationAPiCallBack<T>{
        fun onSuccess(responseData : DOctorLOcationListResponse?)
        fun onError(message : String?)
    }



    override fun getMyDoctorsList(
        source: String,
        shouldGetALlied: Boolean,
        token: String,
        patientId: String,
        callback: AppointmentVMRepository.DoctorListApiCallBack<DoctorsListingResponse?>
    ) {
        if(source.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            doctorListingApiCall = ApiClient.build()?.getPatientDoctorsByCG(token)
        }else{
            if(shouldGetALlied){
                doctorListingApiCall = ApiClient.build()?.doGetAlliedDoctorsList(token)
            }else{
                doctorListingApiCall = ApiClient.build()?.getMyDoctors(token, patientId)
            }
        }



        doctorListingApiCall?.enqueue(object : Callback<DoctorsListingResponse?>{
            override fun onFailure(call: Call<DoctorsListingResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<DoctorsListingResponse?>?,
                                    response: Response<DoctorsListingResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onDoctorListSuccess(response.body())
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getWalletBalance(
        token: String,
        callback: WalletBalanceApiCallBack<WalletBalanceResponse?>
    ) {
        walletBalanceResponse = ApiClient.build()?.getWalletBalance(token)
        walletBalanceResponse?.enqueue(object : Callback<WalletBalanceResponse>{
            override fun onResponse(
                call: Call<WalletBalanceResponse>,
                response: Response<WalletBalanceResponse>
            ) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callback.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<WalletBalanceResponse>, t: Throwable) {
                callback.onError(t?.message)
            }
        })
    }

    override fun getWalletTransactions(
        token: String,
        callback: WalletTransactionsApiCallBack<WalletTransactionsResponse?>
    ) {
        walletTransactionsResponse = ApiClient.build()?.getWalletTransactions(token)
        walletTransactionsResponse?.enqueue(object : Callback<WalletTransactionsResponse>{
            override fun onResponse(
                call: Call<WalletTransactionsResponse>,
                response: Response<WalletTransactionsResponse>
            ) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callback.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<WalletTransactionsResponse>, t: Throwable) {
                callback.onError(t?.message)
            }

        })
    }

    override fun withdrawWalletBalance(
        token: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        withdrawBalanceResponse = ApiClient.build()?.withdrawBalance(token)
        withdrawBalanceResponse?.enqueue(object : Callback<BaseResponse>{
            override fun onResponse(call: Call<BaseResponse>,
                                    response: Response<BaseResponse>) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun checkPatientECRF(
        token: String,
        patientId: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        checkPatientOnEcrfResponse = ApiClient.build()?.doCheckPatientECRF(token, patientId)
        checkPatientOnEcrfResponse?.enqueue(object : Callback<BaseResponse?>{
            override fun onResponse(call: Call<BaseResponse?>,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun getEcrfSummaryPDF(
        token: String,
        patientId: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        getPatientPdfResponse = ApiClient.build()?.doGetEcrfSummaryPDF(token, patientId)
        getPatientPdfResponse?.enqueue(object : Callback<BaseResponse?>{
            override fun onResponse(call: Call<BaseResponse?>,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun getCareGiverDetails(
        token: String,
        callBack: CareTakerResponseApiCallBack<CareGiverResponse?>
    ) {
        getCareTakerResponse = ApiClient.build()?.doGetCareTakerDetails(token)
        getCareTakerResponse?.enqueue(object : Callback<CareGiverResponse?>{
            override fun onResponse(call: Call<CareGiverResponse?>,
                                    response: Response<CareGiverResponse?>) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<CareGiverResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun findDoctor(
        shouldFindPatient: Boolean,
        token: String,
        phoneNo: String,
        callBack: FindDoctorResponseApiCallBack<FindDoctorResponse?>
    ) {
        if(shouldFindPatient){
            findDoctorResponseCall = ApiClient.build()?.doFindPatient(token, phoneNo)
        }else{
            findDoctorResponseCall = ApiClient.build()?.doFindDoctor(token, phoneNo)
        }
        findDoctorResponseCall?.enqueue(object : Callback<FindDoctorResponse?>{
            override fun onResponse(call: Call<FindDoctorResponse?>,
                                    response: Response<FindDoctorResponse?>) {
                if(response.code() == 400){
                    Log.d("WALLET", "Catched")
                }

                Log.d("WALLET", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onFindDoctorSuccess(response.body())
                    Log.d("WALLET", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("WALLET","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("WALLET","API Failed here "+ignored.toString())
                    }

                    Log.d("WALLET", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<FindDoctorResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun assignDoctor(
        shouldAssignPatient: Boolean,
        token: String,
        doctorId: String,
        callBack: AssignDoctorResponseApiCallBack<BaseResponse?>
    ) {
        Log.d("assign_log", "came here")
        if(shouldAssignPatient){
            assignDoctorResponseCall = ApiClient.build()?.doAssignPatient(token, doctorId)
        }else{
            assignDoctorResponseCall = ApiClient.build()?.doAssignDoctor(token, doctorId)
        }
        assignDoctorResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onResponse(call: Call<BaseResponse?>,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("assign_log", "Catched")
                }

                Log.d("assign_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onAssignDoctorSuccess(response.body())
                    Log.d("assign_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("assign_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("assign_log","API Failed here "+ignored.toString())
                    }

                    Log.d("assign_log", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun getEcrfEvents(
        token: String,
        patientId: String,
        callBack: EcrfEventsApiCallBack<EcrfventsResponse?>
    ) {
        Log.d("assign_log", "came here")
        ecrfEventsResponseCall = ApiClient.build()?.doGetEcrfEvents(token, patientId)
        ecrfEventsResponseCall?.enqueue(object : Callback<EcrfventsResponse?>{
            override fun onResponse(call: Call<EcrfventsResponse?>,
                                    response: Response<EcrfventsResponse?>) {
                if(response.code() == 400){
                    Log.d("assign_log", "Catched")
                }

                Log.d("assign_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("assign_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("assign_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("assign_log","API Failed here "+ignored.toString())
                    }

                    Log.d("assign_log", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<EcrfventsResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun addUserByCC(
        shouldAddPatient: Boolean,
        token: String,
        addUserByCCInput: AddUserByCCInput,
        callBack: AddUserByCCResponseApiCallBack<BaseResponse?>
    ) {
        Log.d("assign_log", "came here")
        if(shouldAddPatient){
            assignDoctorResponseCall = ApiClient.build()?.doAddPatientByCC(addUserByCCInput,token)
            Log.d("assign_log", "patient api called")
        }else{
            assignDoctorResponseCall = ApiClient.build()?.doAddDoctorByCC(addUserByCCInput,token)
            Log.d("assign_log", "doctor api called")
        }
        assignDoctorResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onResponse(call: Call<BaseResponse?>,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("assign_log", "Catched")
                }

                Log.d("assign_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onAddUserSuccess(response.body())
                    Log.d("assign_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        callBack.onError(jObjError.getString("message"))
                        Log.d("assign_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("assign_log","API Failed here "+ignored.toString())
                    }

                    Log.d("assign_log", "Err2 "+response.message())
                }
            }

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                callBack.onError(t?.message)
            }
        })
    }

    override fun doAddEmergencyContact(
        emergencyContactInput: EmergencyContactInput?,
        token: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        baseResponseCall = ApiClient.build()?.doAddEmergencyContact(emergencyContactInput, token)
        baseResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun doInviteDoctor(
        inviteInput: InviteInput,
        token: String,
        callBack: InvteResponseApiCallBack<BaseResponse?>
    ) {
        inviteDoctorResponse = ApiClient.build()?.doPatientInviteDoctor(token, inviteInput)
        inviteDoctorResponse?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doAddCareCOmmpanion(
        addCareCompanionInput: AddCareCompanionInput?,
        token: String,
        role: String,
        callBack: AddCareTakerResponseApiCallBack<AddCareTakerResponse?>
    ) {
        if(role.equals(Constants.ROLE_DOCTOR,true)){
            addCTResponseCall = ApiClient.build()?.doAddCareAssistant(addCareCompanionInput, token)
        }else{
            addCTResponseCall = ApiClient.build()?.doAddCareCompanion(addCareCompanionInput, token)
        }



        addCTResponseCall?.enqueue(object : Callback<AddCareTakerResponse?>{
            override fun onFailure(call: Call<AddCareTakerResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<AddCareTakerResponse?>?,
                                    response: Response<AddCareTakerResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun doRemoveCG(token: String, id: String, callBack: DeleteCGResponseApiCallBack<BaseResponse?>) {
        deleteCGResponseCall = ApiClient.build()?.doDeleteCareGiver(token, id)

        deleteCGResponseCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doRemovePatient(
        token: String,
        patientId: String,
        callBack: DeletePatientResponseApiCallBack<BaseResponse?>
    ) {
        deletePatientResponseCall = ApiClient.build()?.doDeletePatientByCC(token,patientId)

        deletePatientResponseCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doAnswerDailyQuestion(
        token: String,
        patientId: String,
        answerQuestionInput: AnswerQuestionInput?,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        answerQuestionResponseCall = ApiClient.build()?.doAnswerDailyQuestion(token, patientId, answerQuestionInput)

        answerQuestionResponseCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }



    override fun doUpdateProfile(
        token: String,
        registrationInput: RegistrationInput?,
        doctorRegistrationInput: DoctorRegistrationInput?,
        callBack: LoginResponseApiCallBack<LoginResponse?>,
        role: String
    ) {
        Log.d("profile_set_log","ROle "+role)
        if(role.equals(Constants.ROLE_DOCTOR,true)){
            updateProfileResponseCall = ApiClient.build()?.doUpdateDoctorProfile(token, doctorRegistrationInput)
        }else if(role.equals(Constants.ROLE_PATIENT, true)){
            updateProfileResponseCall = ApiClient.build()?.doUpdateProfile(token, registrationInput)
        }else if(role.equals(Constants.ROLE_PATIENT_CARE_GIVER, true)){
            updateProfileResponseCall = ApiClient.build()?.doUpdatePatientProfileByCG(token, registrationInput)
        }
        else{
            updateProfileResponseCall = ApiClient.build()?.doUpdateCareTakerProfile(token, registrationInput)
        }


        updateProfileResponseCall?.enqueue(object : Callback<LoginResponse?>{

            override fun onFailure(call: Call<LoginResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<LoginResponse?>?,
                                    response: Response<LoginResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doAlertEmergencyContact(
        token: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        baseResponseCall = ApiClient.build()?.doEmergencyAlert(token)

        baseResponseCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun doDeleteEmergencyContact(
        token: String,
        contactId: String,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        baseResponseCall = ApiClient.build()?.doDeleteEmergencyCOntact(token, contactId)

        baseResponseCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun doGetEmergencyContactList(
        token: String,
        callBack: ContactsListResponseApiCallBack<EmergencyContactsListResponse?>
    ) {
        contactsListResponseCall = ApiClient.build()?.doGetEmergencyContactsList(token)

        contactsListResponseCall?.enqueue(object : Callback<EmergencyContactsListResponse?>{

            override fun onFailure(call: Call<EmergencyContactsListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<EmergencyContactsListResponse?>?,
                                    response: Response<EmergencyContactsListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun doGetDailyQuestionstList(
        token: String,
        callBack: DailyQuestionsListResponseApiCallBack<DailyQuestionsListResponse?>
    ) {
        dailyQuestionsListResponseCall = ApiClient.build()?.doDailyQuestionsList(token)

        dailyQuestionsListResponseCall?.enqueue(object : Callback<DailyQuestionsListResponse?>{

            override fun onFailure(call: Call<DailyQuestionsListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<DailyQuestionsListResponse?>?,
                                    response: Response<DailyQuestionsListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doGetCancerTypesList(
        token: String,
        callBack: CancerListResponseApiCallBack<CancerTypesListResponse?>
    ) {
        cancerListResponseCall = ApiClient.build()?.doGetCancerTypes(token)

        cancerListResponseCall?.enqueue(object : Callback<CancerTypesListResponse?>{

            override fun onFailure(call: Call<CancerTypesListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<CancerTypesListResponse?>?,
                                    response: Response<CancerTypesListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doGetHospitalList(
        token: String,
        callBack: HospitalListResponseApiCallBack<HospitalListingResponse?>
    ) {
        hospitalListResponseCall = ApiClient.build()?.doGetHospitals(token)

        hospitalListResponseCall?.enqueue(object : Callback<HospitalListingResponse?>{

            override fun onFailure(call: Call<HospitalListingResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<HospitalListingResponse?>?,
                                    response: Response<HospitalListingResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doGetCancerSubTypesList(
        token: String,
        cancerType : String,
        callBack: CancerListResponseApiCallBack<CancerTypesListResponse?>
    ) {
        cancerListResponseCall = ApiClient.build()?.doGetCancerSubTypes(token,cancerType)

        cancerListResponseCall?.enqueue(object : Callback<CancerTypesListResponse?>{

            override fun onFailure(call: Call<CancerTypesListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<CancerTypesListResponse?>?,
                                    response: Response<CancerTypesListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun uploadFileToS3(
        token: String,
        file: MultipartBody.Part,
        callback: RecordsVMRepository.UploadS3ApiCallBack<BaseResponse?>
    ) {
        uploadFileToS3Call = ApiClient.build()?.uploadFile(token,file)

        uploadFileToS3Call?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("upload_log", "repo Error "+t?.message)
                callback.onFileUploadError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("upload_log", "Catched")
                }

                Log.d("upload_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onFileUploadSuccess(response.body())
                    Log.d("upload_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onFileUploadError( jObjError.getString("message"))
                        Log.d("upload_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("upload_log","API Failed here "+ignored.toString())
                    }

                    Log.d("upload_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun updateDoctorTimings(
        token: String,
        doctorId: String,
        doctorUpdateInput: DoctorAvailabilityReq,
        callBack: BaseResponseApiCallBack<BaseResponse?>
    ) {
        baseResponseCall = ApiClient.build()?.updateDoctorAvailibility(token, doctorUpdateInput)
        baseResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getDoctorTimings(
        token: String,
        doctorId: String,
        callBack: DoctorTimingsApiCallBack<DoctorAvailibilityResponse?>
    ) {
        getDoctorTimingsCall = ApiClient.build()?.getDoctorTimings(token, doctorId)
        getDoctorTimingsCall?.enqueue(object : Callback<DoctorAvailibilityResponse?>{
            override fun onFailure(call: Call<DoctorAvailibilityResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<DoctorAvailibilityResponse?>?,
                                    response: Response<DoctorAvailibilityResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun veriFyDoctorByCC(
        token: String,
        doctorId: String,
        callBack: VerifyDoctorByCCResponseApiCallBack<BaseResponse?>
    ) {
        verifyDoctorByCcCall = ApiClient.build()?.doVerifyDoctorByCC(token, doctorId)
        verifyDoctorByCcCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onVerifySuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun connectionResponse(
        token: String,
        userId: String,
        isDoctor: Boolean,
        isAccepted: Boolean,
        callBack: ConnectionResponseCallBack<BaseResponse?>
    ) {
        if(isDoctor){
            if(isAccepted){
                Log.d("connection_log", "Doctor accept patient")
                connectionResponseCall = ApiClient.build()?.doDoctorAcceptConnectionRequest(token, userId)
            }else{
                Log.d("connection_log", "Doctor reject patient")
                connectionResponseCall = ApiClient.build()?.doDoctorRejectConnectionRequest(token, userId)
            }
        }else{
            if(isAccepted){
                Log.d("connection_log", "patient accept Doctor")
                connectionResponseCall = ApiClient.build()?.doPatientAcceptConnectionRequest(token, userId)
            }else{
                Log.d("connection_log", "patient reject Doctor")
                connectionResponseCall = ApiClient.build()?.doPatientRejectConnectionRequest(token, userId)
            }
        }
        connectionResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("connection_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("connection_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onConnectionResponseSuccess(response.body())
                    Log.d("connection_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("connection_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("connection_log","API Failed here "+ignored.toString())
                    }

                    Log.d("connection_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getAwsCredentials(
        token: String,
        callBack: AwsCredentialsResponseApiCallBack<AwsCredentialsResponse?>
    ) {
        getAwsCredentialsResponse = ApiClient.build()?.doGetAwsCredentials(token)
        getAwsCredentialsResponse?.enqueue(object : Callback<AwsCredentialsResponse?>{
            override fun onFailure(call: Call<AwsCredentialsResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<AwsCredentialsResponse?>?,
                                    response: Response<AwsCredentialsResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onCredentialSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun sendComplaint(token: String, complaintInput: ComplaintInput, callBack: ComplaintResponseApiCallBack<BaseResponse?>) {
        complaintResponseCall = ApiClient.build()?.doSendComplaint(token, complaintInput)
        complaintResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                                JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getEducations(token: String, callBack: EducationApiCallBack<DegreesResponse?>) {
        educationResponseCall = ApiClient.build()?.doGetAllEducations(token)
        educationResponseCall?.enqueue(object : Callback<DegreesResponse?>{
            override fun onFailure(call: Call<DegreesResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<DegreesResponse?>?,
                                    response: Response<DegreesResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun deleteEducation(
        token: String,
        eduId: String,
        callBack: DeleteEducationApiCallBack<BaseResponse?>
    ) {
        deleteEduResponseCall = ApiClient.build()?.doDeleteEducation(token, eduId)
        deleteEduResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun addEducation(
        token: String,
        eduInput: AddEducationInput,
        callBack: AddEduResponseApiCallBack<BaseResponse?>
    ) {
        addEduResponseCall = ApiClient.build()?.doAddEducation(token, eduInput)
        addEduResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getDoctorAvailibility(
        token: String,
        doctorUserId: String,
        callBack: GetDoctorAvailibilityAPiCallBack<DoctorAvailibilityResponse?>
    ) {
        getDoctorAvailibilityResponseCall = ApiClient.build()?.doGetDoctorAvailibilitySlots(token,doctorUserId)
        getDoctorAvailibilityResponseCall?.enqueue(object : Callback<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>{
            override fun onFailure(call: Call<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>?,
                                    response: Response<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun updateDoctorAvailibility(
        token: String,
        doctorAvailibilityInput: DoctorAvailibilityInput,
        callBack: UpdateDoctorAvailibilityAPiCallBack<BaseResponse?>
    ) {
        updateAvailibilityResponseCall = ApiClient.build()?.doUpdateDoctorTImeSLots(token, doctorAvailibilityInput)
        updateAvailibilityResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun addLocation(
        token: String,
        establishmentInput: EstablishmentInput,
        callBack: AddDoctorLocationAPiCallBack<BaseResponse?>
    ) {
        addLocationResponseCall = ApiClient.build()?.doAddDoctorLocation(token, establishmentInput)
        addLocationResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun editLocation(
        token: String,
        locationId: String,
        establishmentInput: EstablishmentInput,
        callBack: AddDoctorLocationAPiCallBack<BaseResponse?>
    ) {
        editLocationResponseCall = ApiClient.build()?.doEditLocation(token, locationId, establishmentInput)
        editLocationResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun deleteLocation(
        token: String,
        locationId: String,
        callBack: AddDoctorLocationAPiCallBack<BaseResponse?>
    ) {
        deleteLocationResponseCall = ApiClient.build()?.doDeleteLocation(token, locationId)
        deleteLocationResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }



    override fun getLocationsList(
        token: String,
        callBack: GetDoctorLocationAPiCallBack<DOctorLOcationListResponse?>
    ) {
        getLocationsResponseCall = ApiClient.build()?.doGetDoctorLocations(token)
        getLocationsResponseCall?.enqueue(object : Callback<DOctorLOcationListResponse?>{
            override fun onFailure(call: Call<DOctorLOcationListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<DOctorLOcationListResponse?>?,
                                    response: Response<DOctorLOcationListResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getPatientTRansactionsList(
        token: String,
        callBack: PatientTransactionsResponseApiCallBack<PatientTransactionsResponse?>
    ) {
        patientTransactionResponseCall = ApiClient.build()?.doGetPatientTransactions(token)
        patientTransactionResponseCall?.enqueue(object : Callback<PatientTransactionsResponse?>{
            override fun onFailure(call: Call<PatientTransactionsResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<PatientTransactionsResponse?>?,
                                    response: Response<PatientTransactionsResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun addCertification(
        token: String,
        addCertificateInput: AddCertificateInput,
        callBack: AddCertificationResponseApiCallBack<BaseResponse?>
    ) {
        addCertificationResponseCall = ApiClient.build()?.doAddCertification(token, addCertificateInput)
        addCertificationResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }



    override fun getPatientCareTakerDetails(isFromCG: Boolean,
        token: String,
        callBack: CareTeamDetailsApiCallBack<CareTeamResponse?>
    ) {
        if(isFromCG)
             careTeamDetailsResponseCall = ApiClient.build()?.doGetCareCompanionDetailsByCG(token)
        else
            careTeamDetailsResponseCall = ApiClient.build()?.doGetPatientCaretakerDetails(token)

        careTeamDetailsResponseCall?.enqueue(object : Callback<CareTeamResponse?>{
            override fun onFailure(call: Call<CareTeamResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<CareTeamResponse?>?,
                                    response: Response<CareTeamResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun deleteAccount(
        token: String,
        deleteAccountInput: DeleteAccountInput,
        callBack: DeleteAccountResponseApiCallBack<BaseResponse?>
    ) {
        deleteAccountResponseCall = ApiClient.build()?.doDeleteUserAccount(token, deleteAccountInput)
        deleteAccountResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getGeneticReport(
        token: String,
        patientId: String,
        callBack: GeneticReportResponseApiCallBack<GeneticResponse?>
    ) {
        geneticReportResponseCall = ApiClient.build()?.doGetPatientEcrfReport(token, patientId)
        geneticReportResponseCall?.enqueue(object : Callback<GeneticResponse?>{
            override fun onFailure(call: Call<GeneticResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<GeneticResponse?>?,
                                    response: Response<GeneticResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doAddBankAccount(
        token: String,
        bankAccountInput: BankAccountInput,
        callBack: AddBankAccountResponseApiCallBack<BaseResponse?>
    ) {
        addBankAccountResponseCall = ApiClient.build()?.doAddBankAcc(token, bankAccountInput)
        addBankAccountResponseCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doGetPatientDetailsBYCG(
        token: String,
        careCOde: String,
        callBack: PatientDetailsByCGResponseApiCallBack<PatientDetailsByCGResponse?>
    ) {
        if(careCOde.isNullOrEmpty()){
            patientDetailsByCGResponseCall = ApiClient.build()?.getPatientDetailsByCG(token)
        }else{
            patientDetailsByCGResponseCall = ApiClient.build()?.getPatientDetailsByCareCOde(token, careCOde)
        }
        patientDetailsByCGResponseCall?.enqueue(object : Callback<PatientDetailsByCGResponse?>{
            override fun onFailure(call: Call<PatientDetailsByCGResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<PatientDetailsByCGResponse?>?,
                                    response: Response<PatientDetailsByCGResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doAssignPatientTOCG(
        token: String,
        assignCGInput: AssignCGInput,
        callBack: AssignPatientTOCGResponseApiCallBack<BaseResponse?>
    ) {
        assignPatientToCG = ApiClient.build()?.assignPatientToCG(token, assignCGInput)
        assignPatientToCG?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun doUpdateCGDetails(
        token: String,
        careGiverInput: CareGiverInput,
        callBack: UpdateCGAPiApiCallBack<BaseResponse?>
    ) {
        updateCGDetails = ApiClient.build()?.doUpdateCGDetails(token, careGiverInput)
        updateCGDetails?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {
                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }


    override fun cancel() {
          baseResponseCall?.cancel()
    }
}

interface ProfileVMImplementor : BaseImplementor {
    fun doAddEmergencyContact(emergencyContactInput: EmergencyContactInput?, token : String,
                              callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun doInviteDoctor(inviteInput: InviteInput, token : String,
                              callBack: ProfileVMRepository.InvteResponseApiCallBack<BaseResponse?>)

    fun doAddCareCOmmpanion(addCareCompanionInput: AddCareCompanionInput?, token : String, role :String,
                              callBack: ProfileVMRepository.AddCareTakerResponseApiCallBack<AddCareTakerResponse?>)

    fun doRemoveCG(token : String, id: String, callBack: ProfileVMRepository.DeleteCGResponseApiCallBack<BaseResponse?>)

    fun doRemovePatient(token : String, patientId: String, callBack: ProfileVMRepository.DeletePatientResponseApiCallBack<BaseResponse?>)


    fun doAnswerDailyQuestion(token : String, patientId:String, answerQuestionInput: AnswerQuestionInput?,
                              callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun doUpdateProfile(token : String, registrationInput: RegistrationInput?,doctorRegistrationInput: DoctorRegistrationInput?,
                              callBack: ProfileVMRepository.LoginResponseApiCallBack<LoginResponse?>, role : String)

    fun doAlertEmergencyContact(token : String, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun doDeleteEmergencyContact(token : String, contactId:String, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun doGetEmergencyContactList(token : String, callBack: ProfileVMRepository.ContactsListResponseApiCallBack<EmergencyContactsListResponse?>)

    fun doGetDailyQuestionstList(token : String, callBack: ProfileVMRepository.DailyQuestionsListResponseApiCallBack<DailyQuestionsListResponse?>)

    fun doGetCancerTypesList(token : String, callBack: ProfileVMRepository.CancerListResponseApiCallBack<CancerTypesListResponse?>)

    fun doGetHospitalList(token : String, callBack: ProfileVMRepository.HospitalListResponseApiCallBack<HospitalListingResponse?>)

    fun doGetCancerSubTypesList(token : String, cancerType : String, callBack: ProfileVMRepository.CancerListResponseApiCallBack<CancerTypesListResponse?>)

    fun uploadFileToS3(token: String, file: MultipartBody.Part, callback: RecordsVMRepository.UploadS3ApiCallBack<BaseResponse?>)

    fun getMyDoctorsList(source: String,shouldGetALlied: Boolean,token: String, patientId: String, callback: AppointmentVMRepository.DoctorListApiCallBack<DoctorsListingResponse?>)

    fun getWalletBalance (token: String, callback: ProfileVMRepository.WalletBalanceApiCallBack<WalletBalanceResponse?>)

    fun getWalletTransactions(token: String, callback: ProfileVMRepository.WalletTransactionsApiCallBack<WalletTransactionsResponse?>)

    fun withdrawWalletBalance(token: String, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun checkPatientECRF(token: String, patientId: String, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun getEcrfSummaryPDF(token: String, patientId: String, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun getCareGiverDetails(token: String, callBack: ProfileVMRepository.CareTakerResponseApiCallBack<CareGiverResponse?>)

    fun findDoctor(shouldFindPatient: Boolean,token: String, phoneNo: String, callBack: ProfileVMRepository.FindDoctorResponseApiCallBack<FindDoctorResponse?>)

    fun assignDoctor(shouldAssignPatient: Boolean, token: String, doctorId: String, callBack: ProfileVMRepository.AssignDoctorResponseApiCallBack<BaseResponse?>)

    fun getEcrfEvents(token: String, patientId: String, callBack: ProfileVMRepository.EcrfEventsApiCallBack<EcrfventsResponse?>)

    fun addUserByCC(shouldAddPatient: Boolean, token: String, addUserByCCInput: AddUserByCCInput, callBack: ProfileVMRepository.AddUserByCCResponseApiCallBack<BaseResponse?>)

    fun updateDoctorTimings(token: String, doctorId: String, doctorUpdateInput: DoctorAvailabilityReq, callBack: ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>)

    fun getDoctorTimings(token: String, doctorId: String, callBack: ProfileVMRepository.DoctorTimingsApiCallBack<DoctorAvailibilityResponse?>)

    fun veriFyDoctorByCC(token: String, doctorId: String, callBack: ProfileVMRepository.VerifyDoctorByCCResponseApiCallBack<BaseResponse?>)

    fun connectionResponse(token: String, userId: String, isDoctor: Boolean, isAccepted: Boolean, callBack: ProfileVMRepository.ConnectionResponseCallBack<BaseResponse?>)

    fun getAwsCredentials(token: String,callBack: ProfileVMRepository.AwsCredentialsResponseApiCallBack<AwsCredentialsResponse?>)

    fun sendComplaint(token: String, complaintInput: ComplaintInput,callBack: ProfileVMRepository.ComplaintResponseApiCallBack<BaseResponse?>)

    fun getEducations(token: String,callBack: ProfileVMRepository.EducationApiCallBack<DegreesResponse?>)

    fun deleteEducation(token: String,eduId: String,callBack: ProfileVMRepository.DeleteEducationApiCallBack<BaseResponse?>)

    fun addEducation(token: String, eduInput: AddEducationInput,callBack: ProfileVMRepository.AddEduResponseApiCallBack<BaseResponse?>)

    fun getDoctorAvailibility(token: String, doctorUserId: String, callBack: ProfileVMRepository.GetDoctorAvailibilityAPiCallBack<DoctorAvailibilityResponse?>)

    fun updateDoctorAvailibility(token: String, doctorAvailibilityInput: DoctorAvailibilityInput, callBack: ProfileVMRepository.UpdateDoctorAvailibilityAPiCallBack<BaseResponse?>)

    fun addLocation(token: String, establishmentInput: EstablishmentInput, callBack: ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>)

    fun editLocation(token: String, locationId: String, establishmentInput: EstablishmentInput, callBack: ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>)

    fun deleteLocation(token: String, locationId: String, callBack: ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>)

    fun getLocationsList(token: String, callBack: ProfileVMRepository.GetDoctorLocationAPiCallBack<DOctorLOcationListResponse?>)

    fun getPatientTRansactionsList(token: String, callBack: ProfileVMRepository.PatientTransactionsResponseApiCallBack<PatientTransactionsResponse?>)

    fun addCertification(token: String, addCertificateInput: AddCertificateInput, callBack: ProfileVMRepository.AddCertificationResponseApiCallBack<BaseResponse?>)

    fun getPatientCareTakerDetails(isFromCG: Boolean,token: String, callBack: ProfileVMRepository.CareTeamDetailsApiCallBack<CareTeamResponse?>)

    fun deleteAccount(token: String, deleteAccountInput: DeleteAccountInput, callBack: ProfileVMRepository.DeleteAccountResponseApiCallBack<BaseResponse?>)

    fun getGeneticReport(token: String, patientId: String, callBack: ProfileVMRepository.GeneticReportResponseApiCallBack<GeneticResponse?>)

    fun doAddBankAccount(token: String, bankAccountInput: BankAccountInput, callBack: ProfileVMRepository.AddBankAccountResponseApiCallBack<BaseResponse?>)

    fun doGetPatientDetailsBYCG(token: String, careCOde: String, callBack: ProfileVMRepository.PatientDetailsByCGResponseApiCallBack<PatientDetailsByCGResponse?>)

    fun doAssignPatientTOCG(token: String, assignCGInput: AssignCGInput, callBack: ProfileVMRepository.AssignPatientTOCGResponseApiCallBack<BaseResponse?>)

    fun doUpdateCGDetails(token: String, careGiverInput: CareGiverInput, callBack: ProfileVMRepository.UpdateCGAPiApiCallBack<BaseResponse?>)
}
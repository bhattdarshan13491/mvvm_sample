package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.AppointmentVMRepository
import com.oncobuddy.app.models.api_repositories.ProfileVMImplementor
import com.oncobuddy.app.models.api_repositories.ProfileVMRepository
import com.oncobuddy.app.models.api_repositories.RecordsVMRepository
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
import com.oncobuddy.app.utils.Constants
import okhttp3.MultipartBody

class ProfileViewModel(private val profileVMImplementor: ProfileVMImplementor) : ViewModel() {

    private val _liveAddContactResponse = MutableLiveData<BaseResponse>()
    val addContactResonseData: LiveData<BaseResponse> = _liveAddContactResponse

    private val _liveUpdateCGDetailsResponse = MutableLiveData<BaseResponse>()
    val updateCGDetailsResonseData: LiveData<BaseResponse> = _liveUpdateCGDetailsResponse

    private val _liveAddBankAcctResponse = MutableLiveData<BaseResponse>()
    val addBankAcctResonseData: LiveData<BaseResponse> = _liveAddBankAcctResponse

    private val _livePatientTransactionsResponse = MutableLiveData<PatientTransactionsResponse>()
    val patientTransactionsData: LiveData<PatientTransactionsResponse> = _livePatientTransactionsResponse

    private val _liveComplaintResponse = MutableLiveData<BaseResponse>()
    val complainyResonseData: LiveData<BaseResponse> = _liveComplaintResponse

    private val _liveInviteResponse = MutableLiveData<BaseResponse>()
    val inviteResonseData: LiveData<BaseResponse> = _liveInviteResponse

    private val _liveConnectionResponse = MutableLiveData<BaseResponse>()
    val connectionResonseData: LiveData<BaseResponse> = _liveConnectionResponse

    private val _liveDeleteCGResponse = MutableLiveData<BaseResponse>()
    val deleteCGResonseData: LiveData<BaseResponse> = _liveDeleteCGResponse

    private val _liveDeletePatientResponse = MutableLiveData<BaseResponse>()
    val deletePatientResonseData: LiveData<BaseResponse> = _liveDeletePatientResponse

    private val _liveAwsCredentialsResponse = MutableLiveData<AwsCredentialsResponse>()
    val awsCredentialsResponseData: LiveData<AwsCredentialsResponse> = _liveAwsCredentialsResponse

    private val _liveVerifyDoctorResponse = MutableLiveData<BaseResponse>()
    val verifyDoctorResponse: LiveData<BaseResponse> = _liveVerifyDoctorResponse

    private val _liveCareGiverResponse = MutableLiveData<CareGiverResponse>()
    val careGiverResonseData: LiveData<CareGiverResponse> = _liveCareGiverResponse

    private val _liveFindDoctorResponse = MutableLiveData<FindDoctorResponse>()
    val findDoctorResonseData: LiveData<FindDoctorResponse> = _liveFindDoctorResponse

    private val _liveGetEducationResponse = MutableLiveData<DegreesResponse>()
    val educationResonseData: LiveData<DegreesResponse> = _liveGetEducationResponse

    private val _liveAddEducationResponse = MutableLiveData<BaseResponse>()
    val addEducationResonseData: LiveData<BaseResponse> = _liveAddEducationResponse

    private val _liveAddCertificationResponse = MutableLiveData<BaseResponse>()
    val addCertificationResonseData: LiveData<BaseResponse> = _liveAddCertificationResponse

    private val _liveDeleteEducationResponse = MutableLiveData<BaseResponse>()
    val deleteEducationResonseData: LiveData<BaseResponse> = _liveDeleteEducationResponse

    private val _liveEcrfEventsResponse = MutableLiveData<EcrfventsResponse>()
    val ecrfEventsResonseData: LiveData<EcrfventsResponse> = _liveEcrfEventsResponse

    private val _liveAssignDoctorResponse = MutableLiveData<BaseResponse>()
    val assignDoctorResonseData: LiveData<BaseResponse> = _liveAssignDoctorResponse

    private val _liveCheckEcrfResponse = MutableLiveData<BaseResponse>()
    val checkEcrfResonseData: LiveData<BaseResponse> = _liveCheckEcrfResponse

    private val _liveAddUserByCCResponse = MutableLiveData<BaseResponse>()
    val addUserByCCResonseData: LiveData<BaseResponse> = _liveAddUserByCCResponse

    private val _liveEcrfPDFResponse = MutableLiveData<BaseResponse>()
    val checkEcrfPDFResonseData: LiveData<BaseResponse> = _liveCheckEcrfResponse

    private val _liveAlertResponse = MutableLiveData<BaseResponse>()
    val alertContactResonseData: LiveData<BaseResponse> = _liveAlertResponse

    private val _liveAddCareCompanionResponse = MutableLiveData<AddCareTakerResponse>()
    val addCareCompanionResponse: LiveData<AddCareTakerResponse> = _liveAddCareCompanionResponse

    private val _liveDailyQestionsResponse = MutableLiveData<DailyQuestionsListResponse>()
    val dailyQuestionsResonseData: LiveData<DailyQuestionsListResponse> = _liveDailyQestionsResponse

    private val _liveDoctorListResponse = MutableLiveData<DoctorsListingResponse>()
    val doctorListResponse: LiveData<DoctorsListingResponse> = _liveDoctorListResponse

    private val _liveUpdateProfileResponse = MutableLiveData<BaseResponse>()
    val updateProfileResonseData: LiveData<BaseResponse> = _liveUpdateProfileResponse

    private val _liveLoginResponse = MutableLiveData<LoginResponse>()
    val loginResonseData: LiveData<LoginResponse> = _liveLoginResponse

    private val _livePatientDetailsByCG = MutableLiveData<PatientDetailsByCGResponse>()
    val patientDetailsByCGData: LiveData<PatientDetailsByCGResponse> = _livePatientDetailsByCG

    private val _liveAnsweredQuesResponse = MutableLiveData<BaseResponse>()
    val answeredQuesResonseData: LiveData<BaseResponse> = _liveAnsweredQuesResponse

    private val _liveCancerTypesResponse = MutableLiveData<CancerTypesListResponse>()
    val cancerTypesListResonseData: LiveData<CancerTypesListResponse> = _liveCancerTypesResponse

    private val _liveHospitalResponse = MutableLiveData<HospitalListingResponse>()
    val hospitalListResonseData: LiveData<HospitalListingResponse> = _liveHospitalResponse

    private val _liveEmergencyContactsResponse = MutableLiveData<EmergencyContactsListResponse>()
    val emeregencyContactsListResonseData: LiveData<EmergencyContactsListResponse> = _liveEmergencyContactsResponse

    private val _liveCancerSubTypesResponse = MutableLiveData<CancerTypesListResponse>()
    val cancerSubTypesListResonseData: LiveData<CancerTypesListResponse> = _liveCancerSubTypesResponse

    private val _liveDeleteContactResponse = MutableLiveData<BaseResponse>()
    val deleteContactResonseData: LiveData<BaseResponse> = _liveDeleteContactResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError

    private val _onCheckEcrfError = MutableLiveData<String>()
    val onCheckEcrfError: LiveData<String> = _onCheckEcrfError

    private val _liveFileUploadResponse = MutableLiveData<BaseResponse>()
    val responseFileUploadData: LiveData<BaseResponse> = _liveFileUploadResponse

    private val _walletBalanceResponse = MutableLiveData<WalletBalanceResponse>()
    val walletBalanceResponse: LiveData<WalletBalanceResponse> = _walletBalanceResponse

    private val _walletTransactionsResponse = MutableLiveData<WalletTransactionsResponse>()
    val walletTransactionsResponse: LiveData<WalletTransactionsResponse> = _walletTransactionsResponse

    private val _withdrawBalanceResponse = MutableLiveData<BaseResponse>()
    val withdrawBalanceResponse: LiveData<BaseResponse> = _withdrawBalanceResponse

    private val _liveDoctorTimingsResponse = MutableLiveData<DoctorAvailibilityResponse>()
    val doctorTimingResponse: LiveData<DoctorAvailibilityResponse> = _liveDoctorTimingsResponse

    private val _liveUpdateTimingsResponse = MutableLiveData<BaseResponse>()
    val responseUpdateTimings: LiveData<BaseResponse> = _liveUpdateTimingsResponse

    private val _liveAddLocationResponse = MutableLiveData<BaseResponse>()
    val addLocationRsonseData: LiveData<BaseResponse> = _liveAddLocationResponse

    private val _liveDeleteLocationResponse = MutableLiveData<BaseResponse>()
    val deleteLocationRsonseData: LiveData<BaseResponse> = _liveDeleteLocationResponse

    private val _liveLocationListResponse = MutableLiveData<DOctorLOcationListResponse>()
    val locationListRsonseData: LiveData<DOctorLOcationListResponse> = _liveLocationListResponse

    private val _liveDOctorAvailibilityResponse = MutableLiveData<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse>()
    val liveDOctorAvailibilityResponse: LiveData<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse> = _liveDOctorAvailibilityResponse

    private val _liveUpdateDrAvailibilityResponse = MutableLiveData<BaseResponse>()
    val liveUpdateDrAvailibilityResponse: LiveData<BaseResponse> = _liveUpdateDrAvailibilityResponse

    private val _liveDeleteAccountResponse = MutableLiveData<BaseResponse>()
    val deleteAccountResponse: LiveData<BaseResponse> = _liveDeleteAccountResponse

    private val _liveCareTeamDetailResponse = MutableLiveData<CareTeamResponse>()
    val careTeamDetailsResonseData: LiveData<CareTeamResponse> = _liveCareTeamDetailResponse

    private val _liveGeneticReportResponse = MutableLiveData<GeneticResponse>()
    val geneticResonseData: LiveData<GeneticResponse> = _liveGeneticReportResponse

    private val _liveAssignPatientToCG = MutableLiveData<BaseResponse>()
    val assignPatientToCGData: LiveData<BaseResponse> = _liveAssignPatientToCG


    fun callUpdateDoctorTimings(token: String, doctorId: String, doctorUpdateInput: DoctorAvailabilityReq) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.updateDoctorTimings(token,doctorId, doctorUpdateInput,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveUpdateTimingsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callUpdateCGDetails(token: String, careGiverInput: CareGiverInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.doUpdateCGDetails(token, careGiverInput,
            object : ProfileVMRepository.UpdateCGAPiApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveUpdateCGDetailsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetPatientDetailsByCG(token: String, careCode: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.doGetPatientDetailsBYCG(token,careCode,
            object : ProfileVMRepository.PatientDetailsByCGResponseApiCallBack<PatientDetailsByCGResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: PatientDetailsByCGResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _livePatientDetailsByCG.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callAssignPatientToCG(token: String, assignCGInput: AssignCGInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.doAssignPatientTOCG(token,assignCGInput,
            object : ProfileVMRepository.AssignPatientTOCGResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveAssignPatientToCG.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetCareTeamMemberDetails(token: String, isFromCG: Boolean = false) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.getPatientCareTakerDetails(isFromCG,token,
            object : ProfileVMRepository.CareTeamDetailsApiCallBack<CareTeamResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: CareTeamResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveCareTeamDetailResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetGeneticReport(token: String, patientId: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.getGeneticReport(token, patientId,
            object : ProfileVMRepository.GeneticReportResponseApiCallBack<GeneticResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: GeneticResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveGeneticReportResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callAddBankAcctDetails(token: String, bankAccountInput: BankAccountInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.doAddBankAccount(token,bankAccountInput,
            object : ProfileVMRepository.AddBankAccountResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveAddBankAcctResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callDOctorInvite(token: String, inviteInput: InviteInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.doInviteDoctor(inviteInput,token,
            object : ProfileVMRepository.InvteResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveInviteResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callDeleteAccount(token: String, deleteAccountInput: DeleteAccountInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.deleteAccount(token,deleteAccountInput,
            object : ProfileVMRepository.DeleteAccountResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveDeleteAccountResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetEducation(token: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.getEducations(token,
            object : ProfileVMRepository.EducationApiCallBack<DegreesResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: DegreesResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveGetEducationResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetPatientTransactions(token: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.getPatientTRansactionsList(token,
            object : ProfileVMRepository.PatientTransactionsResponseApiCallBack<PatientTransactionsResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: PatientTransactionsResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _livePatientTransactionsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callDeleteEducation(token: String, eduId: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.deleteEducation(token,eduId,
            object : ProfileVMRepository.DeleteEducationApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveDeleteEducationResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callAddEducation(token: String, educationInput: AddEducationInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.addEducation(token, educationInput,
            object : ProfileVMRepository.AddEduResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveAddEducationResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callAddCertification(token: String, addCertificateInput: AddCertificateInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.addCertification(token, addCertificateInput,
            object : ProfileVMRepository.AddCertificationResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveAddCertificationResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }



    fun callSendComplaint(token: String, complaintInput: ComplaintInput) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.sendComplaint(token, complaintInput,
                object : ProfileVMRepository.ComplaintResponseApiCallBack<BaseResponse?> {

                    override fun onError(message: String?) {
                        Log.d("upload_log","4 "+message)
                        _isViewLoading.postValue(false)
                        _onMessageError.postValue(message)
                    }

                    override fun onSuccess(responseData: BaseResponse?) {
                        Log.d("upload_log","5 "+responseData)
                        _liveComplaintResponse.postValue(responseData)
                        _isViewLoading.postValue(false)
                    }
                })
    }

    fun callGetDoctorTimings(token: String, doctorId: String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.getDoctorTimings(token,doctorId,
            object : ProfileVMRepository.DoctorTimingsApiCallBack<DoctorAvailibilityResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: DoctorAvailibilityResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveDoctorTimingsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }


    fun callFileUpload( token:String, file: MultipartBody.Part) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        profileVMImplementor.uploadFileToS3(token,file,
            object : RecordsVMRepository.UploadS3ApiCallBack<BaseResponse?> {

                override fun onFileUploadError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onFileUploadSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveFileUploadResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun addEmergencyContact(emergencyContactInput: EmergencyContactInput, token : String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.doAddEmergencyContact(
            emergencyContactInput,token,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveAddContactResponse.postValue(responseData)
                }

            })
    }

    fun deleteCareGiver(token : String, id: String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.doRemoveCG(token, id,
            object : ProfileVMRepository.DeleteCGResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveDeleteCGResponse.postValue(responseData)
                }

            })
    }

    fun deletePatientByCC(token : String, patientId: String) {

        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.doRemovePatient(token, patientId,
            object : ProfileVMRepository.DeletePatientResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveDeletePatientResponse.postValue(responseData)
                }

            })
    }



    fun getAwsCredentials(token: String) {
        Log.d("aws_credentials","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.getAwsCredentials(token,
            object : ProfileVMRepository.AwsCredentialsResponseApiCallBack<AwsCredentialsResponse?> {

                override fun onError(message: String?) {
                    Log.d("aws_credentials", "failue " + message)
                    Log.d("aws_credentials", "is_loading is 2")
                    Log.d("aws_credentials","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onCredentialSuccess(responseData: AwsCredentialsResponse?) {
                    Log.d("aws_credentials", "Success")
                    Log.d("aws_credentials", "is_loading is 1")
                    Log.d("aws_credentials","5")
                    _isViewLoading.postValue(false)
                    _liveAwsCredentialsResponse.postValue(responseData)
                }

            })
    }


    fun addUserByCC(shouldAddPatient: Boolean, token: String, addUserByCCInput: AddUserByCCInput) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.addUserByCC(shouldAddPatient,token,addUserByCCInput,
            object : ProfileVMRepository.AddUserByCCResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onAddUserSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveAddUserByCCResponse.postValue(responseData)
                }

            })
    }

    fun verifyDoctorByCC(token: String, doctorId: String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.veriFyDoctorByCC(token,doctorId,
            object : ProfileVMRepository.VerifyDoctorByCCResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onVerifySuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveVerifyDoctorResponse.postValue(responseData)
                }

            })
    }


    fun findDoctor(shouldFIndPatient: Boolean, phoneNo : String, token : String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.findDoctor(shouldFIndPatient,
            token, phoneNo,
            object : ProfileVMRepository.FindDoctorResponseApiCallBack<FindDoctorResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onFindDoctorSuccess(responseData: FindDoctorResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveFindDoctorResponse.postValue(responseData)
                }

            })
    }

    fun getEcrfEvents(patientId: String , token : String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.getEcrfEvents(
            token, patientId,
            object : ProfileVMRepository.EcrfEventsApiCallBack<EcrfventsResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: EcrfventsResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveEcrfEventsResponse.postValue(responseData)
                }

            })
    }

    fun getCareTakerDetails(token : String) {
        Log.d("emergency_contact","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.getCareGiverDetails(
            token,
            object : ProfileVMRepository.CareTakerResponseApiCallBack<CareGiverResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    Log.d("emergency_contact","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: CareGiverResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    Log.d("emergency_contact","5")
                    _isViewLoading.postValue(false)
                    _liveCareGiverResponse.postValue(responseData)
                }

            })
    }


    fun assignDoctor(shouldAssignPatient: Boolean, doctorId:String, token : String) {
        Log.d("assign_log","3")
        _isViewLoading.postValue(true)
        profileVMImplementor.assignDoctor(shouldAssignPatient,
            token, doctorId,
            object : ProfileVMRepository.AssignDoctorResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("assign_log", "failue " + message)
                    Log.d("assign_log", "is_loading is 2")
                    Log.d("assign_log","4")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onAssignDoctorSuccess(responseData: BaseResponse?) {
                    Log.d("assign_log", "Success")
                    Log.d("assign_log", "is_loading is 1")
                    Log.d("assign_log","5")
                    _isViewLoading.postValue(false)
                    _liveAssignDoctorResponse.postValue(responseData)
                }
            })
    }



    fun alertEmergencyContact(token : String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doAlertEmergencyContact(
            token,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveAlertResponse.postValue(responseData)
                }

            })
    }

    fun callGetMyDoctorListing(source: String = "",shouldGetALlied: Boolean = false, token: String, patientId: String) {

        _isViewLoading.postValue(true)
        profileVMImplementor.getMyDoctorsList(source, shouldGetALlied,
            token, patientId,
            object : AppointmentVMRepository.DoctorListApiCallBack<DoctorsListingResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onDoctorListSuccess(responseData: DoctorsListingResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveDoctorListResponse.postValue(responseData)
                }

            })
    }

    fun updateProfile(token : String, registrationInput: RegistrationInput?, doctorRegistrationInput: DoctorRegistrationInput?, role : String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doUpdateProfile(
            token, registrationInput,doctorRegistrationInput,
            object : ProfileVMRepository.LoginResponseApiCallBack<LoginResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: LoginResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveLoginResponse.postValue(responseData)
                }

            },role)
    }

    fun answerDailyQuestion(token : String, patientId: String,answerQuestionInput : AnswerQuestionInput) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doAnswerDailyQuestion(
            token, patientId,answerQuestionInput,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveAnsweredQuesResponse.postValue(responseData)
                }

            })
    }

    fun getCancerTypes(token : String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doGetCancerTypesList(
            token,
            object : ProfileVMRepository.CancerListResponseApiCallBack<CancerTypesListResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: CancerTypesListResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveCancerTypesResponse.postValue(responseData)
                }

            })
    }

    fun getHospitalsListing(token : String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doGetHospitalList(
            token,
            object : ProfileVMRepository.HospitalListResponseApiCallBack<HospitalListingResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: HospitalListingResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveHospitalResponse.postValue(responseData)
                }

            })
    }

    fun withdrawBalance(token: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.withdrawWalletBalance(token,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>{
                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(responseData?.message)
                }

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

            })
    }
    fun getWalletBalance(token: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.getWalletBalance(token,
            object : ProfileVMRepository.WalletBalanceApiCallBack<WalletBalanceResponse?> {
                override fun onSuccess(responseData: WalletBalanceResponse?) {
                    _isViewLoading.postValue(false)
                    _walletBalanceResponse.postValue(responseData)
                    Log.d("WALLET", "ResponseData:" + responseData.toString())
                }

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun getWalletTransactions(userAuthToken: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.getWalletTransactions(userAuthToken,
            object : ProfileVMRepository.WalletTransactionsApiCallBack<WalletTransactionsResponse?> {
                override fun onSuccess(responseData: WalletTransactionsResponse?) {
                    _isViewLoading.postValue(false)
                    _walletTransactionsResponse.postValue(responseData)
                }

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })

    }

    fun getEmergencyContactsList(token : String) {
        _isViewLoading.postValue(true)

        profileVMImplementor.doGetEmergencyContactList(
            token,
            object : ProfileVMRepository.ContactsListResponseApiCallBack<EmergencyContactsListResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: EmergencyContactsListResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveEmergencyContactsResponse.postValue(responseData)
                }

            })
    }

    fun getDailyQuestionsList(token : String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doGetDailyQuestionstList(
            token,
            object : ProfileVMRepository.DailyQuestionsListResponseApiCallBack<DailyQuestionsListResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: DailyQuestionsListResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveDailyQestionsResponse.postValue(responseData)
                }

            })
    }

    fun getCSubancerTypes(token : String, cancerTypeId: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doGetCancerSubTypesList(
            token, cancerTypeId,
            object : ProfileVMRepository.CancerListResponseApiCallBack<CancerTypesListResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }



                override fun onSuccess(responseData: CancerTypesListResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveCancerSubTypesResponse.postValue(responseData)
                }

            })
    }

    fun deleteEmergencyContact(token : String, emergencyContact: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.doDeleteEmergencyContact(
            token, emergencyContact,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")

                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveDeleteContactResponse.postValue(responseData)
                }

            })
    }

    fun responseCOnnectionRequest(token: String, userId: String, isDoctor: Boolean, isAccepted: Boolean) {
        _isViewLoading.postValue(true)
        Log.d("connection_log", "isdoctor " + isDoctor)
        profileVMImplementor.connectionResponse(token,userId,isDoctor,isAccepted,
            object : ProfileVMRepository.ConnectionResponseCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("connection_log", "failue " + message)
                    Log.d("connection_log", "is_loading is 2")

                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onConnectionResponseSuccess(responseData: BaseResponse?) {
                    Log.d("connection_log", "Success")
                    Log.d("connection_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveConnectionResponse.postValue(responseData)
                }

            })
    }


    fun addCareCompanion(addCareCompanionInput: AddCareCompanionInput?, token : String, role :String) {
        Log.d("profile_set_log","ROle "+role)
        _isViewLoading.postValue(true)
        profileVMImplementor.doAddCareCOmmpanion(
            addCareCompanionInput, token, role,
            object : ProfileVMRepository.AddCareTakerResponseApiCallBack<AddCareTakerResponse?> {

                override fun onError(message: String?) {
                    Log.d("login_log", "failue " + message)
                    Log.d("login_log", "is_loading is 2")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: AddCareTakerResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveAddCareCompanionResponse.postValue(responseData)
                }

            })
    }

    // TO check whether the user is on ECRF
    fun checkPatientECRF(token: String, patientId: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.
        checkPatientECRF(token, patientId,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>{
                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveCheckEcrfResponse.postValue(responseData)
                } override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onCheckEcrfError.postValue(message)
                }

            })
    }

    // TO check Ger ECRF summery pdf file
    fun getEcrfPDF(token: String, patientId: String) {
        _isViewLoading.postValue(true)
        profileVMImplementor.checkPatientECRF(token, patientId,
            object : ProfileVMRepository.BaseResponseApiCallBack<BaseResponse?>{
                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveEcrfPDFResponse.postValue(responseData)
                } override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

            })
    }

    // New time slots API

    fun getTimeSLots(token: String, doctorId: String) {
        _isViewLoading.postValue(true)


        profileVMImplementor.getDoctorAvailibility(token,doctorId,
            object : ProfileVMRepository.GetDoctorAvailibilityAPiCallBack<DoctorAvailibilityResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?) {
                    _isViewLoading.postValue(false)
                    _liveDOctorAvailibilityResponse.postValue(responseData)
                }

            })
    }

    fun updateDoctorTimeSLots(token: String, doctorAvailibilityInput: DoctorAvailibilityInput) {
        _isViewLoading.postValue(true)


        profileVMImplementor.updateDoctorAvailibility(token,doctorAvailibilityInput,
            object : ProfileVMRepository.UpdateDoctorAvailibilityAPiCallBack<BaseResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveUpdateDrAvailibilityResponse.postValue(responseData)
                }

            })
    }



    //New location related API

    fun addOrEditEstablishment(token: String, locationId: String?, establishmentInput: EstablishmentInput){
        _isViewLoading.postValue(true)
        if(locationId.isNullOrEmpty()){
            profileVMImplementor.addLocation(token, establishmentInput,
                object : ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>{
                    override fun onSuccess(responseData: BaseResponse?) {
                        _isViewLoading.postValue(false)
                        _liveAddLocationResponse.postValue(responseData)
                    } override fun onError(message: String?) {
                        _isViewLoading.postValue(false)
                        _onMessageError.postValue(message)
                    }

                })
        }else{
            profileVMImplementor.editLocation(token, locationId, establishmentInput,
                object : ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>{
                    override fun onSuccess(responseData: BaseResponse?) {
                        _isViewLoading.postValue(false)
                        _liveAddLocationResponse.postValue(responseData)
                    } override fun onError(message: String?) {
                        _isViewLoading.postValue(false)
                        _onMessageError.postValue(message)
                    }

                })
        }
    }

    fun deleteEstablishment(token: String, locationId: String){
        _isViewLoading.postValue(true)
        profileVMImplementor.deleteLocation(token, locationId,
            object : ProfileVMRepository.AddDoctorLocationAPiCallBack<BaseResponse?>{
                override fun onSuccess(responseData: BaseResponse?) {
                    _isViewLoading.postValue(false)
                    _liveDeleteLocationResponse.postValue(responseData)
                } override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

            })
    }

    fun getEstablishments(token: String) {
        _isViewLoading.postValue(true)


            profileVMImplementor.getLocationsList(token,
                object : ProfileVMRepository.GetDoctorLocationAPiCallBack<DOctorLOcationListResponse?> {
                    override fun onSuccess(responseData: DOctorLOcationListResponse?) {
                        _isViewLoading.postValue(false)
                        _liveLocationListResponse.postValue(responseData)
                    }

                    override fun onError(message: String?) {
                        _isViewLoading.postValue(false)
                        _onMessageError.postValue(message)
                    }

                })
    }





}
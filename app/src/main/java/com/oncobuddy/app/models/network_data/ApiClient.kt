package com.oncobuddy.app.models.network_data

import com.oncobuddy.app.models.pojo.*
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.appointments.AddAppointmentResponse
import com.oncobuddy.app.models.pojo.appointments.input.AddAppointmentInput
import com.oncobuddy.app.models.pojo.appointments.input.AddGuestInput
import com.oncobuddy.app.models.pojo.appointments.input.PaymentInput
import com.oncobuddy.app.models.pojo.appointments.input.TimeSlotsInput
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.models.pojo.aws_credentials.AwsCredentialsResponse
import com.oncobuddy.app.models.pojo.bank_acct_details.BankAccountInput
import com.oncobuddy.app.models.pojo.care_giver_details.AddUserByCCInput
import com.oncobuddy.app.models.pojo.care_giver_details.AssignCGInput
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverResponse
import com.oncobuddy.app.models.pojo.care_team_details.CareTeamResponse
import com.oncobuddy.app.models.pojo.chats.MessageInput
import com.oncobuddy.app.models.pojo.chats.chat_groups.ChatGroupResponse
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatListResponse
import com.oncobuddy.app.models.pojo.daily_questions.AnswerQuestionInput
import com.oncobuddy.app.models.pojo.daily_questions.DailyQuestionsListResponse
import com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityInput
import com.oncobuddy.app.models.pojo.doctor_certification.AddCertificateInput
import com.oncobuddy.app.models.pojo.doctor_locations.DOctorLOcationListResponse
import com.oncobuddy.app.models.pojo.doctor_locations.EstablishmentInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.DoctorAvailibilityResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.doctor_update.DoctorAvailabilityReq
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.find_doctor.FindDoctorResponse
import com.oncobuddy.app.models.pojo.doctors.time_slots_listing.TimeSlotsListingResponse
import com.oncobuddy.app.models.pojo.ecrf_events.EcrfventsResponse
import com.oncobuddy.app.models.pojo.education_degrees.AddEducationInput
import com.oncobuddy.app.models.pojo.education_degrees.DegreesResponse
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactInput
import com.oncobuddy.app.models.pojo.emergency_contacts.EmergencyContactsListResponse
import com.oncobuddy.app.models.pojo.extract_doc_response.UploadDocResponse
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.comments.CommentsListResponse
import com.oncobuddy.app.models.pojo.forums.comments.GetCommentsInput
import com.oncobuddy.app.models.pojo.forums.post_details.PostDetailsResponseNew
import com.oncobuddy.app.models.pojo.forums.shorts.ShortsListResponse
import com.oncobuddy.app.models.pojo.forums.trending_blogs.AddCommentInput
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideosListResponse
import com.oncobuddy.app.models.pojo.genetic_report.GeneticResponse
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.guest_details.GuestDetailsResponse
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalListingResponse
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.invite.InviteInput
import com.oncobuddy.app.models.pojo.login_response.LoginInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.nov_signup.SignupResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput
import com.oncobuddy.app.models.pojo.patient_details_by_cg.PatientDetailsByCGResponse
import com.oncobuddy.app.models.pojo.patient_list.PatientsListResponse
import com.oncobuddy.app.models.pojo.patient_profile.PatientDetailsResponse
import com.oncobuddy.app.models.pojo.patient_transactions.PatientTransactionsResponse
import com.oncobuddy.app.models.pojo.profile.CancerTypesListResponse
import com.oncobuddy.app.models.pojo.profile.WalletBalanceResponse
import com.oncobuddy.app.models.pojo.profile.WalletTransactionsResponse
import com.oncobuddy.app.models.pojo.records_list.AddRecordInput
import com.oncobuddy.app.models.pojo.records_list.RecordsListResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.models.pojo.registration_process.NovRegistration
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import com.oncobuddy.app.models.pojo.response_categories.ResponseCategories
import com.oncobuddy.app.models.pojo.response_sub_categories.CategoryIds
import com.oncobuddy.app.models.pojo.response_sub_categories.ResponseSubCategories
import com.oncobuddy.app.models.pojo.twilio_token.TokenResponse
import com.oncobuddy.app.models.pojo.video_calling.FCMRequest
import com.oncobuddy.app.models.pojo.video_calling.FCMResponse
import com.oncobuddy.app.models.pojo.vouchers_response.VoucherListResponse
import okhttp3.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor

/***
 * Handles all the network calls of the application
 */
object ApiClient {

    //Development URL
    private val BASE_URL = "https://auth.qa.oncobuddy.net/v1/"

    // Production URL
    //private val BASE_URL = "https://auth.prod.oncobuddy.net/v1/"

    const val PATIENT_URL =  "patient/"
    const val OTP_URL ="otp/"
    const val APPOINTMENT_URL ="appointment/"
    private val USER_URL =  "user/"
    private val LOGIN =  "login"

    //apis
    const val METHOD_LOGIN = "user/login"
    const val PATIENT_REGISTER = "patient_register"
    const val METHOD_VERIFY_OTP = "verify_otp"
    const val METHOD_ADD_APPOINTMENT = "schedule"
    const val METHOD_GET_APPOINTMENT_LIST = "get_appointments_by_user"

    const val METHOD_MOBILE_NUMBER = "mobile_number"
    const val METHOD_REGISTER = "user_register"
    const val METHOD_FORGOT_PASSWORD = "user_forgot_password"

    private var serviceApiInterface : ServiceApiInterface? = null
    val cacheSize = (5 * 1024 * 1024).toLong()
    const val HEADER_CACHE_CONTROL = "Cache-Control"
    const val HEADER_PRAGMA = "Pragma"


    fun build() : ServiceApiInterface?{

        // create  api builder
        var builder: Retrofit.Builder = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())

        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            .build()


        // retrofit object
        var retrofit: Retrofit = builder.client(httpClient).build()

        // Service API interface
        serviceApiInterface = retrofit.create(ServiceApiInterface::class.java)

        return serviceApiInterface as ServiceApiInterface

    }



    interface ServiceApiInterface{

        @POST(METHOD_LOGIN)
        fun doLogin(@Body login: LoginInput?): Call<LoginResponse?>

        @POST("send")
        fun sendFcmNotification(@Body fcmRequest: FCMRequest?): Call<FCMResponse?>

        @POST("user/register")
        fun doPatientRegister(@Body registrationInput: NovRegistration?): Call<SignupResponse?>

        @GET("appointment/get_appointments_by_user")
        fun doGetAppointmentList(@Header("Authorization")  token : String): Call<AppointmentsListResponse?>

        @GET("appointment/get_todays")
        fun doTodayAppointments(@Header("Authorization")  token : String): Call<AppointmentsListResponse?>


        @GET("record/{patientId}")
        fun doGetRecordsList(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<RecordsListResponse?>

        @GET("record/{patientId}/report")
        fun doGetPatientGeneticReport(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<RecordsListResponse?>


        @GET("record/{patientId}/summary")
        fun doGetPatientSUmmary(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<RecordsListResponse?>

        @GET("record/{patientId}/report")
        fun doGetPatientGeneticReportNew(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<GeneticReportResponse?>

        @GET("record/{patientId}/summary")
        fun doGetPatientSUmmaryNew(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<GeneticReportResponse?>

        @GET("care_companion/{patientId}/get_records")
        fun doGetPatientRecordsByCC(@Header("Authorization")  token : String,
                             @Path ("patientId") patientId : String): Call<RecordsListResponse?>


        @POST("record/add")
        fun doAddRecord(@Body addRecordInput: AddRecordInput, @Header("Authorization")  token : String): Call<BaseResponse?>

        @POST("care_companion/add_record")
        fun doAddRecordByCC(@Body addRecordInput: AddRecordInput, @Header("Authorization")  token : String): Call<BaseResponse?>

        @POST("record/{recordId}/update")
        fun doUpdateRecord(@Body addRecordInput: AddRecordInput,
                           @Header("Authorization")  token : String,
                           @Path ("recordId") recordId : String): Call<BaseResponse?>

        @POST("care_companion/{recordId}/update_record")
        fun doUpdateRecordByCC(@Body addRecordInput: AddRecordInput,
                           @Header("Authorization")  token : String,
                           @Path ("recordId") recordId : String): Call<BaseResponse?>


        @DELETE("record/{recordId}")
        fun doDeleteRecord(@Header("Authorization")  token : String,
                           @Path ("recordId") recordId : String): Call<BaseResponse?>

        @DELETE("patient/delete/care_taker/{id}")
        fun doDeleteCareGiver(@Header("Authorization")  token : String, @Path ("id") id : String): Call<BaseResponse?>


        @DELETE("care_companion/{recordId}/delete_record")
        fun doDeleteRecordByCC(@Header("Authorization")  token : String,
                           @Path ("recordId") recordId : String): Call<BaseResponse?>

        @POST("care_companion/delete/patient/{patientId}")
        fun doDeletePatientByCC(@Header("Authorization")  token : String,
                               @Path ("patientId") patientId : String): Call<BaseResponse?>

        @Multipart
        @POST("record/file_upload")
        fun uploadFile(@Header("Authorization")  token : String, @Part file: MultipartBody.Part): Call<BaseResponse?>?

        @POST(APPOINTMENT_URL+METHOD_ADD_APPOINTMENT)
        fun doAddOrUpdateAppointment(@Body addAppointmentInput: AddAppointmentInput?,
                                     @Header("Authorization")  token : String): Call<AddAppointmentResponse?>


        @POST(APPOINTMENT_URL+"{appointmentId}"+"/reschedule")
        fun doRescheduleAppointment(@Body addAppointmentInput: AddAppointmentInput?,
                                    @Header("Authorization")  token : String, @Path ("appointmentId") appointmentId : String): Call<ResponseBody?>


        @POST("{userId}/appointment/update_appointment")
        fun doUpdateAppointment(@Body addAppointmentInput: AddAppointmentInput?,
                                     @Header("Authorization")  token : String): Call<BaseResponse>

        @POST("appointment/{appointmentId}/add_guest")
        fun doAddGuestToAppointment(@Body addGuestInput: AddGuestInput?,
                                @Header("Authorization")  token : String, @Path ("appointmentId") appointmentId : String): Call<BaseResponse?>

        @POST("appointment/{appointmentId}/update/cancel")
        fun doCancelAppointment(@Header("Authorization")  token : String, @Path ("appointmentId") appointmentId : String): Call<ResponseBody?>

        @POST("appointment/{appointmentId}/call/start")
        fun doStartCall(@Header("Authorization")  token : String,
                        @Path ("appointmentId") appointmentId : String): Call<BaseResponse?>

        @GET("appointment/{appointmentId}/get/token")
        fun doGetTwilioTOken(@Header("Authorization")  token : String,
                             @Path ("appointmentId") appointmentId : String): Call<TokenResponse?>

        @POST("appointment/{appointmentId}/call/end")
        fun doEndCall(@Header("Authorization")  token : String, @Path ("appointmentId") appointmentId : String): Call<BaseResponse?>

        @POST("appointment/{appointmentId}/update/payment")
        fun doPayment(@Header("Authorization")  token : String,
                      @Path ("appointmentId") appointmentId : String,
                      @Body paymentInput: PaymentInput?): Call<BaseResponse?>

        @GET("doctor/get_doctors")
        fun doGetDoctorsList(@Header("Authorization")  token : String): Call<DoctorsListingResponse?>

        @GET("patient/allied/councillors")
        fun doGetAlliedDoctorsList(@Header("Authorization")  token : String): Call<DoctorsListingResponse?>

        @GET("patient/{patientId}/get_doctors")
        fun getMyDoctors(@Header("Authorization")  token : String, @Path ("patientId") doctorId : String): Call<DoctorsListingResponse?>

        @POST("doctor_availability/{doctorUserId}/check")
        fun doGetDoctorTimeSlots(@Header("Authorization")  token : String,
                                 @Path ("doctorUserId") doctorId : String,
                                 @Body timeSlotsInput: TimeSlotsInput?): Call<TimeSlotsListingResponse?>

        @POST("doctor_availability/update")
        fun updateDoctorAvailibility(@Header("Authorization")  token : String, @Body doctorAvailabilityReq: DoctorAvailabilityReq?): Call<BaseResponse?>

        @GET("doctor_availability/get/{doctorUserId}")
        fun getDoctorTimings(@Header("Authorization")  token : String,
                                     @Path ("doctorUserId") doctorId : String): Call<DoctorAvailibilityResponse?>



        @POST("patient/emergency/add")
        fun doAddEmergencyContact(@Body emergencyContactInput: EmergencyContactInput?,
                                  @Header("Authorization")  token : String): Call<BaseResponse?>

        @GET("aws/credentials")
        fun doGetAwsCredentials(@Header("Authorization")  token : String): Call<AwsCredentialsResponse?>


        @POST("patient/add/care_taker")
        fun doAddCareCompanion(@Body addCareCompanionInput: AddCareCompanionInput?,
                                  @Header("Authorization")  token : String): Call<AddCareTakerResponse?>

        @POST("doctor/add/care_assistant")
        fun doAddCareAssistant(@Body addCareCompanionInput: AddCareCompanionInput?,
                               @Header("Authorization")  token : String): Call<AddCareTakerResponse?>

        @POST("patient/emergency/alert")
        fun doEmergencyAlert(@Header("Authorization")  token : String): Call<BaseResponse?>



        @DELETE("patient/emergency/delete/{emergencyContactId}")
        fun doDeleteEmergencyCOntact(@Header("Authorization")  token : String,
                                     @Path ("emergencyContactId") emergencyContactId : String): Call<BaseResponse?>


        @GET("patient/emergency/list")
        fun doGetEmergencyContactsList(@Header("Authorization")  token : String):
                Call<EmergencyContactsListResponse?>



        @GET("patient/get_cancerTypes")
        fun doGetCancerTypes(@Header("Authorization")  token : String): Call<CancerTypesListResponse?>

        @GET("doctor/get_hospitals")
        fun doGetHospitals(@Header("Authorization")  token : String): Call<HospitalListingResponse?>

        @GET("patient/get_cancerSubTypes/{cancerTypeId}")
        fun doGetCancerSubTypes(@Header("Authorization")  token : String,
                                @Path ("cancerTypeId") cancerTypeId : String
        ): Call<CancerTypesListResponse?>


        @POST("patient/update_details")
        fun doUpdateProfile(@Header("Authorization")  token : String,
                            @Body registrationInput: RegistrationInput?): Call<LoginResponse?>

        @POST("care_companion/update_details")
        fun doUpdateCareTakerProfile(@Header("Authorization")  token : String,
                            @Body registrationInput: RegistrationInput?): Call<LoginResponse?>



        @POST("doctor/update_details")
        fun doUpdateDoctorProfile(@Header("Authorization")  token : String,
                            @Body registrationInput: DoctorRegistrationInput?): Call<LoginResponse?>


        @GET("record/pro_questions/get")
        fun doDailyQuestionsList(@Header("Authorization")  token : String): Call<DailyQuestionsListResponse?>

        @GET("doctor/get_patients_by_doctor")
        fun doGetPatients(@Header("Authorization")  token : String): Call<PatientsListResponse?>

        @POST("care_companion/get_patients")
        fun doGetPatientsByCareCOmpanion(@Header("Authorization")  token : String, @Body searchQueryInput: SearchQueryInput): Call<PatientsListResponse?>

        @POST("record/pro/{patientId}/add_patient_answers")
        fun doAnswerDailyQuestion(@Header("Authorization")  token : String,
                                  @Path ("patientId") patientId : String, @Body answerQuestionInput: AnswerQuestionInput?): Call<BaseResponse?>

        @POST("otp/generate_otp")
        fun doGenerateOTP(@Body generateOTPInput: GenerateOTPInput): Call<BaseResponse?>

        @POST("user/forgot_password")
        fun doCallForgotPassword(@Body forgotPwdInput: ForgotPwdInput): Call<BaseResponse?>

        @POST("user/get_otp_for_login")
        fun doGetOtpForLogin(@Body loginInput: com.oncobuddy.app.models.pojo.initial_login.LoginInput): Call<BaseResponse?>

        @POST("user/get_otp_for_registration")
        fun doGetOtpForRegistration(@Body loginInput: com.oncobuddy.app.models.pojo.initial_login.LoginInput): Call<BaseResponse?>

        @POST("user/verify_login_otp")
        fun doVeriFyOtp(@Body verifyLoginOtpInput: VerifyLoginOtpInput): Call<LoginResponse?>

        // Forums APIs
        @GET("forums/trending/videos")
        fun doGetTrendingVideosList(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>

        @POST("forums/get_videos")
        fun doGetAllVideosList(@Header("Authorization")  token : String,
                               @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>

        @GET("forums/qow")
        fun doQuestionOfTheWeek(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>


        @POST("forums/get_questions/community")
        fun doGetCommunityPost(@Header("Authorization")  token : String,
                               @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>

        @POST("forums/get_questions/expert")
        fun doGetExpertQuestions(@Header("Authorization")  token : String,
                               @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>


        @POST("forums/get_questions/onco")
        fun doGetOncoDiscussions(@Header("Authorization")  token : String,
                               @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>


        @POST("forums/get_blogs")
        fun doGetAllBlogsList(@Header("Authorization")  token : String,
                               @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>

        @POST("forums/get_questions")
        fun doGetAllQuestionsList(@Header("Authorization")  token : String,
                              @Body searchQueryInput: SearchQueryInput): Call<TrendingVideosListResponse?>

        @GET("forums/my/questions")
        fun doGetMyQuestions(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>

        @GET("forums/trending/discussions")
        fun doGetTrendingQuestions(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>


        @GET("forums/saved_posts/list")
        fun doGetSavedPosts(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>


        @GET("forums/trending/blogs")
        fun doGetTrendingBlogsList(@Header("Authorization")  token : String): Call<TrendingVideosListResponse?>

        @POST("forums/{postId}/report")
        fun doReportPost(@Header("Authorization")  token : String, @Path ("postId") postId : String): Call<BaseResponse?>

        @POST("forums/{postId}/save")
        fun doSavePost(@Header("Authorization")  token : String, @Path ("postId") postId : String): Call<BaseResponse?>


        @POST("forums/{postId}/unsave")
        fun doUnsavePost(@Header("Authorization")  token : String, @Path ("postId") postId : String): Call<BaseResponse?>


        @POST("forums/{postId}/add_comment")
        fun addCommment(@Header("Authorization")  token : String, @Path ("postId") postId : String,
                        @Body addCommentInput: AddCommentInput): Call<BaseResponse?>

        @POST("forums/{commentId}/edit_comment")
        fun editCommment(@Header("Authorization")  token : String, @Path ("commentId") commentId : String,
                        @Body addCommentInput: AddCommentInput): Call<BaseResponse?>

        @POST("forums/post/add")
        fun addQuestion(@Header("Authorization")  token : String, @Body addQuestionInput: AddQuestionInput): Call<BaseResponse?>

        @POST("forums/{postId}/edit")
        fun editQuestion(@Header("Authorization")  token : String, @Path ("postId") postId : String, @Body addQuestionInput: AddQuestionInput): Call<BaseResponse?>

        @POST("forums/{postId}/like_unlike")
        fun doLikeOrUnlikePost(@Header("Authorization")  token : String, @Path ("postId") postId : String): Call<BaseResponse?>

        @POST("forums/{commentId}/like")
        fun doLikeOrUnlikeCOmment(@Header("Authorization")  token : String, @Path ("commentId") commentId : String): Call<BaseResponse?>

        @POST("forums/{commentId}/delete")
        fun doDeleteCOmment(@Header("Authorization")  token : String, @Path ("commentId") commentId : String): Call<BaseResponse?>

        @GET("forums/{postId}/delete")
        fun doDeletePost(@Header("Authorization")  token : String, @Path ("postId") commentId : String): Call<BaseResponse?>

        @POST("forums/{postId}/comments")
        fun getCommentsList(@Header("Authorization")  token : String, @Path ("postId") postId : String, @Body getCommentInput: GetCommentsInput): Call<CommentsListResponse?>

        @GET("forums/{postId}/get")
        fun getPostDetails(@Header("Authorization")  token : String,
                           @Path ("postId") postId : String):
                           Call<PostDetailsResponseNew?>


        @GET("forums/shorts/list")
        fun doGetShortsList(@Header("Authorization")  token : String): Call<ShortsListResponse?>

        @GET("appointment/wallet/balance")
        fun getWalletBalance(@Header("Authorization") token: String): Call<WalletBalanceResponse>

        @GET("appointment/wallet/transactions")
        fun getWalletTransactions(@Header("Authorization") token: String): Call<WalletTransactionsResponse>

        @POST("appointment/wallet/withdraw")
        fun withdrawBalance(@Header("Authorization") token: String): Call<BaseResponse>

        @GET("patient/{patientId}/ecrf/check")
        fun doCheckPatientECRF(@Header("Authorization")  token : String, @Path("patientId") patientId : String): Call<BaseResponse?>

        @GET("patient/get/care_taker")
        fun doGetCareTakerDetails(@Header("Authorization")  token : String): Call<CareGiverResponse?>

        @GET("patient/{patientId}/ecrf/summary")
        fun doGetEcrfSummaryPDF(@Header("Authorization")  token : String, @Path("patientId") patientId : String): Call<BaseResponse?>

        @POST("appointment/{appointmentId}/request/callback")
        fun doRequestCallback(@Header("Authorization")  token : String, @Path("appointmentId") patientId : String): Call<BaseResponse?>


        @GET("patient/doctor/find/{phoneNumber}")
        fun doFindDoctor(@Header("Authorization")  token : String,
                                     @Path ("phoneNumber") phoneNumber : String): Call<FindDoctorResponse?>

        @POST("patient/assign/doctor/{doctorId}")
        fun doAssignDoctor(@Header("Authorization")  token : String,
                         @Path ("doctorId") doctorId : String): Call<BaseResponse?>

        @GET("patient/{patientId}/ecrf/events")
        fun doGetEcrfEvents(@Header("Authorization")  token : String,
                           @Path ("patientId") patientId : String): Call<EcrfventsResponse?>


        @GET("doctor/patient/find/{phoneNumber}")
        fun doFindPatient(@Header("Authorization")  token : String,
                         @Path ("phoneNumber") phoneNumber : String): Call<FindDoctorResponse?>

        @GET("appointment/{appointmentId}/get_guest")
        fun doGetGuestDetails(@Header("Authorization")  token : String,
                          @Path ("appointmentId") appointmentId : String): Call<GuestDetailsResponse?>

        @POST("doctor/assign/doctor/{doctorId}")
        fun doAssignPatient(@Header("Authorization")  token : String,
                           @Path ("doctorId") doctorId : String): Call<BaseResponse?>

        @POST("care_companion/add/doctor")
        fun doAddDoctorByCC(@Body addUserByCCInput: AddUserByCCInput?,@Header("Authorization")  token : String): Call<BaseResponse?>

        @POST("care_companion/add/patient")
        fun doAddPatientByCC(@Body addUserByCCInput: AddUserByCCInput?, @Header("Authorization")  token : String): Call<BaseResponse?>

        @POST("care_companion/doctor/{doctorId}/verify")
        fun doVerifyDoctorByCC(@Header("Authorization")  token : String, @Path ("doctorId") doctorId : String): Call<BaseResponse?>

        @GET("appointment/{doctorId}/voucher")
        fun doGetVouchersList(@Header("Authorization")  token : String, @Path("doctorId")  doctorId : String): Call<VoucherListResponse?>

        @GET("doctor/{doctorId}/get_doctor")
        fun doGetDoctorDetails(@Header("Authorization")  token : String, @Path("doctorId")  doctorId : String): Call<DoctorDetailsResponse?>

        @GET("patient/{patientId}/get_patient")
        fun doGetPatientDetails(@Header("Authorization")  token : String, @Path("patientId")  doctorId : String): Call<PatientDetailsResponse?>

        @GET("doctor/patient/{patientId}")
        fun doGetPatientDetailsByDoctor(@Header("Authorization")  token : String, @Path("patientId")  doctorId : String): Call<PatientDetailsResponse?>


        @POST("appointment/{appointmentId}/request/reschedule")
        fun doRequestReSchedule(@Header("Authorization")  token : String, @Path("appointmentId") appointmentId : String): Call<BaseResponse?>

        // Accept/Reject connection requests
        @POST("doctor/accept/patient/{patientId}")
        fun doDoctorAcceptConnectionRequest(@Header("Authorization")  token : String, @Path("patientId") patientId : String): Call<BaseResponse?>

        @POST("doctor/reject/patient/{patientId}")
        fun doDoctorRejectConnectionRequest(@Header("Authorization")  token : String, @Path("patientId") patientId : String): Call<BaseResponse?>

        @POST("patient/accept/doctor/{doctorId}")
        fun doPatientAcceptConnectionRequest(@Header("Authorization")  token : String, @Path("doctorId") doctorId : String): Call<BaseResponse?>

        @POST("patient/reject/doctor/{doctorId}")
        fun doPatientRejectConnectionRequest(@Header("Authorization")  token : String, @Path("doctorId") doctorId : String): Call<BaseResponse?>

        @POST("patient/doctor/invite")
        fun doPatientInviteDoctor(@Header("Authorization")  token : String, @Body  inviteInput: InviteInput): Call<BaseResponse?>

        // chat APIs
        @GET("chat/groups/active")
        fun doGetActiveGroups(@Header("Authorization")  token : String): Call<ChatGroupResponse?>

        @POST("chat/start/{receiver_id}")
        fun doStartChat(@Header("Authorization")  token : String, @Path("receiver_id") receiver_id : String): Call<BaseResponse?>

        @POST("chat/message/send")
        fun doSendMessage(@Header("Authorization")  token : String, @Body messageRequest : MessageInput): Call<BaseResponse?>

        @GET("chat/{group_id}")
        fun doGetAllChats(@Header("Authorization")  token : String, @Path("group_id") group_id : String): Call<ChatListResponse?>

        @POST("chat/message/{message_id}/read")
        fun doUpdateMsgRead(@Header("Authorization")  token : String, @Path("message_id") messageId : String): Call<BaseResponse?>

        @POST("chat/message/{message_id}/sent")
        fun doUpdateMsgSent(@Header("Authorization")  token : String, @Path("message_id") messageId : String): Call<BaseResponse?>

        @POST("chat/message/{message_id}/received")
        fun doUpdateMsgReceived(@Header("Authorization")  token : String, @Path("message_id") messageId : String): Call<BaseResponse?>


        @POST("complaint/new")
        fun doSendComplaint(@Header("Authorization")  token : String,
                            @Body complaintInput: ComplaintInput): Call<BaseResponse?>

        @POST("doctor/degree/add")
        fun doAddEducation(@Header("Authorization")  token : String,
                            @Body educationInput:AddEducationInput): Call<BaseResponse?>

        @GET("doctor/degrees")
        fun doGetAllEducations(@Header("Authorization")  token : String): Call<DegreesResponse?>

        @DELETE("doctor/degree/{degreeId}")
        fun doDeleteEducation(@Header("Authorization")  token : String, @Path ("degreeId") degreeId : String): Call<BaseResponse?>

        @POST("doctor/certificate/add")
        fun doAddCertification(@Header("Authorization")  token : String,
                           @Body addCertificateInput: AddCertificateInput): Call<BaseResponse?>


        //time slots

        @GET("doctor_availability/get/{doctorUserId}")
        fun doGetDoctorAvailibilitySlots(@Header("Authorization")  token : String, @Path ("doctorUserId") doctorUserId : String):
                                    Call<com.oncobuddy.app.models.pojo.doctor_availibility.DoctorAvailibilityResponse?>


        @POST("doctor_availability/update")
        fun doUpdateDoctorTImeSLots(@Header("Authorization")  token : String, @Body  doctorAvailibilityInput: DoctorAvailibilityInput): Call<BaseResponse?>



        // locations
        @GET("doctor/location")
        fun doGetDoctorLocations(@Header("Authorization")  token : String): Call<DOctorLOcationListResponse?>

        @POST("doctor/location/add")
        fun doAddDoctorLocation(@Header("Authorization")  token : String, @Body  establishmentInput: EstablishmentInput): Call<BaseResponse?>

        @POST("doctor/location/edit/{locationId}")
        fun doEditLocation(@Header("Authorization")  token : String,
                                @Path ("locationId") locationId : String,
                                @Body  establishmentInput: EstablishmentInput): Call<BaseResponse?>

        @DELETE("doctor/location/{locationId}")
        fun doDeleteLocation(@Header("Authorization")  token : String,
                             @Path ("locationId") locationId : String): Call<BaseResponse?>

        // Document data extract new

        @Multipart
        @POST("record/file_upload/extract")
        fun doUploadDoc(@Header("Authorization")  token : String, @Part file: MultipartBody.Part): Call<UploadDocResponse?>

        // patient transactions
        @GET("appointment/transactions")
        fun doGetPatientTransactions(@Header("Authorization")  token : String): Call<PatientTransactionsResponse?>

        @GET("patient/get_care_companion")
        fun doGetPatientCaretakerDetails(@Header("Authorization")  token : String): Call<CareTeamResponse?>

        // Delete Account API
        @POST("account/delete")
        fun doDeleteUserAccount(@Header("Authorization")  token : String,
                                @Body  deleteAccountResponse: DeleteAccountInput): Call<BaseResponse?>


        @GET("patient/{patientId}/ecrf/report")
        fun doGetPatientEcrfReport(@Header("Authorization")  token : String, @Path ("patientId") patientId : String): Call<GeneticResponse?>

        @POST("doctor/add/bank")
        fun doAddBankAcc(@Header("Authorization")  token : String, @Body bankAccountInput: BankAccountInput): Call<BaseResponse?>

        // New APIs on 04th Feb 2022
        @GET("record/report_categories")
        fun doGetTagsCategories(@Header("Authorization")  token : String): Call<ResponseCategories?>

        @POST("record/report_sub_categories")
        fun doGetTagsSubCategories(
            @Header("Authorization")  token : String,
            @Body categoryIds: CategoryIds
        ): Call<ResponseSubCategories?>

        // Care giver APIs

        @GET("caregiver/care_code/{careCode}")
        fun getPatientDetailsByCareCOde(@Header("Authorization")  token : String,
                                        @Path ("careCode") careCode : String): Call<PatientDetailsByCGResponse?>

        @GET("caregiver/get_patient")
        fun getPatientDetailsByCG(@Header("Authorization")  token : String): Call<PatientDetailsByCGResponse?>


        @GET("caregiver/care_companion")
        fun doGetCareCompanionDetailsByCG(@Header("Authorization")  token : String): Call<CareTeamResponse?>

        @GET("caregiver/patient/appointments")
        fun doGetAppointmentListByCG(@Header("Authorization")  token : String): Call<AppointmentsListResponse?>

        @POST("caregiver/patient/assign")
        fun assignPatientToCG(@Header("Authorization")  token : String, @Body assignCGInput: AssignCGInput):
                              Call<BaseResponse?>

        @GET("caregiver/patient/doctors")
        fun getPatientDoctorsByCG(@Header("Authorization")  token : String): Call<DoctorsListingResponse?>

        @GET("caregiver/patient/records")
        fun doGetPatientRecordsByCG(@Header("Authorization")  token : String): Call<RecordsListResponse?>

        @POST("caregiver/add_record")
        fun doAddRecordByCG(@Body addRecordInput: AddRecordInput,
                            @Header("Authorization")  token : String): Call<BaseResponse?>

        @POST("caregiver/update_details")
        fun doUpdateCGDetails(@Header("Authorization")  token : String, @Body careGiverInput: CareGiverInput): Call<BaseResponse?>

        @Multipart
        @POST("caregiver/record/upload/{patientId}")
        fun uploadFileByCG(@Header("Authorization")  token : String,
                           @Part file: MultipartBody.Part,
                           @Path ("patientId") patientId : String):
                           Call<UploadDocResponse?>?

        @DELETE("caregiver/record/delete/{patientId}/{recordId}")
        fun doDeleteRecordByCG(@Header("Authorization")  token : String, @Path("patientId")  patientId : String,
                               @Path ("recordId") recordId : String): Call<BaseResponse?>

        @POST("caregiver/record/update/{patientId}/{recordId}")
        fun doUpdateRecordByCG(@Body addRecordInput: AddRecordInput,
                               @Header("Authorization")  token : String,
                               @Path ("recordId") recordId : String,
                               @Path("patientId")  patientId : String): Call<BaseResponse?>

        @POST("caregiver/patient/update_details")
        fun doUpdatePatientProfileByCG(@Header("Authorization")  token : String,
                            @Body registrationInput: RegistrationInput?): Call<LoginResponse?>


    }
}
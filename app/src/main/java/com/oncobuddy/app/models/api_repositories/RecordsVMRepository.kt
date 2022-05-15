package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.BaseImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.extract_doc_response.UploadDocResponse
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.records_list.AddRecordInput
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.models.pojo.records_list.RecordsListResponse
import com.oncobuddy.app.models.pojo.response_categories.ResponseCategories
import com.oncobuddy.app.models.pojo.response_sub_categories.CategoryIds
import com.oncobuddy.app.models.pojo.response_sub_categories.ResponseSubCategories
import com.oncobuddy.app.utils.Constants
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Responsitory to handle all
 */

class RecordsVMRepository : RecrodsVMImplementor {

    private var getRecordsListCall : Call<RecordsListResponse?>? = null
    private var viewReportCall : Call<GeneticReportResponse?>? = null
    private var viewSummaryCall : Call<GeneticReportResponse?>? = null
    private var addRecordCall : Call<BaseResponse?>? = null
    private var updateRecordCall : Call<BaseResponse?>? = null
    private var deleteRecordCall : Call<BaseResponse?>? = null
    private var uploadFileToS3Call : Call<BaseResponse?>? = null
    private var docUploadCall : Call<UploadDocResponse?>? = null
    private var categoriesResponseData: Call<ResponseCategories?>? =  null
    private var subCategoriesResponseData: Call<ResponseSubCategories?>? =  null

    interface ApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface CategoriesApiCallBack<T>{
        fun onSuccess(responseData : ResponseCategories?)
        fun onError(message : String?)
    }

    interface SubCategoriesApiCallBack<T>{
        fun onSuccess(responseData : ResponseSubCategories?)
        fun onError(message : String?)
    }

    interface UploadS3ApiCallBack<T>{
        fun onFileUploadSuccess(data : BaseResponse?)
        fun onFileUploadError(message : String?)
    }

    interface UploadDocApiCallBack<T>{
        fun onDocUploadSuccess(data : UploadDocResponse?)
        fun onError(message : String?)
    }

    interface ListApiCallBack<T>{
        fun onListSuccess(responseData : List<Record>?)
        fun onError(message : String?)
    }

    interface ViewReportApiCallBack<T>{
        fun onReportSuccess(responseData : GeneticReportResponse?)
        fun onError(message : String?)
    }

    override fun getRecordsList(
        source: String,
        token: String,
        patientId: String,
        callback: ListApiCallBack<RecordsListResponse?>
    ) {
        if(source.equals(Constants.ROLE_CARE_COMPANION)){
            getRecordsListCall = ApiClient.build()?.doGetPatientRecordsByCC(token, patientId)
        }else if(source.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            getRecordsListCall = ApiClient.build()?.doGetPatientRecordsByCG(token)
        }
        else{
            getRecordsListCall = ApiClient.build()?.doGetRecordsList(token, patientId)
        }


        getRecordsListCall?.enqueue(object : Callback<RecordsListResponse?>{
            override fun onFailure(call: Call<RecordsListResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<RecordsListResponse?>?,
                                    response: Response<RecordsListResponse?>) {

                if(response.code() == 400){
                    Log.d("records_log", "Catched")
                }

                Log.d("records_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    response.body()?.payLoad?.let { callback.onListSuccess(it) }
                    Log.d("records_log", "success2 "+response.message())
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

    override fun getGeneticReport(
        token: String,
        patientId: String,
        callback: ViewReportApiCallBack<GeneticReportResponse?>
    ) {

        viewReportCall = ApiClient.build()?.doGetPatientGeneticReportNew(token, patientId)

        viewReportCall?.enqueue(object : Callback<GeneticReportResponse?>{
            override fun onFailure(call: Call<GeneticReportResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<GeneticReportResponse?>?,
                                    response: Response<GeneticReportResponse?>) {

                if(response.code() == 400){
                    Log.d("records_log", "Catched")
                }

                Log.d("records_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onReportSuccess(response.body())
                    Log.d("records_log", "success2 "+response.message())
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

    override fun getSUmmary(
        token: String,
        patientId: String,
        callback: ViewReportApiCallBack<GeneticReportResponse?>
    ) {
        viewSummaryCall = ApiClient.build()?.doGetPatientSUmmaryNew(token, patientId)

        viewSummaryCall?.enqueue(object : Callback<GeneticReportResponse?>{
            override fun onFailure(call: Call<GeneticReportResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<GeneticReportResponse?>?,
                                    response: Response<GeneticReportResponse?>) {

                if(response.code() == 400){
                    Log.d("records_log", "Catched")
                }

                Log.d("records_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onReportSuccess(response.body())
                    Log.d("records_log", "success2 "+response.message())
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

    override fun addRecord(
        source: String,
        addRecordInput: AddRecordInput,
        token:String,
        userId: String,
        callback: ApiCallBack<BaseResponse?>
    ) {
        if(source.equals(Constants.ROLE_CARE_COMPANION)){
            addRecordCall = ApiClient.build()?.doAddRecordByCC(addRecordInput, token)
        }else if(source.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            addRecordCall = ApiClient.build()?.doAddRecordByCG(addRecordInput, token)
        } else{
            addRecordCall = ApiClient.build()?.doAddRecord(addRecordInput, token)
        }


        addRecordCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("records_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
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

    override fun updateRecord(
        source: String,
        addRecordInput: AddRecordInput,
        token: String,
        userId: String,
        recordId: String,
        patientId: String,
        callback: ApiCallBack<BaseResponse?>
    ) {
        if(source.equals(Constants.ROLE_CARE_COMPANION)){
            updateRecordCall = ApiClient.build()?.doUpdateRecordByCC(addRecordInput, token, recordId)
        }else if(source.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            updateRecordCall = ApiClient.build()?.doUpdateRecordByCG(addRecordInput, token, recordId, patientId)
        }
        else{
            updateRecordCall = ApiClient.build()?.doUpdateRecord(addRecordInput, token,recordId)
        }



        updateRecordCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("records_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
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

    override fun deleteRecord(
        source: String,
        patientId: String,
        token: String,
        recordId: String,
        callback: ApiCallBack<BaseResponse?>
    ) {
        if(source.equals(Constants.ROLE_CARE_COMPANION)){
            deleteRecordCall = ApiClient.build()?.doDeleteRecordByCC(token,recordId)
        }else if(source.equals(Constants.ROLE_PATIENT_CARE_GIVER)){
            deleteRecordCall = ApiClient.build()?.doDeleteRecordByCG(token, patientId, recordId)
        }
        else{
            deleteRecordCall = ApiClient.build()?.doDeleteRecord(token,recordId)
        }



        deleteRecordCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("delete_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("delete_log", "Catched")
                }

                Log.d("delete_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("delete_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("delete_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("delete_log","API Failed here "+ignored.toString())
                    }

                    Log.d("delete_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getCategories(
        token: String,
        callback: CategoriesApiCallBack<ResponseCategories?>
    ) {
        categoriesResponseData = ApiClient.build()?.doGetTagsCategories(token)

        categoriesResponseData?.enqueue(object : Callback<ResponseCategories?>{

            override fun onFailure(call: Call<ResponseCategories?>?, t: Throwable?) {
                Log.d("upload_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<ResponseCategories?>?,
                                    response: Response<ResponseCategories?>) {

                if(response.code() == 400){
                    Log.d("upload_log", "Catched")
                }

                Log.d("upload_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("upload_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("upload_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("upload_log","API Failed here "+ignored.toString())
                    }

                    Log.d("upload_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getSubCategories(
        token: String,
        categoryIds: CategoryIds,
        callback: SubCategoriesApiCallBack<ResponseSubCategories?>
    ) {
        subCategoriesResponseData = ApiClient.build()?.doGetTagsSubCategories(token, categoryIds)

        subCategoriesResponseData?.enqueue(object : Callback<ResponseSubCategories?>{

            override fun onFailure(call: Call<ResponseSubCategories?>?, t: Throwable?) {
                Log.d("upload_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<ResponseSubCategories?>?,
                                    response: Response<ResponseSubCategories?>) {

                if(response.code() == 400){
                    Log.d("upload_log", "Catched")
                }

                Log.d("upload_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("upload_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("upload_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("upload_log","API Failed here "+ignored.toString())
                    }

                    Log.d("upload_log", "Err2 "+response.message())
                }
            }
        })
    }


    override fun uploadFileToS3(token: String, file: MultipartBody.Part, callback: UploadS3ApiCallBack<BaseResponse?>) {
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

    override fun uploadDoc(
        patientId: String,
        token: String,
        file: MultipartBody.Part,
        callback: UploadDocApiCallBack<UploadDocResponse?>
    ) {
        if(patientId.isNullOrEmpty()){
            docUploadCall = ApiClient.build()?.doUploadDoc(token,file)
        }else{
            docUploadCall = ApiClient.build()?.uploadFileByCG(token, file, patientId)
        }

        docUploadCall?.enqueue(object : Callback<UploadDocResponse?>{

            override fun onFailure(call: Call<UploadDocResponse?>?, t: Throwable?) {
                Log.d("upload_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<UploadDocResponse?>?,
                                    response: Response<UploadDocResponse?>) {

                if(response.code() == 400){
                    Log.d("upload_log", "Catched")
                }

                Log.d("upload_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onDocUploadSuccess(response.body())
                    Log.d("upload_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("upload_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("upload_log","API Failed here "+ignored.toString())
                    }

                    Log.d("upload_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun cancel() {
          getRecordsListCall?.cancel()
    }



}

interface RecrodsVMImplementor : BaseImplementor {
    fun getRecordsList(source : String, token: String, patientId: String,
                       callback: RecordsVMRepository.ListApiCallBack<RecordsListResponse?>)

    fun getGeneticReport(token: String, patientId: String,
                       callback: RecordsVMRepository.ViewReportApiCallBack<GeneticReportResponse?>)

    fun getSUmmary(token: String, patientId: String,
                       callback: RecordsVMRepository.ViewReportApiCallBack<GeneticReportResponse?>)

    fun addRecord(source : String, addRecordInput: AddRecordInput, token: String,userId: String,
                  callback: RecordsVMRepository.ApiCallBack<BaseResponse?>)

    fun updateRecord(source : String, addRecordInput: AddRecordInput, token: String,userId: String, recordId: String,patientId: String = "",
                  callback: RecordsVMRepository.ApiCallBack<BaseResponse?>)

    fun deleteRecord(source: String,patientId: String, token: String,userId: String,
                     callback: RecordsVMRepository.ApiCallBack<BaseResponse?>)

    fun getCategories(token: String,
                     callback: RecordsVMRepository.CategoriesApiCallBack<ResponseCategories?>)

    fun getSubCategories(token: String, categoryIds: CategoryIds,
                         callback: RecordsVMRepository.SubCategoriesApiCallBack<ResponseSubCategories?>)

    fun uploadFileToS3(token: String, file: MultipartBody.Part, callback: RecordsVMRepository.UploadS3ApiCallBack<BaseResponse?>)

    fun uploadDoc(patientId: String = "",token: String, file: MultipartBody.Part, callback: RecordsVMRepository.UploadDocApiCallBack<UploadDocResponse?>)

}
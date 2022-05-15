package com.oncobuddy.app.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalListingResponse
import com.oncobuddy.app.models.pojo.new_tags.MainTag
import com.oncobuddy.app.models.pojo.new_tags.SuperTag
import com.oncobuddy.app.models.pojo.tags_response.TagsResponse
import com.oncobuddy.app.views.activities.NovSplashActivity
import java.io.InputStream
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Common methods
 * Contains all static methods for the application
 * @constructor Create empty Common methods
 */

object CommonMethods {

    const val SHOULD_PRINT_LOG: Boolean = true

    fun showLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) Log.d(tag, message)
    }

    fun getStringWithOnePadding(number: String): String {
        return number.padStart(2, '0')
    }

    /**
     * Change activity
     * It helps to redirect from one activity to another activity with animation
     *
     * @param currentActivity
     * @param cls
     * @param shouldFinish true if you want to clear previous one
     */
    fun changeActivity(
        currentActivity: Activity,
        cls: Class<*>?,
        shouldFinish: Boolean
    ) {
        val mIntent = Intent(currentActivity, cls)
        currentActivity.startActivity(mIntent)
        currentActivity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        if (shouldFinish) currentActivity.finish()
    }

    fun changeActivity(
        currentActivity: Activity,
        cls: Class<*>?,
        shouldFinish: Boolean,
        shouldClearStack: Boolean
    ) {
        val mIntent = Intent(currentActivity, cls)
        if (shouldClearStack) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.d("clear_stack", "CLeared stack")
        }
        currentActivity.startActivity(mIntent)
        currentActivity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out)
        if (shouldFinish) currentActivity.finish()
    }

    @JvmStatic
    fun getResizedBitmap(
        image: Bitmap,
        maxSize: Int
    ): Bitmap? {
        return try {
            var width = image.width
            var height = image.height
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: NullPointerException) {
            image
        }
    }

    @JvmStatic
    fun isNotNullOrEmpty(s: String?): Boolean {
        return !(s == null || s.trim { it <= ' ' } == Constants.STR_BLANK || s.equals(
            Constants.STR_NULL,
            ignoreCase = true
        ))
    }

    @JvmStatic
    fun calculateWidth(): Int {
        val displayMetrics = DisplayMetrics()
        FourBaseCareApp.activityFromApp.windowManager.getDefaultDisplay().getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun CheckGps(mActivity: Activity?, e: java.lang.Exception) {
        try {
// Show the dialog by calling startResolutionForResult(),
// and check the result in onActivityResult().
            val resolvable = e as ResolvableApiException
            resolvable.startResolutionForResult(
                mActivity,
                Constants.GPS_ENABLE
            )
        } catch (sendEx: IntentSender.SendIntentException) {
// Ignore the error.
        }
    }


    fun hideKeyboard(activity: Activity) {
        val imm =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getTrimmedText(editText: EditText): String? {
        return editText.text.toString().trim { it <= ' ' }
    }

    @JvmStatic
    fun showToast(context: Context, message: String) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {

        }

    }

    fun checkInterNetConnection(context: Context): Boolean {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show()
        return false
    }

    fun restartApp(activity: Activity) {
        val intent = Intent(activity, NovSplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

    fun setLanguage(mContext: Context, langCode: String?) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLayoutDirection(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale)
        } else {
            setSystemLocaleLegacy(config, locale)
        }
        mContext.resources
            .updateConfiguration(config, mContext.resources.displayMetrics)
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun setSystemLocale(
        config: Configuration,
        locale: Locale?
    ) {
        config.setLocale(locale)
    }

    fun setSystemLocaleLegacy(
        config: Configuration,
        locale: Locale?
    ) {
        config.locale = locale
    }

    fun getDateTime(timeStamp: Long): String {
        try {
            val sdf = SimpleDateFormat("dd MMM yy hh:mm aa")
            val netDate = Date(timeStamp * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    fun returnConsultationCategory(cc: String): String {
        when (cc) {
            "DIETITIAN" -> return "Dietitian"
            "PHYSIOTHERAPY" -> return "Physiotherapy"
            "GENETIC_COUNSELLING" -> return "Genetic counselling"
            "GENERAL" -> return "General"
            else ->
                return cc
        }
    }

    fun returnreturnConsultationCategoryEnum(cc: String): String {
        when (cc) {
            "Dietitian" -> return "DIETITIAN"
            "Physiotherapy" -> return "PHYSIOTHERAPY"
            "Genetic counselling" -> return "GENETIC_COUNSELLING"
            "General" -> return "GENERAL"
            else ->
                return cc
        }
    }

    fun returnGenderEnum(cc: String): String {
        when (cc) {
            "Male" -> return "MALE"
            "Female" -> return "FEMALE"
            "Not Specified" -> return "NOT_SPECIFIED"
            else ->
                return cc
        }
    }

    fun returnGenderName(cc: String): String {
        when (cc) {
            "MALE" -> return "Male"
            "FEMALE" -> return "Female"
            "NOT_SPECIFIED" -> return "Not Specified"
            else ->
                return cc
        }
    }

    fun returnRelationSHipEnum(cc: String): String {
        when (cc) {
            "Spouse" -> return "SPOUSE"
            "Son" -> return "SON"
            "Daughter" -> return "DAUGHTER"
            "Parent" -> return "PARENT"
            "Sibling" -> return "SIBLING"
            else ->
                return "OTHERS"
        }
    }

    fun returnRelationSHipName(cc: String): String {
        when (cc) {
            "SPOUSE" -> return "Spouse"
            "SON" -> return "Son"
            "DAUGHTER" -> return "Daughter"
            "PARENT" -> return "Parent"
            "SIBLING" -> return "Sibling"
            else ->
                return "Others"
        }
    }


    fun getDate(date: String): Date? {
        var dtStart = "2010-10-15T09:27:37Z"
        //dtStart = date.substring(0, date.length - 9)
        dtStart = date

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            val date: Date = format.parse(dtStart)
            System.out.println(date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
    }
    fun getAge(dateOfBirth: Date?): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance()
        var age = 0
        birthDate.time = dateOfBirth
        require(!birthDate.after(today)) { "Can't be born in the future" }
        age = today[Calendar.YEAR] - birthDate[Calendar.YEAR]

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if (birthDate[Calendar.DAY_OF_YEAR] - today[Calendar.DAY_OF_YEAR] > 3 ||
            birthDate[Calendar.MONTH] > today[Calendar.MONTH]
        ) {
            age--

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if (birthDate[Calendar.MONTH] === today[Calendar.MONTH] &&
            birthDate[Calendar.DAY_OF_MONTH] > today[Calendar.DAY_OF_MONTH]
        ) {
            age--
        }
        return age
    }

    fun getDateForAge(date: String): Date? {
        var dtStart = "2010-10-15T09:27:37Z"
        //dtStart = date.substring(0, date.length - 9)
        dtStart = date

        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            val date: Date = format.parse(dtStart)
            System.out.println(date)
            Log.d("patient_details","dob date "+date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.d("patient_details","dob date null")
            return null
        }
    }

    fun getDate1HourBack(date: String): Date? {
        var dtStart = "2010-10-15T09:27:37Z"
        //dtStart = date.substring(0, date.length - 9)
        dtStart = date

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        try {
            var date: Date = format.parse(dtStart)
            var HOUR = 3600*1000

            System.out.println(date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }
    }

    @JvmStatic
    fun getDateFromTimeStamp(timeStamp: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return format.format(Date(timeStamp))
    }

    // 21 june 2021
    fun getReportDateForPresciption(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    fun getDateForAppt(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.APPOINTMENT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.OUTPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    fun openWebBrowser(webpageUrl: String, context: Context) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(webpageUrl)
        context.startActivity(i)
    }

    fun openYoutubeVideo(activity: Activity, url: String) {
        activity.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }


    @JvmStatic
    fun addNextFragment(
        mActivity: AppCompatActivity, targetedFragment: Fragment,
        shooterFragment: Fragment?, isDownToUp: Boolean
    ) {
        val transaction =
            mActivity.supportFragmentManager.beginTransaction()
        if (isDownToUp) {
            transaction.setCustomAnimations(
                R.animator.slide_fragment_vertical_right_in,
                R.animator.slide_fragment_vertical_left_out,
                R.animator.slide_fragment_vertical_left_in,
                R.animator.slide_fragment_vertical_right_out
            )

            //FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(mActivity, transaction, shooterFragment, targetedFragment, R.id.activity_menubar_containers);
            //fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SLIDE_VERTICAL);
        } else {
            transaction.setCustomAnimations(
                R.animator.slide_fragment_horizontal_right_in,
                R.animator.slide_fragment_horizontal_left_out,
                R.animator.slide_fragment_horizontal_left_in,
                R.animator.slide_fragment_horizontal_right_out
            )

            //FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(mActivity, transaction, shooterFragment, targetedFragment, R.id.activity_menubar_containers);
            //fragmentTransactionExtended.addTransition(FragmentTransactionExtended.SLIDE_HORIZONTAL);
        }
        transaction.add(
            R.id.frameLayout,
            targetedFragment,
            targetedFragment.javaClass.simpleName
        )
        //curFragment = targetedFragment;
        transaction.hide(shooterFragment!!)
        transaction.addToBackStack(targetedFragment.javaClass.simpleName)
        transaction.commit()
    }

    @JvmStatic
    fun addNextFragment(
        mActivity: AppCompatActivity, targetedFragment: Fragment,
        shooterFragment: Fragment?, isDownToUp: Boolean, resultCode: Int
    ) {
        val transaction =
            mActivity.supportFragmentManager.beginTransaction()
        if (isDownToUp) {
            transaction.setCustomAnimations(
                R.animator.slide_fragment_vertical_right_in,
                R.animator.slide_fragment_vertical_left_out,
                R.animator.slide_fragment_vertical_left_in,
                R.animator.slide_fragment_vertical_right_out
            )
        } else {
            transaction.setCustomAnimations(
                R.animator.slide_fragment_horizontal_right_in,
                R.animator.slide_fragment_horizontal_left_out,
                R.animator.slide_fragment_horizontal_left_in,
                R.animator.slide_fragment_horizontal_right_out
            )
        }
        transaction.add(
            R.id.frameLayout,
            targetedFragment,
            targetedFragment.javaClass.simpleName
        )
        targetedFragment.setTargetFragment(shooterFragment, resultCode)
        //curFragment = targetedFragment;
        transaction.hide(shooterFragment!!)
        transaction.addToBackStack(targetedFragment.javaClass.simpleName)
        transaction.commit()
    }

    fun changeCOmmentDateTimeFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.COMMENT_DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    fun changeRecordDateFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.RECORD_OUTPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    fun changeTransactionDateFormat(inputTime: String): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.RECORD_OUTPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val mDateTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mDateTime)
        } catch (e: ParseException) {
            inputTime
        }
    }



     fun prepareMenuForIcons(popupMenu: PopupMenu) {
        try {
            val fields: Array<Field> = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.getName()) {
                    field.setAccessible(true)
                    val menuPopupHelper: Any = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons: Method = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun convertTimeSlotFormat1(inputTime: String?): String? {
        return try {
            val simpleTimeFormatInput = SimpleDateFormat(
                Constants.INPUT_DATE_FORMAT,
                Locale.getDefault()
            )
            val simpleTimeFormatOutput = SimpleDateFormat(
                Constants.DATE_FORMAT_OUTPUT,
                Locale.getDefault()
            )
            val mTime = simpleTimeFormatInput.parse(inputTime)
            simpleTimeFormatOutput.format(mTime)
        } catch (e: ParseException) {
            inputTime
        }
    }

    @JvmStatic
    fun shareApp(activity: Activity, message: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            message
        )
        sendIntent.type = "text/plain"
        activity.startActivity(sendIntent)
    }

    // Function to Get Data from the Plain text

    fun getDateFormat(word: String?): String {
        val formatStrings = Arrays.asList("yyyy-MM-dd","dd-MM-yyyy","dd/MMM/yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "dd MMM yy", "dd MMM yyyy","MMM dd, yyyy")
        var result = ""
        for (format in formatStrings) {
            val df = SimpleDateFormat(format)
            try {
                df.parse(word)
                result = format
                break
            } catch (e: ParseException) {
                //result = false
            }
        }
        return result

    }

    /**
     * Checks date of the report with all possible format
     * If date is parsed successfully, Result will be true and loop will not executed further, otherwise, It will check next format
     */
    fun isDate(word: String?): Boolean {
        val formatStrings = Arrays.asList("dd-MM-yyyy","dd/MMM/yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "dd MMM yy", "dd MMM yyyy","MMM dd, yyyy")
        var result = false
        for (format in formatStrings) {
            val df = SimpleDateFormat(format)
            try {
                df.parse(word)
                result = true
                Log.d("is_date","date "+word)
                break
            } catch (e: ParseException) {
                //result = false
            }
        }
        Log.d("is_date","result "+result)
        if(result){
            Log.d("is_date","date extracted "+word)
        }
        return result


        /*val df = SimpleDateFormat("dd-MM-yyyy")
        return try {
            df.parse(word)
            true
        } catch (e: ParseException) {
            val df = SimpleDateFormat("dd/MM/yyyy")
            return try {
                df.parse(word)
                true
            } catch (e: ParseException) {
                false
            }
            false
        }*/
    }


    fun convertDateToSaveRecords(inputDate: String?): String? {
        if (inputDate != null) {

            if (!getDateFormat(inputDate).isNullOrEmpty()) {
                val df = SimpleDateFormat(getDateFormat(inputDate))
                return try {
                    Log.d("date_log", "1 date format found " + getDateFormat(inputDate))
                    val originalDate = df.parse(inputDate)
                    val targetFormat = SimpleDateFormat("yyyy-MM-dd")
                    Log.d("date_log", "1 " + targetFormat.format(originalDate))
                    return targetFormat.format(originalDate)
                } catch (e: ParseException) {
                    return ""

                }
            } else {
                return ""
            }


        } else {
            return ""
        }
    }

    fun showImplementationPending(context: Context) {
        Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show()
    }

    fun getHospitalsFromAssets(): HospitalListingResponse? {
        //var string: String? = ""
        try {
            val inputStream: InputStream =
                FourBaseCareApp.activityFromApp.getAssets().open("hospital_list_obj.txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            //string = String(buffer)

            val gson = Gson()
            val hospitalListingResponse =
                gson.fromJson(String(buffer), HospitalListingResponse::class.java)
            Log.d("hospital_log", "Hospital list size " + hospitalListingResponse.payLoad.size)
            return hospitalListingResponse
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("hospital_log", "Err " + e.toString())
            return null
        }
    }

    fun getMainTagFromAssets(): MainTag? {
        //var string: String? = ""
        try {
            val inputStream: InputStream =
                FourBaseCareApp.activityFromApp.getAssets().open("main_tags.txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            val gson = Gson()
            val mainTag = gson.fromJson(String(buffer), MainTag::class.java)
            return mainTag
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("tag_log", "Err " + e.toString())
            return null
        }
    }

    fun getTagsFromAssets(): TagsResponse? {
        //var string: String? = ""
        try {
            val inputStream: InputStream =
                FourBaseCareApp.activityFromApp.getAssets().open("tags_list_obj.txt")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            //string = String(buffer)
            val gson = Gson()
            val tagsListingResponse = gson.fromJson(String(buffer), TagsResponse::class.java)
            return tagsListingResponse
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("tag_log", "Err " + e.toString())
            return null
        }
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    fun utcTOLocalDate(utcDate: String): String {
        val idf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH)
        idf.timeZone = TimeZone.getTimeZone("UTC")
        var date = idf.parse(utcDate)

        // simple conversion is not working. so, adding 1 day as of now
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, 1)
        date = c.time

        val odf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH)
        odf.timeZone = TimeZone.getDefault()
        return odf.format(date)

    }

    fun getTimeAgo(timeObj: Long): String? {
        Log.d("time_ago_log","Seconds "+timeObj)
        var time = timeObj
        if (time < 1000000000000L) {
            time *= 1000//TimeUnit.SECONDS.convert(timeObj * 1000,TimeUnit.MILLISECONDS)
            Log.d("time_ago_log","miliseconds "+time)

        }
        return if (time < Constants.MINUTE_MILLIS) {
            "just now"
        } else if (time < 2 * Constants.MINUTE_MILLIS) {
            "a minute ago"
        } else if (time < 50 * Constants.MINUTE_MILLIS) {
            Log.d("time_ago_log","minutes ago")
            val result = time / Constants.MINUTE_MILLIS
            "$result minutes ago"
        } else if (time < 90 * Constants.MINUTE_MILLIS) {
            "an hour ago"
        } else if (time < 24 * Constants.HOUR_MILLIS) {
            val result = time / Constants.HOUR_MILLIS
            "$result hours ago"
        } else if (time < 48 * Constants.HOUR_MILLIS) {
            "yesterday"
        } else {
            Log.d("time_ago_log","Days ago")
            val result = time / Constants.DAY_MILLIS
            "$result days ago"
        }
    }






    @JvmStatic
    fun generateSuperTagsSet() {

        var superTagsList = ArrayList<SuperTag>()

        // set name
        var superTag = SuperTag()
        superTag.name = "Discharge summery"

        // set alternative names
        var alternativeNames = ArrayList<String>()
        alternativeNames.add("Treatment summery")
        alternativeNames.add("Discharge Slip")
        //superTag.alternattiveNames = alternativeNames

        // set subtgs
        var subTags = ArrayList<com.oncobuddy.app.models.pojo.new_tags.Tag>()
        var tag = com.oncobuddy.app.models.pojo.new_tags.Tag()
        tag.name = "ChemoTherapy"
        var alternative = ArrayList<String>()
        alternative.add("Chemo 1")
        alternative.add("Chemo 2")
        tag.alternativeNames = alternative
        subTags.add(tag)

        var tagTwo = com.oncobuddy.app.models.pojo.new_tags.Tag()
        tagTwo.name = "Radiotherapy"
        subTags.add(tagTwo)

        // superTag.subTagsListing = subTags

        superTagsList.add(superTag)


        var mainTag = MainTag()
        mainTag.superTagList = superTagsList

        val gson = Gson()

        val strTagsResponse = gson.toJson(mainTag, MainTag::class.java)

        Log.d("generate_tags", strTagsResponse)


    }


}
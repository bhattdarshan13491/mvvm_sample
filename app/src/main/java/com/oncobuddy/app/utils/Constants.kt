package com.oncobuddy.app.utils

import com.twilio.video.OpusCodec
import com.twilio.video.Vp8Codec

/**
 * Constants
 * Global object containing all constant values  of the app
 * @constructor Create empty Constants
 */

object Constants {

    val APP_UPDATE_VERSION = 10

    var IS_ACC_SETUP_MODE = false

    var LOGOUT_NEEDED = false

    var IS_SELECTION_MODE = false

    @JvmField
    var CURRENT_LONGITUDE: Double = 0.0
    @JvmField
    var CURRENT_LATITUDE: Double = 0.0

    @JvmField
    val LBM_EVENT_LOCATION_UPDATE = "lbmLocationUpdate"

    @JvmField
    val INTENT_FILTER_LOCATION_UPDATE = "intentFilterLocationUpdate"

    var LOCATION_ADDRESS = ""
    var GPS_ENABLE = 15252

    //Db name
    const val DB_NAME = "FOUR_BASE_DB"
    const val FUNCTION_DELAY: Long = 500

    const val RING_WAITING_TIME = 30

    const val ANDROID = "ANDROID"

    const val PATIENT_ID = "PATIENT_ID"

    // Record types
    const val RECORD_TYPE = "RECORD_TYPE"
    const val RECORD_TYPE_REPORT = "REPORT"
    const val RECORD_TYPE_PRESCRIPTION = "PRESCRIPTION"
    const val RECORD_TYPE_BILL = "BILL"
    const val RECORD_TYPE_OTHERS = "OTHERS"

    var IS_LIST_UPDATED = false
    var IS_HOME_SCREEN_UPDATED = false

    //OTP purposes
    const val PURPOSE_FORGOT_PASSWORD = "FORGOT_PASSWORD"

    // Image processing
    const val PICK_GALLERY_IMAGE = 101
    const val REQUEST_EXTERNAL_STORAGE = 51
    const val REQUEST_CAMERA_AUDIO_STORAGE = 51

    const val FRAGMENT_RESULT = 110
    const val ACTIVITY_RESULT = 115
    const val REQ_USER_CONSENT = 51
    const val OS_R_STORAGE_PERMISSION = 2296
    const val FRAGMENT_SELECT_DOCTOR_RESULT = 111
    const val FRAGMENT_SELECT_VOUCHER_RESULT = 121
    const val QR_SCAN_RESULT = 112
    const val DOCTOR_DETAILS_RESULT = 113

    // Starting activity for results
    const val START_CAMERA = 1
    const val START_PDF = 2
    const val OPEN_PDF = 3

    //Shared prefs
    const val PREF_IS_FIRST_TIME = "is_first_time"
    const val PREF_USER_OBJ = "user_obj"
    const val PREF_AUTH_TOKEN = "auth_token"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_NAME = "user_name"
    const val PREF_FCM_TOKEN = "fcm_token"
    const val PREF_AWS_ACCESS_KEY = "aws_access_key"
    const val PREF_PATIENT_DETAILS_FOR_CG = "patient_details_for_cg"
    const val PREF_AWS_SECRET_KEY = "aws_secret_key"
    const val PREF_WALLET_BALANCE = "wallet_balance"
    const val IS_LOGGED_IN = "is_logged_in"

    const val VIDEO_LIST = "video_list"
    const val ONCO_CLUB = "onco_club"
    const val BLOG_LIST = "blog_list"
    const val QUESTION_LIST = "question_list"
    const val COMMUNITY_LIST = "community_list"
    const val EXPERT_QUESTIONS_LIST = "expert_questions_list"
    const val ONCO_DISCUSSIONS_LIST = "onco_discussions_list"
    const val QUESTION_OF_THE_WEEK = "question_of_the_week"
    const val ONCO_HUB = "onco_hub"

    const val PREF_CURRENT_LAT = "current_lat"
    const val PREF_CURRENT_CITY = "current_city"
    const val PREF_CURRENT_LONG = "current_long"

    const val QUESTION_TYPE_OPEN = "OPEN"
    const val QUESTION_TYPE_EXPERT = "EXPERT"
    const val QUESTION_TYPE_ONCO_DISCUSSIONS = "ONCO_DISCUSSION"



    // intent and bundles
    const val PRODUCT_ID = "product_id"
    const val RECORD_ID = "record_id"
    const val APPOINTMENT_ID = "appointment_id"
    const val FILTER_INPUT = "filter_input"
    const val TRANSACTION_ID = "transaction_id"
    const val PHONE_NUMBER = "phone_number"
    const val YOUTUBE_ID = "you_tube_id"
    const val YOUTUBE_URL = "you_tube_url"
    const val RECORD_OBJ = "record_obj"
    const val APPOINTMENT_OBJ = "appointment_obj"
    const val BLOG_OBJ = "blog_obj"
    const val VIDEO_OBJ = "video_obj"
    const val VIDEO_ID = "video_id"
    const val BLOG_ID = "blog_id"
    const val CHAT_GROUP_ID = "chat_group_id"
    const val CHAT_NAME = "chat_name"
    const val CHAT_URL = "chat_url"
    const val PDF_PATH = "pdf_path"
    const val PDF_URL = "pdf_url"
    const val SERVER_FILE_NAME = "server_file_name"
    const val SERVER_FILE_URL = "server_file_url"
    const val USER_ID = "user_id"
    const val PDF_CLASS_DATA = "pdf_class_data"
    const val SOURCE = "source"
    const val SHOULD_HIDE_BACK = "show_back"
    const val SHOULD_SHOW_TITLE = "show_title"
    const val APPOINTMENT_DETAILS = "appointment_details"
    const val DOCTOR_APPOINTMENTS_LIST = "doctor_appointments_list"
    const val CATEGORISED_VIDEOS_LIST = "categorised_video_list"
    const val DOCTOR_LIST = "doctor_list"
    const val VOUCHER_LIST = "voucher_list"
    const val DOCTOR_DATA = "DOCTOR_DATA"
    const val PATIENT_DATA = "PATIENT_DATA"
    const val VOUCHER_DATA = "VOUCHER_DATA"
    const val EDIT_RECORD_FRAGMENT = "edit_record_fragment"
    const val VIDEO_BY_DOCTOR = "video_by_doctor"
    const val ADD_APPOINTMENT_FRAGMENT = "add_appointment_fragment"
    const val RECORD_LISTING_FRAGMENT = "record_list_fragment"
    const val CAMERA_OR_GALLERY_FRAGMENT = "camera_or_gallery_fragment"
    const val SCAN_FRAGMENT = "scan_fragment"
    //const val IS_EDIT_MODE = "is_edit_mode"

    const val DID_YOU_KNOW = "DID_YOU_KNOW"
    const val TIPS = "TIPS"
    const val THOUGHT_OF_THE_DAY = "THOUGHT_OF_THE_DAY"


    //time formats
    const val DATE_FORMAT: String = "yyyy-MM-dd"
    const val DD_MM_YYYY_FORMAT: String = "dd-MM-yyyy"
    const val Y_M_FORMAT: String = "MMMM yyyy"
    const val DATE_FORMAT1 = "dd-MM-yyyy"
    const val INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss" //"yyyy-MM-dd'T'HH:mm:ss"
    const val RECORD_INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.000+0000" //"yyyy-MM-dd'T'HH:mm:ss"
    //2021-05-31T12:00:00
    const val APPOINTMENT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss" //"yyyy-MM-dd'T'HH:mm:ss"


    const val RECORD_OUTPUT_DATE_FORMAT = "dd MMMM yyyy" //"yyyy-MM-dd'T'HH:mm:ss"
    const val OUTPUT_DATE_FORMAT = "dd MMMM yyyy - hh:mm aa"
    const val COMMENT_DATE_FORMAT = "dd MMM yy hh:mm aa"

    const val DATE_FORMAT_OUTPUT = "dd MMM yyyy"
    const val INPUT_TIME_SLOT_FORMAT = "HH:mm"
    const val INPUT_TIME_FORMAT = "HH:mm:ss"
    const val TIME_FORMAT_OUTPUT = "h:mm a"
    const val TIME_AMPM_FORMAT = "hh:mm aa"

    // file paths
    const val PDF_FILES = "/PDFfiles/"

    //Appointment Status
    const val SCHEDULED = "SCHEDULED"
    const val STARTED = "STARTED"
    const val COMPLETED = "COMPLETED"
    const val CANCELLED = "CANCELLED"
    const val POSTPONED = "POSTPONED"


    var STR_BLANK = ""
    var STR_NULL = "null"
    var IS_FROM_ADD_APPOINTMENT = false
    var IS_FROM_HOME_SCREEN = false
    var IS_FROM_CONSULTATION_SCREEN = false
    var IS_FROM_PATIENT_LIST_SCREEN = false
    var PATIENT_NUMBERS = 0


    var PATIENT_ID_FOR_RECORDS = "0"

    @JvmField
    var IS_DOCTOR_LOGGED_IN = false
    const val PG_NUM_STYLE_PAGE_X_OF_N = "pg_num_style_page_x_of_n"
    const val PG_NUM_STYLE_X_OF_N = "pg_num_style_x_of_n"
    const val AUTH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5MDEwNTkwMDU5Iiwicm9sZSI6IlNVUEVSX0FETUlOIiwiaWF0IjoxNjEyMzI4MjU3LCJleHAiOjE2MTM2MjQyNTd9.mGbqmRJ6kYNK9wvi2oMJoYlyjixXfTYmbJ7p27DCgcQ"


    //video calling
    const val PREF_AUDIO_CODEC =  "audio_codec"
    const val PREF_AUDIO_CODEC_DEFAULT = OpusCodec.NAME
    const val PREF_VIDEO_CODEC = "video_codec"
    const val PREF_VIDEO_CODEC_DEFAULT = Vp8Codec.NAME
    const val PREF_SENDER_MAX_AUDIO_BITRATE = "sender_max_audio_bitrate"
    const val PREF_SENDER_MAX_AUDIO_BITRATE_DEFAULT = "0"
    const val PREF_SENDER_MAX_VIDEO_BITRATE = "sender_max_video_bitrate"
    const val PREF_SENDER_MAX_VIDEO_BITRATE_DEFAULT = "0"
    const val PREF_VP8_SIMULCAST = "vp8_simulcast"
    const val PREF_VP8_SIMULCAST_DEFAULT = false

    const val contentType = "Content-Type"
    const val authorization = "Authorization"

    const val ROLE_DOCTOR = "ROLE_DOCTOR"
    const val ROLE_NEW_USER = "ROLE_NEW_USER"
    const val USER_ROLE = "user_role"
    const val USER_MOBILE_NUM = "mobile_num"
    const val ROLE_PATIENT = "ROLE_PATIENT"
    const val ROLE_CARE_COMPANION = "ROLE_CARE_COMPANION"
    const val GENETIC_REPORT = "GENETIC_REPORT"
    const val SUMMARY = "SUMMARY"
    const val ROLE_PATIENT_CARE_GIVER ="ROLE_PATIENT_CARE_GIVER"
    const val FCM_DATA_KEY = "fcm_data_key"
    const val CALL_RESPONSE_ACTION_KEY = "call_response_action_key"
    const val CALL_RECEIVE_ACTION = "call_receive_action"
    const val CALL_CANCEL_ACTION = "call_cancel_action"


    const val PREF_HAS_READ_APPOINTMENT_MSG = "has_read_appt_msg"
    const val PREF_HAS_READ_REPORT_MSG = "has_read_report_msg"

    const val PREF_REMEMBER_ON = "pref_remember_on"
    const val PREF_REMEMBER_EMAIL = "pref_remember_email"
    const val PREF_REMEMBER_PWD = "pref_remember_pwd"



    // Video call constants
    const val ACTION_VIDEO_NOTIFICATION = "VIDEO_NOTIFICATION"
    const val VIDEO_NOTIFICATION_TITLE = "VIDEO_NOTIFICATION_TITLE"
    const val VIDEO_NOTIFICATION_ROOM_NAME = "VIDEO_NOTIFICATION_ROOM_NAME"

    // Mixpanel constants
    const val EVENT_SCREEN_VIEW = "SCREEN_VIEWED"
    const val PROPERTY_SCREEN_NAME = "SCREEN_NAME"

    const val PRIVACY_POLICY_URL = "https://4basecare.com/privacy-policy/"
    const val TERMS_URL = "https://4basecare.com/terms-of-use/"

    const val IMAGE_URL = "image_url"

    // upload pdf doc
    const val DOC_MODE = "doc_mode"
    const val PDF_MODE = 0
    const val PHOTO_MODE = 1
    const val IMAGE_PATH = "image_path"

    const val DOCTOR_ID = "doctor_id"

    const val ACTION_ADD = 0
    const val ACTION_UPDATE = 1
    const val ACTION_DELETE = 2

    const val OWN_CLINIC = "OWN_CLINIC"
    const val VISIT_HOSPITAL = "VISIT_HOSPITAL"

    const val COMMUNITY = "COMMUNITY"
    const val ASK_EXPERT = "ASK_EXPERT"


    //permissions
    const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123

    const val SECOND_MILLIS = 1000
    const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    const val DAY_MILLIS = 24 * HOUR_MILLIS

    const val ACCOUNT_TYPE_BANK = "BANK_ACCOUNT"
    const val ACCOUNT_TYPE_UPI = "UPI"

    const val ATTACHEMENT_TYPE_DOCUMENT = "DOCUMENT"
    const val ATTACHEMENT_TYPE_IMAGE = "IMAGE"

    const val PATIENT_TYPE_TEST_TAKER = "TEST_TAKER"
    const val PATIENT_TYPE_NON_TEST_TAKER = "NON_TEST_TAKER"




}
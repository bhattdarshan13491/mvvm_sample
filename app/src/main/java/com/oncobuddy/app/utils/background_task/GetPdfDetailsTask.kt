package com.oncobuddy.app.utils.background_task

import android.util.Log
import com.oncobuddy.app.models.pojo.PDFExtractedData
import com.oncobuddy.app.models.pojo.tags_response.Tag
import com.oncobuddy.app.utils.CommonMethods
import java.util.*
import kotlin.collections.ArrayList

open class GetPdfDetailsTask(private val extractedText: String,
                             private val getPdfDetailsInterface: GetPdfDetailsInterface) : BaseTask<Any?>() {
    @Throws(Exception::class)
    override fun call(): Any {
        return extractText()
    }

    override fun setUiForLoading() {
        //listener.showProgressBar();
        Log.d("super_tag_log", "Pre execute")
    }

    override fun setDataAfterLoading(result: Any?) {
        /*listener.setDataInPageWithResult(result);
        listener.hideProgressBar();*/
        Log.d("super_tag_log", "post execute")
        getPdfDetailsInterface.onPdfObjectCreated(result as PDFExtractedData)
    }

    interface GetPdfDetailsInterface {
        fun onPdfObjectCreated(pdfExtractedData: PDFExtractedData?)
    }

    private fun extractText(): PDFExtractedData {
        Log.d("super_tag_log", "in background")
        try {
            Log.d("super_tag_log", "started extraction")
            Log.d("super_tag_log", "extracted text "+extractedText)
            var pdfExtractedData = PDFExtractedData()

            val myList: List<String> = java.util.ArrayList(
                Arrays.asList(
                    *extractedText!!.split(" ".toRegex()).toTypedArray()
                )
            )

            var isNameSet = false
            var matchedTags = ArrayList<Tag>()

            Log.d("super_tag_log", "Total words "+myList.size)
            for (i in myList.indices) {

                val word = myList[i]
                //Log.d("super_tag_log", "processing word "+word)
                if (!word.isNullOrBlank() && !word.equals(
                        "null",
                        true
                    )
                )

                {
                    if (word.length > 3 && !isNameSet) {
                        pdfExtractedData.reportName = word + " " + myList[i + 1]
                        isNameSet = true

                    }


                    if (pdfExtractedData.getConsulationDate() == null && CommonMethods.isDate(word)) {
                        pdfExtractedData.setConsulationDate(word)
                        Log.d("etxract_date_log", "date $word")
                    }
                    if (pdfExtractedData.getReportType() == null && (word.equals(
                            "report",
                            ignoreCase = true
                        )
                                || word.equals("Prescription", ignoreCase = true)
                                || word.equals("bill", ignoreCase = true))
                    ) {
                        pdfExtractedData.setReportType(word)
                        Log.d("super_tag_log", "type $word")
                    }
                    if (pdfExtractedData.getHospitalName() == null && (word.contains(
                            "hospital",
                            ignoreCase = true
                        ) || word.contains("lab", ignoreCase = true) || word.contains(
                            "clinic",
                            ignoreCase = true
                        ))
                    ) {
                        var hospitalName = myList[i - 1]+" "+word
                        pdfExtractedData.setHospitalName(hospitalName)
                        Log.d("hospital_log", "hospital name set " + hospitalName)
                    }
                    // Adding Tags //

                    //Search for a sub-tag
                    //If you find a tag, add that and its parent to the tags list
                    //If no sub-tag is found, then only search for a primary tag


                    // If you find any word ending with ectomy, consider that as surgery

                    /*if(word.length > 8){
                        var subWord = word.substring(word.length-6)
                        if(subWord.equals("ectomy")){
                            var isAvailable = checkIfALreadyAdded(matchedTags, "Surgery")
                            if(!isAvailable){
                                matchedTags.add(Tag(21, "Surgery"))
                            }else{
                                Log.d("super_tag_log", "Already added surgery tag "+word)
                            }
                        }
                    }*/




        /*            val tagsResponse = CommonMethods.getMainTagFromAssets()

                    val tagsList = tagsResponse?.superTagList

                    var prtTags = ""
                    var i = 0
                    if (tagsList != null) {

                        for (superTag in tagsList) {

                            if (superTag.name.equals(word, true)) {
                                Log.d("super_tag_log", "Matched main tag "+word+" with "+superTag.name)

                                var isAvailable = checkIfALreadyAdded(matchedTags, word)

                                if(!isAvailable){
                                    matchedTags.add(Tag(superTag.id, superTag.name))
                                    Log.d("super_tag_log", "Adding MainTag "+word)
                                }else{
                                    Log.d("super_tag_log", "ALready added Main tag "+word)
                                }
                            }
                            else{
                                if(!superTag.alternattiveNames.isNullOrEmpty()){
                                    for(str in superTag.alternattiveNames){
                                        if(str.name.equals(word,true)){
                                            Log.d("super_tag_log", "Matched main alternative "+word+" with "+str.name)
                                            var isAvailable = checkIfALreadyAdded(matchedTags, superTag.name)
                                            if(!isAvailable){
                                                matchedTags.add(Tag(superTag.id, superTag.name))
                                                // If alternative name matched, It will add main tag name
                                                Log.d("super_tag_log", "Adding Main tag alternative "+superTag.name)
                                            }else{
                                                Log.d("super_tag_log", "ALready added  main tag alternative "+superTag.name)
                                            }


                                        }
                                    }
                                }
                            }
                            // Searching in sub tags
                            if(!superTag.subTagsListing.isNullOrEmpty()){
                                for(subTag in superTag.subTagsListing){
                                    if (subTag.name.contains(word, true)) {
                                        for(subTag in superTag.subTagsListing){
                                            if(subTag.name.equals(word, true)){
                                                // Adding sub tag
                                                Log.d("super_tag_log", "Matched subtag "+word+" with "+subTag.name)
                                                var isAvailable = checkIfALreadyAdded(matchedTags, subTag.name)
                                                if(!isAvailable){
                                                    matchedTags.add(Tag(subTag.id, subTag.name))
                                                    Log.d("super_tag_log", "Adding subtag "+subTag.name)
                                                }else{
                                                    Log.d("super_tag_log", "ALready added subtag "+subTag.name)
                                                }

                                                // Adding super tag
                                                if(!superTag.name.equals("Others",true)){
                                                    var isAvailableSUperTag = checkIfALreadyAdded(matchedTags, superTag.name)
                                                    if(!isAvailableSUperTag){
                                                        matchedTags.add(Tag(superTag.id, superTag.name))
                                                        Log.d("super_tag_log", "Adding MainTag with subtag "+superTag.name)
                                                    }else{
                                                        Log.d("super_tag_log", "Already Added MainTag with subtag "+superTag.name)
                                                    }
                                                }

                                            }
                                        }

                                    }else{
                                        // Search tag alternatives
                                        if(!subTag.alternattiveNames.isNullOrEmpty()){
                                            for(str in subTag.alternattiveNames){
                                                if(str.name.equals(word, true)){
                                                    Log.d("super_tag_log", "Matched subtag alternative "+word+" with "+str.name)
                                                    var isAvailable = checkIfALreadyAdded(matchedTags, subTag.name)

                                                    if(!isAvailable){
                                                        matchedTags.add(Tag(subTag.id, subTag.name))
                                                        Log.d("super_tag_log", "Adding Sub tag "+subTag.name)
                                                    }else{
                                                        Log.d("super_tag_log", "ALready Added Sub tag "+subTag.name)
                                                    }



                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    } else{
                        Log.d("super_tag_log","tag list null")
                    }
                    Log.d("prt_tags_log", prtTags)*/

                }
            }
            pdfExtractedData.tags.addAll(matchedTags)

            if (!pdfExtractedData.tags.isNullOrEmpty())
                Log.d(
                    "super_tag_log",
                    "Added " + pdfExtractedData.tags.size
                );
            else Log.d("super_tag_log", "Nothing added")
            Log.d("super_tag_log", "Extraction completed")

            return pdfExtractedData

        } catch (e: Exception) {
            Log.d("super_tag_log", "Error "+e.toString())

            e.printStackTrace()
            return PDFExtractedData()
        }
    }

    private fun checkIfALreadyAdded(
        matchedTags: ArrayList<Tag>,
        word: String
    ): Boolean {
        var isAvailable = false
        for (tag in matchedTags) {
            if (tag.name.equals(word)) {
                isAvailable = true
            }
        }
        return isAvailable
    }
}
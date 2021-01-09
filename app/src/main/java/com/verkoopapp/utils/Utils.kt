package com.verkoopapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.flashbar.Flashbar
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Utils {
    private const val GIF_PATTERN = "(.+?)\\.gif$"
    fun savePreferencesString(context: Context, key: String, value: String): String {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.commit()

        return key
    }

    fun getPreferences(context: Context, key: String): String {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "").toString()

    }

    fun removePreferences(context: Activity, key: String) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove(key)

    }

    fun getPreferencesBoolean(context: Activity, key: String): Boolean {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean(key, false)

    }
    fun getPreferencesInt(context: Activity, key: String): Int {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getInt(key, 0)

    }

    fun getPreferencesLong(context: Activity, key: String): Long {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getLong(key, 0L)

    }

    fun getPreferencesString(context: Context, key: String): String {
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        return sharedPreferences.getString(key, "").toString()
    }

    fun saveIntPreferences(context: Context, key: String, value: Int) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun saveLongPreferences(context: Context, key: String, value: Long) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    fun saveFloatPreferences(context: Context, key: String, value: Float) {

        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putFloat(key, value)
        editor.commit()
    }


    fun emailValidator(mailAddress: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(mailAddress)
        return matcher.matches()
    }

    fun isValidPassword(str: String): Boolean {
        return if (str.contains(" ")) {
            false
        } else !str.isEmpty()
    }

    fun showSimpleMessage(context: Context, text: String): Flashbar {
        return Flashbar.Builder(context as AppCompatActivity)
                .gravity(Flashbar.Gravity.TOP)
                .messageSizeInSp(16f)
                //.titleTypeface(Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Regular.ttf"))
                .message(text)
                .backgroundColorRes(R.color.colorPrimary)
                .dismissOnTapOutside()
                .duration(5000)
                .build()
    }

    fun showSimpleMessageShort(context: Context, text: String): Flashbar {
        return Flashbar.Builder(context as AppCompatActivity)
                .gravity(Flashbar.Gravity.TOP)
                .messageSizeInSp(16f)
                //.titleTypeface(Typeface.createFromAsset(context.assets, "fonts/SourceSansPro-Regular.ttf"))
                .message(text)
                .backgroundColorRes(R.color.colorPrimary)
                .dismissOnTapOutside()
                .duration(2000)
                .build()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun isOnline(context: Context): Boolean {
        val conMgr = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = conMgr.activeNetworkInfo

        return !(netInfo == null || !netInfo.isConnected || !netInfo.isAvailable)
    }

    fun clearPreferences(context: Context) {
        val sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.commit()
    }

    fun checkGif(path: String): Boolean {
        return path.matches(GIF_PATTERN.toRegex())
    }

    fun getRealPathFromURI(context: Context, contentURI: Uri): String {
        val result: String
        val cursor = context.contentResolver.query(contentURI, null, null, null, null)
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.path.toString()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    fun scaleDown(realImage: Bitmap, maxImageSize: Float,
                  filter: Boolean): Bitmap {
        val ratio = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height)
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter)
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    @SuppressLint("Recycle")
    fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDate(pre: String, dateString: String, post: String): String {
        val parseFormat = SimpleDateFormat(pre)
        parseFormat.timeZone = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        try {
            date = parseFormat.parse(dateString)
            parseFormat.timeZone = TimeZone.getDefault()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val format = SimpleDateFormat(post)
        format.timeZone = TimeZone.getDefault()
        // Log.e("dateTimeStamp",date)

        return format.format(date)

    }

    @SuppressLint("SimpleDateFormat")
    fun dateDifference(pre: String, dateString: String,daysNo:Int): String {
        val format = SimpleDateFormat("MMMM dd, yyyy")
        val c = Calendar.getInstance()
        try {
            c.time = format.parse(dateString)
        } catch (e: Exception) {
            Log.e("exception","exceptionDone")
        }
	c.add(Calendar.DAY_OF_MONTH, daysNo)
	val newDate = format.format(c.time)
	System.out.println("Date after Addition: "+newDate)
        return newDate

    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDifference(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
            // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.ssssss")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val year = diff / (24 * 60 * 60 * 24 * 365 * 1000L)
            diff -= year * (24 * 60 * 60 * 24 * 365 * 1000L)

            val month = diff / (24 * 60 * 60 * 30 * 1000L)
            diff -= month * (24 * 60 * 60 * 30 * 1000L)

            val week = diff / (24 * 60 * 60 * 7 * 1000)
            diff -= week * (24 * 60 * 60 * 7 * 1000)

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000

            if (year != 0L) {
                totalTime = year.toInt()
                if (totalTime == 1) {
                    unit = "Year"
                } else {
                    unit = "Years"
                }
                // unit = "day"
            } else if (month != 0L) {
                totalTime = month.toInt()
                if (totalTime == 1) {
                    unit = "Month"
                } else {
                    unit = "Months"
                }
                // unit = "day"
            } else if (week != 0L) {
                totalTime = week.toInt()
                if (totalTime == 1) {
                    unit = "Week"
                } else {
                    unit = "Weeks"
                }
                // unit = "day"
            } else if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "Day"
                } else {
                    unit = "Days"
                }
                // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "Hour"
                } else {
                    unit = "Hours"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "Min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "Sec"

            } else {
                totalTime = 1
                unit = "Sec"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalTime.toString() + " " + unit
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateDifferenceDiff(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
            // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val year = diff / (24 * 60 * 60 * 24 * 365 * 1000L)
            diff -= year * (24 * 60 * 60 * 24 * 365 * 1000L)

            val month = diff / (24 * 60 * 60 * 30 * 1000L)
            diff -= month * (24 * 60 * 60 * 30 * 1000L)

            val week = diff / (24 * 60 * 60 * 7 * 1000)
            diff -= week * (24 * 60 * 60 * 7 * 1000)

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000


            if (year != 0L) {
                totalTime = year.toInt()
                if (totalTime == 1) {
                    unit = "year"
                } else {
                    unit = "years"
                }
                // unit = "day"
            } else if (month != 0L) {
                totalTime = month.toInt()
                if (totalTime == 1) {
                    unit = "month"
                } else {
                    unit = "months"
                }
                // unit = "day"
            } else if (week != 0L) {
                totalTime = week.toInt()
                if (totalTime == 1) {
                    unit = "week"
                } else {
                    unit = "weeks"
                }
                // unit = "day"
            } else if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "day"
                } else {
                    unit = "days"
                }
                // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "hour"
                } else {
                    unit = "hours"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "sec"

            } else {
                totalTime = 1
                unit = "sec"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalTime.toString() + " " + unit
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateDifferenceDetails(FutureDate: String): String {
        var totalTime = 0
        var unit: String? = null
        try {
            // val netDate = java.sql.Date(java.lang.Long.parseLong(FutureDate) * 1000)
            val parseFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            parseFormat.timeZone = TimeZone.getTimeZone("UTC")
            var date: Date? = null
            try {
                date = parseFormat.parse(FutureDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            val oldDate = Date()

            var diff = oldDate.time - date!!.time

            val days = diff / (24 * 60 * 60 * 1000)
            diff -= days * (24 * 60 * 60 * 1000)

            val hours = diff / (60 * 60 * 1000)
            diff -= hours * (60 * 60 * 1000)

            val minutes = diff / (60 * 1000)
            diff -= minutes * (60 * 1000)

            val seconds = diff / 1000

            if (days != 0L) {
                totalTime = days.toInt()
                if (totalTime == 1) {
                    unit = "day"
                } else {
                    unit = "days"
                }
                // unit = "day"
            } else if (hours != 0L) {
                totalTime = hours.toInt()
                if (totalTime == 1) {
                    unit = "hour"
                } else {
                    unit = "hours"
                }
            } else if (minutes != 0L) {
                totalTime = minutes.toInt()
                unit = "min"
            } else if (seconds != 0L) {
                totalTime = seconds.toInt()
                unit = "sec"

            } else {
                totalTime = 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return totalTime.toString() + " " + unit
    }

    fun saveTempBitmap(context: Context, mBitmap: Bitmap): String? {

        val outputDir = context.cacheDir

        var file: File? = null
        try {
            file = File.createTempFile("temp_post_img", ".jpg", outputDir)
            //outputFile.getAbsolutePath();
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val f3 = File(Environment.getExternalStorageDirectory().toString() + "/inpaint/")
        if (!f3.exists()) {
            f3.mkdirs()
        }
        val outStream: OutputStream?
        //File file = new File(Environment.getExternalStorageDirectory() + "/inpaint/"+"seconds"+".png");
        try {
            outStream = FileOutputStream(file!!)
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream.flush()
            outStream.close()

            //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        //getPath( Uri.parse(file.getAbsolutePath()), context);

        return file.absolutePath//getPath( Uri.parse(file.getAbsolutePath()), context);
    }

    fun hideKeyboardOnOutSideTouch(view: View, activity: Activity) {
        if (view !is EditText) {
            view.setOnTouchListener({ v, event ->
                hideKeyboard(activity)
                false
            })
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyboardOnOutSideTouch(innerView, activity)
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused com.blockWorkout.view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no com.blockWorkout.view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setDatePicker(context: Context, currentDate: CurrentDate) {
        val myCalendar = Calendar.getInstance()
        val date = { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            var month = (monthOfYear + 1).toString()
            var day = dayOfMonth.toString()
            if (monthOfYear < 10) {
                month = "0$month"
            }
            if (dayOfMonth < 10) {
                day = "0$day"
            }
            currentDate.getSelectedDate(year.toString() + "-" + month + "-" + day)
        }
        val picker = DatePickerDialog(context, R.style.datepicker,
                date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
        picker.datePicker.maxDate = myCalendar.time.time
        picker.show()
    }

    interface CurrentDate {
        fun getSelectedDate(date: String)
    }

    @Throws(Exception::class)
    public fun createTemporaryFile(part: String, ext: String): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.absolutePath + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }

    fun dpToPx(context: Context, valueInDp: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
    }

    fun setDate(date: String): String {
        val dateNow = Date()
        val calNow = GregorianCalendar.getInstance()
        calNow.time = dateNow
        calNow.set(Calendar.HOUR_OF_DAY, 0)
        calNow.set(Calendar.MINUTE, 0)
        calNow.set(Calendar.SECOND, 0)
        val timeFormat: SimpleDateFormat
        val dateServ = Date(java.lang.Long.parseLong(date))

        if (dateServ.after(calNow.time)) {
            timeFormat = SimpleDateFormat("hh:mm aa")

        } else {
            calNow.add(Calendar.DATE, -1)
            if (dateServ.after(calNow.time)) {
                return "YESTERDAY"
            } else {
                timeFormat = SimpleDateFormat("dd/MM/yy")
            }
        }
        return timeFormat.format(dateServ)
    }

     fun appInstalledOrNot(context:Context,uri: String): Boolean {
        val pm = context.packageManager
        val appInstalled: Boolean
        appInstalled = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }

    fun convertCurrency(context: Context, from: String, amount: Double): String? {
        Log.e("TAG", "convertCurrency: "+from)
        Log.e("TAG", "convertCurrency: "+amount)
        Log.e("TAG", "convertCurrency: ")
        if (from.isNullOrEmpty()) {
            return "${getPreferencesString(context, AppConstants.CURRENCY_SYMBOL)} --"
        }

        if (from.equals(getPreferencesString(context, AppConstants.CURRENCY), true)) {
            return "${getPreferencesString(context, AppConstants.CURRENCY_SYMBOL)} $amount"
        }

        val fromRate = VerkoopApplication.instance.currencies.first { it.code == from }
        val toRate = VerkoopApplication.instance.currencies.first { it.code == getPreferencesString(context, AppConstants.CURRENCY) }
        val convert = toRate.rate.toDouble() / fromRate.rate.toDouble()
        val output = convert * amount
        return "${getPreferencesString(context, AppConstants.CURRENCY_SYMBOL)} ${String.format("%.2f", output)}"
    }

    fun convertCurrencyToUsd(context: Context, from: String, amount: Double): String? {
        try {


            val fromRate = VerkoopApplication.instance.currencies.first {
                it.code == from
            }
            val toRate = VerkoopApplication.instance.currencies.first { it.code == "USD" }

            val convert = toRate.rate.toDouble() / fromRate.rate.toDouble()

            val output = convert * amount


            return "${String.format("%.2f", output)}"
        }
        catch (ex:Exception){
            Log.e("TAG", "convertCurrencyToUsdex: "+ex.message)
        }
        return null
    }

    fun convertCurrencyWithoutSymbol(context: Context, from: String, amount: Double): Double {
        if (from.isNullOrEmpty()) {
            return amount
        }
        if (from.equals(getPreferencesString(context, AppConstants.CURRENCY), true)) {
            return amount
        }
        val fromRate = VerkoopApplication.instance.currencies.first { it.code == from }
        val toRate = VerkoopApplication.instance.currencies.first { it.code == getPreferencesString(context, AppConstants.CURRENCY) }
        val convert = toRate.rate.toDouble() / fromRate.rate.toDouble()
        val output = convert * amount
        return String.format("%.2f", output).toDouble()
    }



}
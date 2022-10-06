package com.enflash.mobile.storeapp.utils


import android.os.Environment
import androidx.annotation.Keep
import com.enflash.mobile.storeapp.application.App
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Keep
object FileLog {

    fun writeToConsole(message: String) {

        var mExternalStorageAvailable: Boolean
        var mExternalStorageWriteable: Boolean
        var state = Environment.getExternalStorageState()

        when (state) {
            Environment.MEDIA_MOUNTED -> {
                mExternalStorageWriteable = true
                mExternalStorageAvailable = mExternalStorageWriteable
            }
            Environment.MEDIA_MOUNTED_READ_ONLY -> {
                mExternalStorageAvailable = true
                mExternalStorageWriteable = false
            }
            else -> {
                mExternalStorageWriteable = false
                mExternalStorageAvailable = mExternalStorageWriteable
            }
        }
        val date = Date()
        var dateFormat = android.text.format.DateFormat.format("yyyy-MM-dd", date) as String
        var dateFormatdata = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", date) as String
        var newmessage = "${message}\n"

        if (mExternalStorageAvailable && mExternalStorageWriteable) {

            val root = App.getAppInstance().getExternalFilesDir(null)

            val dir = File(root!!.absolutePath + "/" + Constants.APPLICATION_NAME)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            getDirectories(dir.absolutePath)

            val file = File(dir, "$dateFormat-log.txt")
            PreferencesManager.setPath(file.absolutePath)
            try {
                val stream = FileOutputStream(file, true)
                val bw = BufferedWriter(OutputStreamWriter(stream))
                bw.write("${Constants.VERSION_NAME} | $dateFormatdata:$newmessage")
                bw.newLine()
                bw.close()
                stream.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            try {
                val path = App.getAppInstance()!!.getExternalFilesDir(null)

                getDirectories(path!!.absolutePath)

                val file = File(path, "$dateFormat-log.txt")
                PreferencesManager.setPath(file.absolutePath)
                val stream = FileOutputStream(file, true)
                val bw = BufferedWriter(OutputStreamWriter(stream))
                bw.write("${Constants.VERSION_NAME} | $dateFormatdata:$newmessage")
                bw.newLine()
                bw.close()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getDirectories(path: String) {
        val directory = File(path)
        val files = directory.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (i in files.indices) {
                try {
                    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val today = Date()
                    val dia = df.parse(files[i].name.split("-log")[0])
                    val diff = today.time - dia!!.time
                    val days = diff / (1000 * 60 * 60 * 24)
                    if (days >= 2) {
                        deleteRecursive(files[i])
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                deleteRecursive(child)

        fileOrDirectory.delete()
    }

}
package digitec.arsen.digitecproject.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.storage.FirebaseStorage
import digitec.arsen.digitecproject.R
import digitec.arsen.digitecproject.adater.FileAdapter
import digitec.arsen.digitecproject.model.DataModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileOutputStream
import java.nio.file.Path
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private val storage = FirebaseStorage.getInstance()
    private val data = ArrayList<DataModel>()
    private lateinit var adapter: FileAdapter
    private lateinit var layout: View
    private val GET_FROM_GALARY = 77


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        filesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FileAdapter(this, data)
        filesRecyclerView.adapter = adapter
        layout = findViewById(R.id.main_layout)


        getFilesButton.setOnClickListener {
            checkPermission()
        }

        uploadFilesButton.setOnClickListener {
            uploadToDB(data)
        }

    }

    private fun checkPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE), GET_FROM_GALARY)
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            GET_FROM_GALARY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    checkPermission()
                }
                return
            }

            else -> {

            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/* video/*"
        startActivityForResult(intent, GET_FROM_GALARY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_FROM_GALARY && resultCode == Activity.RESULT_OK && data != null) {
            val selectedFile = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Video.Media.DATA)

            val cursor = contentResolver.query(selectedFile, filePathColumn, null, null, null)
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            var filePath = cursor.getString(columnIndex)
            val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
            val fileType = filePath.substring(filePath.lastIndexOf(".") + 1)
            val file = File(filePath)
            var fileSize = file.length() / 1024
            if ((fileType == "jpg") || (fileType == "png") && fileSize > 1536) {
                do {
                    val bitmap = BitmapFactory.decodeFile(filePath)
                    val newSize = (bitmap.height * (512.0 / bitmap.width)).toInt()
                    val bitmapScaled = Bitmap.createScaledBitmap(bitmap, 512, newSize, true)
                    val outFileDest = File(Environment.getExternalStorageDirectory().absolutePath + "/Test")
                    outFileDest.mkdirs()
                    val outFile = File(outFileDest.absolutePath, System.currentTimeMillis().toString()  + "." + fileType)
                    val out = FileOutputStream(outFile)
                    bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, out)
                    filePath = outFile.absolutePath
                    fileSize = outFile.length() / 1024
                } while (fileSize > 1536)
            }
            cursor.close()
            val dataList = ArrayList<DataModel>()
            dataList.add(DataModel(filePath, fileName, fileSize, fileType))
            adapter.updateData(dataList)

        }
    }

    private fun compressVideo() {
        //            val videoResolutions = MediaMetadataRetriever()
//            videoResolutions.setDataSource(filePath)
//            val height = videoResolutions.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT).toInt()
//            val width = videoResolutions.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH).toInt()
//            if ((fileType == "avi" || fileType == "flv" || fileType == "mp4") && (width>1280)||(height>720)) {
//
//                FFmpeg.getInstance(this).loadBinary(object : FFmpegLoadBinaryResponseHandler {
//                    override fun onFinish() {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onFailure() {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onSuccess() {
//                        FFmpeg.getInstance(this@MainActivity).execute(arrayOf("a", "b"), object : FFmpegExecuteResponseHandler {
//                            override fun onFinish() {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun onSuccess(message: String?) {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun onProgress(message: String?) {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun onFailure(message: String?) {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun onStart() {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                        })
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onStart() {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                })
//            }
    }


    private fun uploadToDB(filesList: ArrayList<DataModel>) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        for (i in 0 until filesList.size) {
            val file = File(filesList[i].fileUri)
            val uri = Uri.fromFile(file)
            val storageRf = storage.getReference("Files/" + UUID.randomUUID().toString())
            storageRf.putFile(uri)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Uploaded", Toast.LENGTH_LONG).show()
                        } else {
                            Log.e("Some", "Error", it.exception)
                        }
                    }.addOnProgressListener {
                        val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                        progressDialog.setMessage("Uploaded $progress%")
                    }
        }


    }
}
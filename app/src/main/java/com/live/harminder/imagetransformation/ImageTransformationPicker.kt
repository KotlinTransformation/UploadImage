package com.live.harminder.imagetransformation

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class ImageTransformationPicker : BaseActivity() {
    var activity: Activity? = null
    var requestCodeGallary = 159
    var requestCodeCamera = 1231
    var imgPath = ""

    var permissionlistener: PermissionListener? = null


    fun mImageTransformation(activity: Activity) {
        this.activity= activity
        startTed()
//        if (activity != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                    activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
//                    activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    mRequestPermissionForCameraGallery()
//                }else{
//                    uploadImage()
//                }
//
//            } else {
//                uploadImage()
//            }
//        }
    }

    private fun startTed() {
        permissionlistener = object : PermissionListener {
          override  fun onPermissionGranted (){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (activity!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || activity!!.checkSelfPermission(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED || activity!!.checkSelfPermission(
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        mRequestPermissionForCameraGallery()
                        //   return false;
                    } else {
                        uploadImage()
                        //  return true;
                    }
                } else {
                    uploadImage()
                    // return true;
                }
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
            }
        }
        tedPermission()


        /* permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                //Ask for permission
                ImagePickers(mContext)
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@SignupActivity, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            }
        }*/
    }

    open fun tedPermission() {
        TedPermission.with(activity)
            .setPermissionListener(permissionlistener)
            .setRationaleConfirmText("Permissions")
            .setRationaleTitle("Permission required.")
            .setRationaleMessage("We need this permission for image picker..")
            .setDeniedTitle("Permission denied")
            .setDeniedMessage(
                "If you reject permission,you can not use image picker\n\nPlease turn on permissions at [Setting] > [Permission]"
            )
            .setGotoSettingButtonText("Settings")
            .setPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private fun uploadImage() {
        val uploadImage = Dialog(activity!!, R.style.Theme_Dialog)
        uploadImage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        uploadImage.setContentView(R.layout.camera_gallery_popup)
        uploadImage.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        uploadImage.setCancelable(true)
        uploadImage.setCanceledOnTouchOutside(true)
        uploadImage.window!!.setGravity(Gravity.BOTTOM)
        uploadImage.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvCamera = uploadImage.findViewById<TextView>(R.id.tvCamera)
        val tvGallery = uploadImage.findViewById<TextView>(R.id.tvGallery)
        val tv_cancel = uploadImage.findViewById<TextView>(R.id.tv_cancel)
        tvCamera.setOnClickListener {
            uploadImage.dismiss()
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(activity!!.packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    val myDirectory = File(Environment.getExternalStorageDirectory(), "Pictures")
                    if (!myDirectory.exists()) {
                        myDirectory.mkdirs()
                    }
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //Log.i(TAG, "IOException");
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    activity!!.startActivityForResult(cameraIntent, requestCodeCamera)
                }
            }
        }
        tv_cancel.setOnClickListener { uploadImage.dismiss() }
        tvGallery.setOnClickListener {
            uploadImage.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activity!!.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                requestCodeGallary
            )
        }
        uploadImage.show()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        var image: File? = null

        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        image = File.createTempFile(
            imageFileName,  // prefix
            ".jpg",  // suffix
            storageDir // directory
        )

        // Save a file: path for use with ACTION_VIEW intents
        imgPath = "file:" + image.absolutePath
        return image
    }
    private fun mRequestPermissionForCameraGallery() {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            ), 14758
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            14758 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show();
                    // main logic
                    uploadImage()
                } else {
                    // Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(
                                activity!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                activity!!, Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                                activity!!, Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                mRequestPermissionForCameraGallery()
                            }
                        }
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeCamera && resultCode == RESULT_OK) {
            try {
                if (Uri.parse(imgPath) != null) {
                    CropImage.activity(Uri.parse(imgPath))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == requestCodeGallary && resultCode == RESULT_OK) {
            try {
                val uri = data!!.data
                if (uri != null) {
                    if (uri != null) {
                        CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            try {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val uri = result.uri
                    if (uri != null) {
                        imgPath = Common.getPath(activity!!, uri).toString() //CommonMethods.getPath(activity, uri);
                        selectedImage(imgPath)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    abstract fun selectedImage(imagePath: String?)


}
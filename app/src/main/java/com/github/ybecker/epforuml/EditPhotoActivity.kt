package com.github.ybecker.epforuml

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants


/**
 * A simple [Fragment] subclass.
 * Use the [EditPhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPhotoActivity: AppCompatActivity() {

private lateinit var IMAGE_URI : String
private lateinit var imageView : ImageView

    override fun onCreate(avedInstanceState: Bundle?){
        super.onCreate(avedInstanceState)
        setContentView(R.layout.fragment_edit_photo)
        imageView = this.findViewById<ImageView>(R.id.imageToModify)
        checkPermission()
        }

    private fun goBackToQuestion(){
        val intent  = Intent(this, MainActivity::class.java)
        intent.putExtra("uri", IMAGE_URI)
        intent.putExtra("fragment", "EditPhotoFragment")
        intent.putExtra("questionTitle", this.intent.getStringExtra("questionTitle"))
        intent.putExtra("questionDetails", this.intent.getStringExtra("questionDetails"))
        startActivity(intent)

    }
    private fun checkPermission(){
        val permission =ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            openImage()
        }else{
            if(permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf(WRITE_EXTERNAL_STORAGE), 100)

            }else{
                openImage()
            }
        }
    }

    private fun openImage() {
        //Initialize URI

        IMAGE_URI = this.intent.getStringExtra("uri").toString()
        val uri = Uri.parse(IMAGE_URI)
        val intent = Intent(
            this,
            DsPhotoEditorActivity::class.java
        )
        intent.data = uri
        intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Images")

        intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.GRAY)
        intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.WHITE)
        intent.putExtra(
            DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
            arrayOf(
                DsPhotoEditorActivity.TOOL_CONTRAST,
                DsPhotoEditorActivity.TOOL_EXPOSURE,
                DsPhotoEditorActivity.TOOL_FILTER,
                DsPhotoEditorActivity.TOOL_FRAME,
                DsPhotoEditorActivity.TOOL_PIXELATE,
                DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,
                DsPhotoEditorActivity.TOOL_WARMTH,
                DsPhotoEditorActivity.TOOL_SHARPNESS,
                DsPhotoEditorActivity.TOOL_STICKER
            )
        )
        startActivityForResult(intent,101)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            goBackToQuestion()


        }
    }

        override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openImage()
        }else{
            Toast.makeText(applicationContext, "permission denied",Toast.LENGTH_SHORT).show()
        }
    }


}


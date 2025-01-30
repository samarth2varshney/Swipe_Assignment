package com.ajit.swipeandroidassignment.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(msg: String?) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
}


fun Uri.isValidImage(context: Context): Boolean {
    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
    if (!fileExtension.equals("jpg", true) &&
        !fileExtension.equals("jpeg", true) &&
        !fileExtension.equals("png", true)
    ) {
        Toast.makeText(
            context,
            "Please select an image in JPEG or PNG format.",
            Toast.LENGTH_LONG
        ).show()
        return false
    }

    // Check the aspect ratio of the image (1:1 ratio)
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(this.path, options)
    val imageWidth = options.outWidth
    val imageHeight = options.outHeight
    if (imageWidth != imageHeight) {
        Toast.makeText(
            context,
            "Please select an image with a 1:1 aspect ratio.",
            Toast.LENGTH_LONG
        ).show()
        return false
    }

    return true
}
package com.example.enterprise_wifi_ulstu

import android.widget.Toast
import androidx.fragment.app.Fragment

inline fun Fragment.toast(message:()->String){
    Toast.makeText(this.context, message() , Toast.LENGTH_LONG).show()
}
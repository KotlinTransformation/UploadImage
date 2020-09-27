package com.live.harminder.imagetransformation

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

  abstract  class BaseActivity :AppCompatActivity(){
    protected abstract  val contentViewId:Int
    lateinit var context:Context
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if(contentViewId!=null){
            setContentView(contentViewId)
        }
        context= this@BaseActivity
    }
}
package com.example.lockpattern

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var arrIdLockView:MutableList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arrIdLockView = mutableListOf()
        findViewById<IceLockView>(R.id.myCustmoView).onIceLockListener(object : IceLockView.OnIceLockListener{
            override fun onListenerMatrix(idDots: MutableList<Int>) {
                if (arrIdLockView.size == 0){
                    arrIdLockView.addAll(idDots)
                    Toast.makeText(this@MainActivity, "$idDots", Toast.LENGTH_SHORT).show()
                    Log.i("test123","$idDots")
                }else{
                    if (arrIdLockView == idDots){
                        Toast.makeText(this@MainActivity, "Draw success", Toast.LENGTH_SHORT).show()
                        Log.i("test123","Draw success")
                        arrIdLockView.clear()
                    }else{
                        Toast.makeText(this@MainActivity, "Draw wrong", Toast.LENGTH_SHORT).show()
                        Log.i("test123","Draw wrong")
                    }
                }
            }
        })
    }
}
package com.xanarry.lantrans

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.xanarry.lantrans.minterfaces.ProgressListener
import com.xanarry.lantrans.minterfaces.SearchStateListener
import com.xanarry.lantrans.network.UdpClient
import com.xanarry.lantrans.network.UdpServer

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            UdpClient(SearchStateListener { tryTimes, times -> }, 9992).search()
        }

        Thread {
            UdpServer(9992).waitClient()
        }.start()
    }

}

package com.xanarry.lantrans

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.xanarry.lantrans.minterfaces.ProgressListener
import com.xanarry.lantrans.minterfaces.SearchStateListener
import com.xanarry.lantrans.network.UdpClient
import com.xanarry.lantrans.network.UdpServer
import com.xanarry.lantrans.utils.Utils
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity() {

    val control = ThreadControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        my_ip.text = Utils.getLocalHostLanIP().hostAddress

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            UdpClient(SearchStateListener { tryTimes, times -> }, 9992).search()
        }

        control.waitScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        control.stopWait()
    }

}

class ThreadControl {
    private val udpServer = UdpServer(9992)

    fun waitScan() {
        Thread {
            udpServer.waitClient()
        }.start()
    }

    fun stopWait() {
        udpServer.kill()
    }


}

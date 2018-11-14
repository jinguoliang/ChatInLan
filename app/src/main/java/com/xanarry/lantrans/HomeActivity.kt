package com.xanarry.lantrans

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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
            Snackbar.make(view, "Scaning...", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
            SearchThread { addresses ->
                runOnUiThread {
                    addresses.forEach {
                        Toast.makeText(this@HomeActivity, "receiver = $it", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()

        }

        control.waitScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        control.stopWait()
    }

}

class SearchThread(val callback: (address: List<String>) -> Unit) : Thread() {
    override fun run() {
        val address = UdpClient(9992).search()
        if (address != null) {
            callback(address)
        }
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

package com.xanarry.lantrans

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.view.recycleview.withItems
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

        initUI()

        control.waiting()
    }

    private fun initUI() {
        my_ip.text = Utils.getLocalHostLanIP().hostAddress
        setupChatList()
        setupInput()
        setupFab()
    }

    private fun setupInput() {
        send.setOnClickListener {
            val content = inputBox.text.toString()
            sendTextContent(content)
        }
    }

    private fun sendTextContent(content: String) {
        mAddress?.let {
            ChatClient().send(it)
        }
    }

    private fun setupChatList() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRecyclerView.withItems {  }
    }

    private var mAddress: String? = null

    private fun setupFab() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Scaning...", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
            SearchThread { addresses ->
                runOnUiThread {
                    addresses.forEach {
                        Toast.makeText(this@HomeActivity, "receiver = $it", Toast.LENGTH_LONG).show()
                    }
                    mAddress = addresses[0]
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

    fun waiting() {
        Thread {
            udpServer.waitClient()
        }.start()
    }

    fun stopWait() {
        udpServer.kill()
    }
}

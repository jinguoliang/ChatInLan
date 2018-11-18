package com.xanarry.lantrans

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.google.android.material.snackbar.Snackbar
import com.xanarry.lantrans.minterfaces.ProgressListener
import com.xanarry.lantrans.network.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import org.jetbrains.anko.doAsync
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private var client: TcpClient? = null

    val control = ThreadControl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        initUI()

        control.waiting()
    }

    private fun initUI() {
        my_ip.text = IPUtils.getIPInLan()?.hostAddress
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
            doAsync {
                if (client == null) {
                    client = TcpClient(HostAddress(it, TRANSFER_WAITER_PORT), ProgressListener { filePositon, hasGot, totalSize, speed -> })
                    client?.connectReceiver()
                }
                client?.send("hello")
                client?.sendFile("/sdcard/zhao.mp3")
            }

        }
    }

    private fun setupChatList() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRecyclerView.withItems { }
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

        control.waiting()
    }

    override fun onDestroy() {
        super.onDestroy()
        control.stopWait()
    }

}

class SearchThread(val callback: (address: List<String>) -> Unit) : Thread() {
    override fun run() {
        val address = UdpClient(SCAN_WAITER_PORT).search()
        if (address != null) {
            callback(address)
        }
    }
}

const val SCAN_WAITER_PORT = 9992
const val TRANSFER_WAITER_PORT = 23732

class ThreadControl {
    private val udpServer = UdpServer(SCAN_WAITER_PORT)
    private val tcpServer = TcpServer(TRANSFER_WAITER_PORT, ProgressListener { filePositon, hasGot, totalSize, speed -> })
    fun waiting() {
        Thread {
            try {
                udpServer.waitClient()
            } catch (e: IOException) {
                loge("udp server exception")
            }
            tcpServer.waitClient()
            val msg = tcpServer.receiveMessage()
            loge("msg = $msg")
            tcpServer.receiveFile()
        }.start()
    }

    fun stopWait() {
        udpServer.kill()
    }
}

package com.jone.lanchat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.google.android.material.snackbar.Snackbar
import com.jone.lanchat.network.*
import com.jone.lanchat.utils.showToast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import org.jetbrains.anko.doAsync
import java.io.IOException

class ChatRoomActivity : AppCompatActivity() {

    private var client: TcpClient? = null

    val control = ThreadControl()

    val presenter = ChatRoomPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        initUI()

        control.waiting()
    }

    private fun initUI() {
        showMyIPInLan()
        setupChatList()
        setupInput()
        setupFab()
    }

    private fun showMyIPInLan() {
        my_ip.text = IPUtils.getIPInLan()?.hostAddress
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
                    client = TcpClient(it, TRANSFER_WAITER_PORT)
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
            scanObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { addresses ->
                        showOtherPoint(addresses)
                    }
        }

        control.waiting()
    }

    private fun scanObservable(): Observable<List<String>> {
        return Observable.create<List<String>> {
            val addresses = UdpScanner(SCAN_WAITER_PORT).scan()
            it.onNext(addresses)
        }
    }

    private fun showOtherPoint(addresses: List<String>) {
        addresses.forEach {
            Toast.makeText(this@ChatRoomActivity, "receiver = $it", Toast.LENGTH_LONG).show()
        }
        if (addresses.isEmpty()) {
            showToast(R.string.no_target)
        } else {
            mAddress = addresses[0]
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        control.stopWait()
    }

}

const val SCAN_WAITER_PORT = 9992
const val TRANSFER_WAITER_PORT = 23732

class ThreadControl {
    private val udpServer = UdpServer(SCAN_WAITER_PORT)
    private val tcpServer = TcpServer(TRANSFER_WAITER_PORT)
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

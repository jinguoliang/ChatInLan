package com.jone.lanchat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.jone.lanchat.network.IPUtils
import com.jone.lanchat.network.TcpClient
import com.jone.lanchat.network.TcpServer
import com.jone.lanchat.network.UdpServer
import com.jone.lanchat.utils.showToast
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import java.io.IOException

class ChatRoomActivity : AppCompatActivity() {

    private val presenter = ChatRoomPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        initUI()

        presenter.onActivityCreate()
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
            presenter.sendTextContent(content)
        }
    }


    private fun setupChatList() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRecyclerView.withItems { }
    }

    private fun setupFab() {
        fab.setOnClickListener { view ->
            presenter.getOtherPoint { addresses ->
                showOtherPoint(addresses)
            }
        }
    }

    private fun showOtherPoint(addresses: List<String>) {
        addresses.forEach {
            Toast.makeText(this@ChatRoomActivity, "receiver = $it", Toast.LENGTH_LONG).show()
        }
        if (addresses.isEmpty()) {
            showToast(R.string.no_target)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroy()
    }

}

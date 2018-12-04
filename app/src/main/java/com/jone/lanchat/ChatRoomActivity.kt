package com.jone.lanchat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.empty.jinux.baselibaray.utils.inflate
import com.empty.jinux.baselibaray.view.recycleview.Item
import com.empty.jinux.baselibaray.view.recycleview.ItemAdapter
import com.empty.jinux.baselibaray.view.recycleview.ItemController
import com.empty.jinux.baselibaray.view.recycleview.withItems
import com.jone.lanchat.network.IPUtils
import com.jone.lanchat.utils.getSelectFileIntent
import com.jone.lanchat.utils.showToast
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.item_chat_bubble.*
import org.jetbrains.anko.longToast

class ChatRoomActivity : RxAppCompatActivity() {

    private val presenter = ChatRoomPresenter(this)

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
    }

    private fun showMyIPInLan() {
        my_ip.text = IPUtils.getIPInLan()?.hostAddress
    }

    private val REQUEST_CODE_SELECT_FILE = 234

    private fun setupInput() {
        send.setOnClickListener {
            val content = inputBox.text.toString()
            presenter.sendTextContent(content)
            inputBox.setText("")
        }
        plus.setOnClickListener {
            startActivityForResult(getSelectFileIntent(recentDirectoryUri), REQUEST_CODE_SELECT_FILE)
        }
    }

    private lateinit var itemAdapter: ItemAdapter

    private fun setupChatList() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRecyclerView.withItems { }
        itemAdapter = chatRecyclerView.adapter as ItemAdapter
    }

    fun showMessage(message: String?) {
        message?.apply {
            itemAdapter.add(ChatBubble(this))
        }
    }

    fun showOtherPoint(address: String) {
        Toast.makeText(this@ChatRoomActivity, "receiver = $address", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_SELECT_FILE -> {
                onSelectFileResult(resultCode, data)
            }
        }


    }

    private var recentDirectoryUri: Uri? = null

    private fun onSelectFileResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED || data == null) {
            longToast(R.string.not_select_file)
            return
        }
        recentDirectoryUri = data.data
        showToast(data.data.toString())
    }
}

class ChatBubble(val msg: String) : Item {
    companion object Controller : ItemController {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as Holder
            item as ChatBubble
            holder.text.text = item.msg
        }

        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return Holder(parent.inflate(R.layout.item_chat_bubble, false))
        }
    }

    class Holder(override val containerView: View?) : RecyclerView.ViewHolder(containerView!!), LayoutContainer

    override val controller: ItemController
        get() = Controller

}

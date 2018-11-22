package com.jone.lanchat

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
import com.jone.lanchat.utils.showToast
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.item_chat_bubble.*

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

    private fun setupInput() {
        send.setOnClickListener {
            send.text = ""
            val content = inputBox.text.toString()
            presenter.sendTextContent(content)
        }
    }


    private lateinit var itemAdapter: ItemAdapter

    private fun setupChatList() {
        chatRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        chatRecyclerView.withItems {  }
        itemAdapter = chatRecyclerView.adapter as ItemAdapter
    }

    fun showMessage(message: String?) {
        message?.apply {
            itemAdapter.add(ChatBubble(this))
        }
    }

    fun showOtherPoint(addresses: List<String>) {
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

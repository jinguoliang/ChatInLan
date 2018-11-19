package com.xanarry.lantrans.network

import android.util.Log
import com.xanarry.lantrans.utils.Configuration.BROADCAST_DATA
import com.xanarry.lantrans.utils.Configuration.RESPONSE_DATA
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketTimeoutException

/**
 *
 */
class UdpServer(private val mPort: Int) {
    private var socket: DatagramSocket? = null
    private var mLive = true

    @Throws(IOException::class)
    fun waitClient() {
        socket = DatagramSocket(mPort)

        val buf = ByteArray(BROADCAST_DATA.toByteArray().size)
        val message = DatagramPacket(buf, buf.size)
        while (mLive) {
            message.data = buf
            try {
                socket!!.receive(message)
            } catch (e: SocketTimeoutException) {

            }

            val msg = String(message.data)

            Log.e(TAG, "waitClient: $msg")
            if (BROADCAST_DATA == msg) {
                message.data = RESPONSE_DATA.toByteArray()
                socket!!.send(message)
                Log.e(TAG, "waitClient: send out")
            }
        }
        socket!!.close()
    }

    fun kill() {
        mLive = false
    }

    fun close() {
        socket!!.close()
    }

    companion object {
        const val TAG = "UdpServer"
    }
}

package com.jone.lanchat.network

import android.util.Log
import com.empty.jinux.baselibaray.log.logd
import com.jone.lanchat.utils.Configuration
import com.jone.lanchat.utils.runUntil
import java.io.UnsupportedEncodingException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException

/**
 */
class UdpScanner(private val port: Int) : Scanner {
    var isLive: Boolean = true

    override fun scan(onResult: (address: String) -> Unit) {
        try {
            val clientSocket = broadcast(Configuration.BROADCAST_DATA)
            waitResponse(clientSocket, Configuration.RESPONSE_DATA, onResult)
            clientSocket.close()
        } catch (e3: SocketException) {
            e3.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun kill() {
        this.isLive = false
    }

    private fun waitResponse(
            clientSocket: DatagramSocket,
            expectResponse: String,
            onResult: (address: String) -> Unit
    ) {
        val receiveBuf = ByteArray(expectResponse.toByteArray().size)
        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

        runUntil (Configuration.SEARCH_TIMEOUT) {
            if (!isLive) {
                return@runUntil
            }

            try {
                clientSocket.receive(receivePacket)
                Log.e(TAG, "search: receive " + receivePacket.address)

                val msg = String(receivePacket.data)
                Log.e(TAG, "search: receive $msg")

                if (expectResponse == msg) {
                    Log.e(TAG, "search: matched")
                    onResult(receivePacket.address.hostAddress)
                }
            } catch (e: SocketTimeoutException) {
                logd("timeout")
            }
        }
    }

    private fun broadcast(msg: String): DatagramSocket {
        val sendBuf = msg.toByteArray()
        val broadcastAddress = IPUtils.getBroadcastAddress()
        val sendPacket = DatagramPacket(sendBuf, sendBuf.size, broadcastAddress, port)

        val clientSocket = DatagramSocket()
        clientSocket.broadcast = true
        clientSocket.soTimeout = Configuration.SEARCH_SOCKET_TIMEOUT
        clientSocket.send(sendPacket)

        return clientSocket
    }

    companion object {
        private const val TAG = "UdpScanner"
    }
}

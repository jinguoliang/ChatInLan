package com.jone.lanchat.network

import android.util.Log
import com.jone.lanchat.utils.Configuration
import com.jone.lanchat.utils.runUntil
import java.io.UnsupportedEncodingException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.*

/**
 */
class UdpScanner(private val port: Int) : Scanner {

    override fun scan(): List<String> {
        try {
            val clientSocket = broadcast(Configuration.BROADCAST_DATA)
            val addresses = waitResponse(clientSocket, Configuration.RESPONSE_DATA)
            clientSocket.close()
            return addresses
        } catch (e3: SocketException) {
            e3.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return Collections.emptyList()
    }

    private fun waitResponse(clientSocket: DatagramSocket, expectResponse: String): List<String> {
        val receiveBuf = ByteArray(expectResponse.toByteArray().size)
        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)
        val addresses = ArrayList<String>()

        runUntil(Configuration.SEARCH_TIMEOUT) {
            try {
                clientSocket.receive(receivePacket)
                Log.e(TAG, "search: receive " + receivePacket.address)

                val msg = String(receivePacket.data)
                Log.e(TAG, "search: receive $msg")

                if (expectResponse == msg) {
                    Log.e(TAG, "search: matched")
                    addresses.add(receivePacket.address.hostAddress)
                }
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
            }
        }

        return addresses
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

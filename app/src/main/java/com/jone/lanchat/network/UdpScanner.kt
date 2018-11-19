package com.jone.lanchat.network

import android.util.Log
import com.jone.lanchat.IPUtils
import com.jone.lanchat.utils.Configuration
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.util.*

/**
 */
class UdpClient(private val port: Int) {
    fun search(): List<String>? {
        val receiveBuf = ByteArray(Configuration.RESPONSE_DATA.toByteArray().size)
        val sendBuf = Configuration.BROADCAST_DATA.toByteArray()

        val broadcastAddress = IPUtils.getBroadcastAddress()
        val sendPacket = DatagramPacket(sendBuf, sendBuf.size, broadcastAddress, port)
        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

        val addresses = ArrayList<String>()
        try {
            val clientSocket = DatagramSocket()
            clientSocket.broadcast = true
            clientSocket.soTimeout = Configuration.SEARCH_SOCKET_TIMEOUT

            clientSocket.send(sendPacket)

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < Configuration.SEARCH_TIMEOUT) {
                clientSocket.receive(receivePacket)
                Log.e(TAG, "search: receive " + receivePacket.address)
                val msg = String(receivePacket.data)
                Log.e(TAG, "search: receive $msg")
                if (Configuration.RESPONSE_DATA == msg) {
                    Log.e(TAG, "search: match")
                    addresses.add(receivePacket.address.hostAddress)
                }
            }
            clientSocket.close()

        } catch (e3: SocketException) {
            e3.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addresses
    }

    companion object {
        private const val TAG = "UdpClient"
    }
}

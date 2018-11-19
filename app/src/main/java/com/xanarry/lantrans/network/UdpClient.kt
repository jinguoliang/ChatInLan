package com.xanarry.lantrans.network

import android.util.Log
import com.xanarry.lantrans.IPUtils
import com.xanarry.lantrans.utils.Configuration
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.util.*

/**
 */
class UdpClient(private val port: Int) {
    private val timeout: Int = Configuration.SEARCH_TIMOUT

    fun search(): List<String>? {
        val receiveBuf = ByteArray(Configuration.RESPONSE_DATA.toByteArray().size)
        val sendBuf = Configuration.BROADCAST_DATA.toByteArray()

        val broadcastAddress = IPUtils.getBroadcastAddress()
        Log.e(TAG, "search: " + broadcastAddress!!)
        val sendPacket = DatagramPacket(sendBuf, sendBuf.size, broadcastAddress, port)
        val receivePacket = DatagramPacket(receiveBuf, receiveBuf.size)

        val addresses = ArrayList<String>()


        try {
            val clientSocket = DatagramSocket()//创建一个udpClient
            clientSocket.broadcast = true//广播信息
            clientSocket.soTimeout = this.timeout * 1000//如果2秒后没后得到服务器的回应, 抛出超时异常, 以便重新广播

            clientSocket.send(sendPacket)//向服务器发送数据包

            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 10000) {
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

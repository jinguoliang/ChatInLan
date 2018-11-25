package com.jone.lanchat.network

import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

/**
 */
class TcpServer(private val port: Int) {

    private var serverSocket = ServerSocket(this.port)
    private var clientSocket: Socket? = null
    private var bufferedInputStream: BufferedInputStream? = null
    private var bufferedOutputStream: BufferedOutputStream? = null

    fun close() {
        try {
            bufferedInputStream?.close()
            bufferedOutputStream?.close()
            serverSocket?.close()
            clientSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun waitClient() {
        try {
            Log.e(TAG, "tcp server is accept")
            clientSocket = serverSocket!!.accept()
            Log.e(TAG, "tcp server is accepted")
            clientSocket!!.keepAlive = true

            bufferedInputStream = BufferedInputStream(clientSocket!!.getInputStream())
            Log.e(TAG, "input stream opened")
            bufferedOutputStream = BufferedOutputStream(clientSocket!!.getOutputStream())
            Log.e(TAG, "write stream opened")
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun receiveMessage(): String {
        if (bufferedInputStream == null) {
            return ""
        }

        val builder = StringBuilder()

        val buffer = ByteArray(MESSAGE_BUFFER_SIZE)
        try {
            val n = bufferedInputStream!!.read(buffer)
            if (n == -1) {
                return ""
            }
            builder.append(String(buffer, 0, n))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return builder.toString()
    }

    fun receiveFile() {
        if (bufferedInputStream == null) {
            return
        }

        val buffer = ByteArray(FILE_BUFFER_SIZE)
        try {
            val `in` = bufferedInputStream
            val out = BufferedOutputStream(FileOutputStream("/sdcard/hellosss"))
            var n = `in`!!.read(buffer)
            while (n > 0) {
                Log.e(TAG, "receiveFile: $n")
                out.write(buffer, 0, n)
                out.flush()
                n = bufferedInputStream!!.read(buffer)
            }
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {
        private const val TAG = "TcpServer"
        const val FILE_BUFFER_SIZE = 1024
        const val MESSAGE_BUFFER_SIZE = 64
    }
}

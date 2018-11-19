package com.jone.lanchat.network

/**
 */

import android.util.Log
import java.io.*
import java.net.Socket


class TcpClient(private val serverAddress: String, private val serverPort: Int) {

    private var clientSocket: Socket? = null
    private var bufferedInputStream: BufferedInputStream? = null
    private var bufferedOutputStream: BufferedOutputStream? = null

    fun close() {
        try {
            if (bufferedInputStream != null) {
                bufferedInputStream!!.close()
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream!!.close()
            }
            if (clientSocket != null) {
                clientSocket!!.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun connectReceiver() {
        try {
            clientSocket = Socket(serverAddress, serverPort)
            Log.e(TAG, "connectReceiver: socket created")
            bufferedInputStream = BufferedInputStream(clientSocket!!.getInputStream())
            Log.e(TAG, "connectReceiver: socket getInputStream")

            bufferedOutputStream = BufferedOutputStream(clientSocket!!.getOutputStream())
            Log.e(TAG, "connectReceiver: socket getOutputStream")

        } catch (e: IOException) {
            Log.e(TAG, "connectReceiver: ", e)
        }

    }

    fun send(msg: String) {
        if (bufferedOutputStream == null) {
            return
        }

        try {
            bufferedOutputStream!!.write(msg.toByteArray())
            bufferedOutputStream!!.flush()
            Log.e(TAG, "send: writed")
        } catch (e: IOException) {
            e.printStackTrace()

        }

    }

    fun sendFile(path: String) {
        if (bufferedOutputStream == null) {
            return
        }

        try {
            val `in` = BufferedInputStream(FileInputStream(path))
            val out = bufferedOutputStream

            val buf = ByteArray(1024)
            var n = `in`.read(buf)
            while (n > 0) {
                Log.e(TAG, "sendFile: $n")
                out!!.write(buf, 0, n)
                out.flush()
                n = `in`.read(buf)
            }
            `in`.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {
        private const val TAG = "TcpClient"
    }
}

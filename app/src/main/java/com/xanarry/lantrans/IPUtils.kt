package com.xanarry.lantrans

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

class IPUtils {
    companion object {
        /**
         * 获取本机在局域网中的IP
         *
         * @return
         */
        fun getIPInLan(): InetAddress? {
            try {
                val address = Collections.list(NetworkInterface.getNetworkInterfaces())
                        .filter { netInterface ->
                            !netInterface.isLoopback && !netInterface.isPointToPoint
                                    && !netInterface.isVirtual || netInterface.isUp
                        }
                        .flatMap { Collections.list(it.inetAddresses) }
                        .asSequence()
                        .filter { it is Inet4Address }
                        .filter { it.hostAddress.startsWith("192") }
                        .toList()
                return if (address.isEmpty()) {
                    null
                } else {
                    address[0]
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

            return null
        }

        private fun getBroadcastAddress(address: InetAddress?): InetAddress? {
            if (address == null) {
                return null
            }

            return try {
                NetworkInterface.getByInetAddress(address).interfaceAddresses
                        .filter { it.address == address }[0].broadcast
            } catch (e: SocketException) {
                e.printStackTrace()
                null
            }
        }

        fun getBroadcastAddress(): InetAddress? {
            return getBroadcastAddress(getIPInLan())
        }
    }
}
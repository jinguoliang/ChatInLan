package com.jone.lanchat

import com.empty.jinux.baselibaray.log.loge
import com.jone.lanchat.network.UdpServer
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import java.io.IOException

class WaiterObservable : Observable<Void>() {
    private val udpServer = UdpServer(SCAN_WAITER_PORT)

    override fun subscribeActual(observer: Observer<in Void>?) {
        observer?.onSubscribe(D())

        try {
            udpServer.waitClient()
        } catch (e: IOException) {
            loge("udp server exception")
        }
    }

    inner class D : MainThreadDisposable() {
        override fun onDispose() {
            udpServer.kill()
        }
    }
}
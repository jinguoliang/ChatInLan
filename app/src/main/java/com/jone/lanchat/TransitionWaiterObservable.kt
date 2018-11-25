package com.jone.lanchat

import com.empty.jinux.baselibaray.log.logi
import com.jone.lanchat.network.TcpServer
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class TransitionWaiterObservable : Observable<String>() {
    private val tcpServer = TcpServer(TRANSFER_WAITER_PORT)

    private var isLive: Boolean = true

    override fun subscribeActual(observer: Observer<in String>?) {
        observer?.onSubscribe(D())
        while (isLive) {
            tcpServer.waitClient()

            var msg = tcpServer.receiveMessage()
            while (msg.isNotEmpty() && isLive) {
                logi("msg = $msg")
                observer?.onNext(msg)
                msg = tcpServer.receiveMessage()
            }
        }
    }

    inner class D : MainThreadDisposable() {
        override fun onDispose() {
            isLive = false
            tcpServer.close()
        }
    }
}
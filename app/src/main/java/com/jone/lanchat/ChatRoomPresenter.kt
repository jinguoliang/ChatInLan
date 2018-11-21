package com.jone.lanchat

import com.empty.jinux.baselibaray.log.loge
import com.empty.jinux.baselibaray.log.logi
import com.jone.lanchat.network.TcpClient
import com.jone.lanchat.network.TcpServer
import com.jone.lanchat.network.UdpScanner
import com.jone.lanchat.network.UdpServer
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

class ChatRoomPresenter {

    private var client: TcpClient? = null

    private var address: String? = null

    private fun scanObservable(): Observable<List<String>> {
        return Observable.create<List<String>> {
            val addresses = UdpScanner(SCAN_WAITER_PORT).scan()
            it.onNext(addresses)
        }
    }

    private var scanDisposable: Disposable? = null

    fun getOtherPoint(callback: (addresses: List<String>) -> Unit) {
        scanDisposable = scanObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { addresses ->
                    callback(addresses)
                    address = addresses[0]
                }
    }

    internal fun sendTextContent(content: String) {
        address?.let {
            Observable.create<Void> { _ ->
                if (client == null) {
                    client = TcpClient(it, TRANSFER_WAITER_PORT)
                    client?.connectReceiver()
                }
                client?.send(content)
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

        }
    }

    fun onActivityCreate() {
        startWaiting()
    }

    private var waiterDisposable: Disposable? = null

    private var transitionWaiterDisposable: Disposable? = null

    private fun startWaiting() {
        waiterDisposable = WaiterObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        transitionWaiterDisposable = TransitionWaiterObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun onActivityDestroy() {
        scanDisposable?.dispose()
        waiterDisposable?.dispose()
        client?.close()
        transitionWaiterDisposable?.dispose()
    }
}

const val SCAN_WAITER_PORT = 9992
const val TRANSFER_WAITER_PORT = 23732

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

class TransitionWaiterObservable : Observable<Void>() {
    private val tcpServer = TcpServer(TRANSFER_WAITER_PORT)

    override fun subscribeActual(observer: Observer<in Void>?) {
        observer?.onSubscribe(D())
        tcpServer.waitClient()
        val msg = tcpServer.receiveMessage()
        logi("msg = $msg")
    }

    inner class D : MainThreadDisposable() {
        override fun onDispose() {
            tcpServer.close()
        }
    }
}


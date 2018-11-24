package com.jone.lanchat

import com.empty.jinux.baselibaray.log.loge
import com.jone.lanchat.network.TcpClient
import com.jone.lanchat.utils.ioToUI
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class ChatRoomPresenter(val view: ChatRoomActivity) {

    private var client: TcpClient? = null

    private var addresses: MutableList<String> = mutableListOf()

    private fun scanObservable(): Observable<String> {
        return ScannerObservable()
    }

    private var scanDisposable: Disposable? = null

    fun getOtherPoint(callback: (addresses: String) -> Unit) {
        scanDisposable = scanObservable()
                .ioToUI()
                .bindToLifecycle(view)
//                .filter { it != IPUtils.getIPInLan()?.hostAddress }
                .subscribe { address ->
                    callback(address)
                    addresses.add(address)
                }
    }

    internal fun sendTextContent(content: String) {
        addresses.forEach {
            createTransferClientObserver(it, content)
                    .ioToUI()
                    .subscribe()
        }
    }

    private fun createTransferClientObserver(it: String, content: String): Observable<Void> {
        return Observable.create<Void> { _ ->
            if (client == null) {
                client = TcpClient(it, TRANSFER_WAITER_PORT)
                client?.connectReceiver()
                loge("connected server")
            }
            client?.send(content)
            loge("send")
        }
    }

    fun onActivityCreate() {
        startWaiting()
        getOtherPoint { address ->
            view.showOtherPoint(address)
        }
    }

    private var waiterDisposable: Disposable? = null

    private var transitionWaiterDisposable: Disposable? = null

    private fun startWaiting() {
        waiterDisposable = WaiterObservable()
                .ioToUI()
                .bindToLifecycle(view)
                .subscribe()
        transitionWaiterDisposable = TransitionWaiterObservable()
                .ioToUI()
                .bindToLifecycle(view)
                .subscribe {
                    view.showMessage(it)
                }
    }

    fun onActivityDestroy() {
        client?.close()
    }
}

const val SCAN_WAITER_PORT = 9992
const val TRANSFER_WAITER_PORT = 8832


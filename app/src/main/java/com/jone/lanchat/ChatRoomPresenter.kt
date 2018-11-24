package com.jone.lanchat

import com.jone.lanchat.network.IPUtils
import com.jone.lanchat.network.TcpClient
import com.jone.lanchat.network.UdpScanner
import com.jone.lanchat.utils.ioToUI
import com.trello.rxlifecycle3.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class ChatRoomPresenter(val view: ChatRoomActivity) {

    private var client: TcpClient? = null

    private var address: String? = null

    private fun scanObservable(): Observable<List<String>> {
        return Observable.create<List<String>> {
            val addresses = UdpScanner(SCAN_WAITER_PORT).scan()
                    .filter { it != IPUtils.getIPInLan()?.hostAddress }
            if (addresses.isNotEmpty()) {
                it.onNext(addresses)
            }
        }
    }

    private var scanDisposable: Disposable? = null

    fun getOtherPoint(callback: (addresses: List<String>) -> Unit) {
        scanDisposable = scanObservable()
                .ioToUI()
                .bindToLifecycle(view)
                .subscribe { addresses ->
                    callback(addresses)
                    address = addresses[0]
                }
    }

    internal fun sendTextContent(content: String) {
        address?.let {
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
            }
            client?.send(content)
        }
    }

    fun onActivityCreate() {
        startWaiting()
        getOtherPoint { addresses ->
            view.showOtherPoint(addresses)
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


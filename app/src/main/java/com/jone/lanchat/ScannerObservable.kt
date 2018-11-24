package com.jone.lanchat

import com.jone.lanchat.network.UdpScanner
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class ScannerObservable : Observable<String>() {
    val scanner = UdpScanner(SCAN_WAITER_PORT)
    override fun subscribeActual(emitter: Observer<in String>?) {
        emitter?.onSubscribe(D())
        scanner.scan {
            emitter?.onNext(it)
        }
    }

    inner class D : MainThreadDisposable() {
        override fun onDispose() {
            scanner.kill()
        }
    }
}
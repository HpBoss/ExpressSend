package com.noah.express_send.ui.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseObserver<T> : Observer<T> {
    override fun onSubscribe(d: Disposable) {}
    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        onFailure(e)
    }

    override fun onComplete() {}
    abstract fun onSuccess(t: T)
    abstract fun onFailure(e: Throwable)
}
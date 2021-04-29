package com.noah.datainfo

/**
 * @Auther: 何飘
 * @Date: 3/29/21 18:30
 * @Description:
 */
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
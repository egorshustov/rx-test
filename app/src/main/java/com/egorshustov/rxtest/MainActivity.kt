package com.egorshustov.rxtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //observableExample()
        flowable()
    }

    private fun observableExample() {
        val taskObservable: Observable<Task> = Observable
            .fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .filter(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    Log.d(TAG, "test: ${Thread.currentThread().name}")
                    Thread.sleep(1000)
                    return t.isComplete
                }
            })
            .observeOn(AndroidSchedulers.mainThread())

        taskObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe: called.")
            }

            override fun onNext(t: Task) {
                Log.d(TAG, "onNext: ${Thread.currentThread().name}")
                Log.d(TAG, "onNext: ${t.description}")
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: $e")
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete: called.")
            }

        })
    }

    private fun flowable() {
        Flowable.range(0, 1000000)
            .onBackpressureBuffer()
            .observeOn(Schedulers.computation())
            .subscribe(object : FlowableSubscriber<Int> {
                override fun onSubscribe(s: Subscription) {
                    s.request(1000000)
                }

                override fun onNext(t: Int?) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(t: Throwable?) {
                    Log.e(TAG, "onError: ", t)
                }

                override fun onComplete() {
                }
            })
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
package com.egorshustov.rxtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import java.lang.Thread.sleep
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //observableExample()

        //flowable()

        //disposable()

        //createFromSingleObject()
        //createFromListOfObjects()
        //just()
        //range()
        //repeat()

        //interval()
        //timer()

        //fromArray()
        //fromIterable()
        fromCallable()
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

                override fun onComplete() {}
            })
    }

    private fun disposable() {
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
                disposables.add(d)
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

        disposables.add(taskObservable.subscribe(object : Consumer<Task> {
            override fun accept(t: Task?) {
            }
        }))
    }

    private fun createFromSingleObject() {
        val task = Task("Walk the dog", false, 4)

        val singleTaskObservable = Observable
            .create(object : ObservableOnSubscribe<Task> {
                override fun subscribe(emitter: ObservableEmitter<Task>) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(task)
                        emitter.onComplete()
                    }
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        singleTaskObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(task: Task) {
                Log.d(TAG, "onNext: single task: " + task.description)
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        })
    }

    private fun createFromListOfObjects() {
        val taskListObservable = Observable
            .create(ObservableOnSubscribe<Task> { emitter ->
                // Inside the subscribe method iterate through the list of tasks and call onNext(task)
                for (task in DataSource.createTasksList()) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(task)
                    }
                }
                // Once the loop is complete, call the onComplete() method
                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        taskListObservable.subscribe(object : Observer<Task> {
            override fun onSubscribe(d: Disposable) {}

            override fun onNext(task: Task) {
                Log.d(TAG, "onNext: task list: " + task.description)
            }

            override fun onError(e: Throwable) {}

            override fun onComplete() {}
        })
    }

    private fun just() {
        Observable.just("first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "ninth", "tenth")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    Log.d(TAG, "onSubscribe: called")
                }

                override fun onNext(t: String) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(e: Throwable) {
                    Log.d(TAG, "onError: $e")
                }

                override fun onComplete() {
                    Log.d(TAG, "onComplete: done")
                }
            })
    }

    private fun range() {
        Observable.range(0, 10)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Task> {
                override fun apply(t: Int): Task {
                    Log.d(TAG, "apply: ${Thread.currentThread().name}")
                    return Task("This is a new task with priority $t", false, t)
                }
            })
            .takeWhile(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    Log.d(TAG, "test: ${Thread.currentThread().name}")
                    return t.priority < 5
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Task) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })
    }

    private fun repeat() {
        // repeat must be used in conjunction with another operator
        Observable.range(0, 3)
            .repeat(3)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: Int) {
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })
    }

    private fun interval() {
        val intervalObservable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .takeWhile(object : Predicate<Long> {
                override fun test(t: Long): Boolean {
                    Log.d(TAG, "test: $t, thread: ${Thread.currentThread().name}")
                    return t <= 5
                }
            })
            .observeOn(AndroidSchedulers.mainThread())

        val intervalDisposable = intervalObservable.subscribe {
            Log.d(TAG, it.toString())
        }
    }

    private fun timer() {
        val timerObservable = Observable.timer(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        timerObservable.subscribe(object : Observer<Long> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: Long) {
                Log.d(TAG, "Next: $t")
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }

    private fun fromArray() {
        val fromArrayDisposable = Observable.fromArray(DataSource.createTasksArray())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { array ->
                Log.d(TAG, "onNext: $array")
                array.forEach {
                    Log.d(TAG, "$it")
                }
            }
    }

    private fun fromIterable() {
        val fromIterableDisposable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }

    private fun fromCallable() {
        val tasksDao = AppDatabase.getInstance(application).taskDao()

        val callableDisposable = Observable
            .fromCallable(object : Callable<List<Task>> {
                override fun call(): List<Task> {
                    tasksDao.insertAllTasks(DataSource.createTasksList())
                    sleep(1000)
                    return tasksDao.getAllTasks()
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        // If using MVVM, clear disposables in ViewModel onCleared method
        disposables.clear()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
package com.egorshustov.rxtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscription
import java.lang.Thread.sleep
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()
    var timeSinceLastRequest: Long = 0

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
        //fromCallable()

        //filterForStringField()
        //filterForBooleanField()

        //distinct()

        //take()
        //takeWhile()

        //mapTaskToString()
        //mapTaskToUpdatedTask()

        //buffer()
        //bufferForTrackingUIInteractions()

        //debounce()
        throttleFirst()
    }

    private fun observableExample() {
        val taskObservable: Observable<Task> = Observable
            .fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .filter(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    Log.d(TAG, "test: ${Thread.currentThread().name}")
                    sleep(1000)
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

    private fun filterForStringField() {
        val filterObservable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .filter(object : Predicate<Task> {
                override fun test(t: Task): Boolean {
                    return when (t.description) {
                        "Walk the dog" -> true
                        else -> false
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread())

        val filterDisposable = filterObservable.subscribe {
            Log.d(TAG, "onNext: $it")
        }
    }

    private fun filterForBooleanField() {
        Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .filter {
                Log.d(TAG, "test: ${Thread.currentThread().name}")
                it.isComplete
            }.observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Task> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Task) {
                    Log.d(TAG, "onNext: ${Thread.currentThread().name}")
                    Log.d(TAG, "onNext: $t")
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }

    private fun distinct() {
        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .distinct { t -> t.description }.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }

    private fun take() {
        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .take(3)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }


    private fun takeWhile() {
        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .takeWhile { t -> t.isComplete }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }

    private fun mapTaskToString() {
        val extractDescriptionFunction = Function<Task, String> { t ->
            Log.d(TAG, "apply: doing work on thread: ${Thread.currentThread().name}")
            t.description
        }

        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .map(extractDescriptionFunction)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: $it")
            }
    }

    private fun mapTaskToUpdatedTask() {
        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .map {
                it.isComplete = true
                it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext: is this $it complete? " + it.isComplete)
            }
    }

    private fun buffer() {
        val disposable = Observable.fromIterable(DataSource.createTasksList())
            .subscribeOn(Schedulers.io())
            .buffer(2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                Log.d(TAG, "onNext: bundle results: -------------------")

                list.forEach {
                    Log.d(TAG, it.toString())
                }
            }
    }

    private fun bufferForTrackingUIInteractions() {
        val disposable = button.clicks()
            .map {
                Log.d(TAG, "map thread: ${Thread.currentThread().name}")
                1
            }
            .buffer(4, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.d(TAG, "onNext thread: ${Thread.currentThread().name}")
                Log.d(TAG, "onNext: you clicked ${it.size} times in 4 seconds!")
            }

        disposables.add(disposable)
    }

    private fun debounce() {
        timeSinceLastRequest = System.currentTimeMillis()

        val disposable = search_view.queryTextChanges()
            .debounce(1, TimeUnit.SECONDS)
            .subscribe {
                Log.d(TAG, "onNext: time  since last request: " + (System.currentTimeMillis() - timeSinceLastRequest))
                timeSinceLastRequest = System.currentTimeMillis()
                Log.d(TAG, "onNext thread: ${Thread.currentThread().name}")
                Log.d(TAG, "onNext: search query: $it")

                fakeSendRequestToServer(it.toString())
            }

        disposables.add(disposable)
    }

    private fun fakeSendRequestToServer(text: String) {
    }

    private fun throttleFirst() {
        // Restricts button spamming
        timeSinceLastRequest = System.currentTimeMillis()
        val disposable = button.clicks()
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                Log.d(TAG, "onNext: time since last clicked: " + (System.currentTimeMillis() - timeSinceLastRequest))
                Log.d(TAG, "onNext thread: ${Thread.currentThread().name}")
                someMethod()
            }
        disposables.add(disposable)
    }

    private fun someMethod() {
        timeSinceLastRequest = System.currentTimeMillis()
    }

    override fun onDestroy() {
        super.onDestroy()
        // If using MVVM, clear disposables in ViewModel onCleared method
        disposables.clear()
    }

    companion object {
        const val TAG = "RxTest"
    }
}
package kashish.com.database

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Created by Kashish on 11-08-2018.
 */
class  AppExecutors {

    companion object {
        private val LOCK: Any = Object()
        private var sInstance: AppExecutors? = null


        private class MainThreadExecutor: Executor {

            private var mainThreadHandler: Handler = Handler(Looper.getMainLooper())

            override fun execute(p0: Runnable?) {
                mainThreadHandler.post(p0)
            }

        }

        fun getInstance(): AppExecutors{
            if (sInstance == null){
                synchronized(LOCK){
                    sInstance = AppExecutors(Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(3),
                            MainThreadExecutor())
                }
            }
            return sInstance!!
        }

    }

    private var diskIO: Executor? = null
    private var mainThread: Executor? = null
    private var networkIO: Executor? = null

    constructor(diskIO: Executor, networkIO: Executor, mainThread: Executor){
        this.diskIO = diskIO
        this.networkIO = networkIO
        this.mainThread = mainThread
    }

    fun diskIO(): Executor = diskIO!!
    fun mainThread(): Executor = mainThread!!
    fun networkIO(): Executor = networkIO!!

}
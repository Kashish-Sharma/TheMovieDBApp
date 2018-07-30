package kashish.com.singleton

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.util.LruCache

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley


class VolleySingleton private constructor(context: Context) {
    private var mRequestQueue: RequestQueue? = null
    val imageLoader: ImageLoader

    val requestQueue: RequestQueue
        get() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mCtx.applicationContext)
            }
            return this.mRequestQueue!!
        }

    init {
        mCtx = context
        mRequestQueue = requestQueue
        imageLoader = ImageLoader(mRequestQueue,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)

                    override fun getBitmap(url: String): Bitmap {
                        return cache.get(url)
                    }

                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }

    companion object {

        private var mInstance: VolleySingleton? = null
        private lateinit var mCtx: Context

        @Synchronized
        fun getInstance(context: Context): VolleySingleton {
            if (mInstance == null) {
                 mInstance = VolleySingleton(context)
            }
            return mInstance as VolleySingleton



        }
    }

}
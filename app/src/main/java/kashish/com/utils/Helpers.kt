package kashish.com.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by Kashish on 01-08-2018.
 */
object Helpers {

    fun buildImageUrl(path: String): String {
        return "http://image.tmdb.org/t/p/w342" + path
    }

    fun buildBackdropImageUrl(path: String): String {
        return "http://image.tmdb.org/t/p/w780" + path
    }

    fun buildYouTubeThumbnailURL(key: String): String {
        return "https://img.youtube.com/vi/$key/0.jpg"
    }

    fun buildYoutubeURL(key: String): String {
        return "https://www.youtube.com/watch?v=" + key
    }


    fun handleViewHideOnScroll(view: View, dy: Int, maxTranslation: Float) {
        val translationY = view.translationY
        if (dy > 0) {
            if (translationY < maxTranslation) {
                if (translationY + dy < maxTranslation) {
                    view.translationY = translationY + dy
                } else {
                    view.translationY = maxTranslation
                }
            }
        } else {
            if (translationY > 0) {
                if (translationY + dy > 0) {
                    view.translationY = translationY + dy
                } else {
                    view.translationY = 0f
                }
            }
        }
    }

    fun setUpTransparentStatusBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

}

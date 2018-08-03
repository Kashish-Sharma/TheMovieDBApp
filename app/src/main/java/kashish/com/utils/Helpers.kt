package kashish.com.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.Window
import kashish.com.utils.DateUtils.Companion.getDateFromEpoch
import kashish.com.utils.Urls.Companion.MOVIE_DETAILS_BASE_URL
import kashish.com.utils.Urls.Companion.TMDB_API_KEY
import java.text.ParseException
import java.text.SimpleDateFormat

/**
 * Created by Kashish on 01-08-2018.
 */
object Helpers {

    fun buildUpcomingMoviesUrl(pageNumber: Int, adult: String = "true"): String{
        return "https://api.themoviedb.org/3/movie/upcoming?api_key="+ TMDB_API_KEY+
                "&language=en-US"+"&page="+pageNumber+"&region=IN|US&with_release_type=2|3"
    }

    fun buildMovieCastUrl(movieId: String): String{
        return "https://api.themoviedb.org/3/movie/"+movieId+"/credits?api_key="+ TMDB_API_KEY
    }

    fun buildProfileImageUrl(path: String): String {
        return "http://image.tmdb.org/t/p/w185" + path
    }

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

    fun buildMovieDetailUrl(movieId: String): String {
        return MOVIE_DETAILS_BASE_URL + movieId + "?api_key=" + TMDB_API_KEY + "&append_to_response=videos"
    }

    fun buildMovieReviewUrl(movieId: String, pageNumber: Int): String {
        return MOVIE_DETAILS_BASE_URL + movieId + "/reviews?api_key=" + TMDB_API_KEY + "&language=en-US&page=" + pageNumber
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
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

}

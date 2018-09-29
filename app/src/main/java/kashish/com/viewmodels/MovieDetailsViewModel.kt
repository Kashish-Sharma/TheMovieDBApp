package kashish.com.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import kashish.com.repositories.MovieDetailsRepository
import kashish.com.models.MovieDetail
import kashish.com.requestmodels.MovieCreditRequest
import kashish.com.requestmodels.MovieReviewsRequest


/**
 * Created by Kashish on 15-08-2018.
 */
class MovieDetailsViewModel(private val detailsRepo: MovieDetailsRepository): ViewModel() {

    private var movieDetail: LiveData<MovieDetail>? = null
    private var movieReviews: LiveData<MovieReviewsRequest>? = null
    private var movieCredit: LiveData<MovieCreditRequest>? = null

    fun getDetails(movieId: String): LiveData<MovieDetail>{
        if (movieDetail == null){
            movieDetail = detailsRepo.getMovieDetails(movieId)
        }
        return movieDetail!!
    }

    fun getReviews(movieId: Long): LiveData<MovieReviewsRequest>{
        if (movieReviews == null){
            movieReviews = detailsRepo.getMovieReviews(movieId)
        }
        return movieReviews!!
    }

    fun getCredits(movieId: Long): LiveData<MovieCreditRequest>{
        if (movieCredit == null){
            movieCredit = detailsRepo.getMovieCredit(movieId)
        }
        return movieCredit!!
    }

}
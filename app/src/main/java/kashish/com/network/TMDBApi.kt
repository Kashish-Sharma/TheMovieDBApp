package kashish.com.network

import android.util.Log
import kashish.com.models.MovieDetail
import kashish.com.requestmodels.MovieCreditRequest
import kashish.com.requestmodels.MovieRequest
import kashish.com.requestmodels.MovieReviewsRequest
import kashish.com.requestmodels.MovieVideosRequest
import kashish.com.API_KEY.Companion.TMDB_API_KEY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Kashish on 12-08-2018.
 */
private val TAG: String = "TMDBApi"

interface TMDBApi {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String,
                         @Query("language") language: String,
                         @Query("page") pageNumber: Int,
                         @Query("region") region:String,
                         @Query("with_release_type") releaseType: String): Call<MovieRequest>

    @GET("movie/now_playing")
    fun getNowShowingMovies(@Query("api_key") apiKey: String,
                            @Query("language") language: String,
                            @Query("page") pageNumber: Int,
                            @Query("region") region:String,
                            @Query("with_release_type") releaseType: String): Call<MovieRequest>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String,
                          @Query("language") language: String,
                          @Query("page") pageNumber: Int,
                          @Query("region") region:String,
                          @Query("with_release_type") releaseType: String): Call<MovieRequest>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String,
                          @Query("language") language: String,
                          @Query("page") pageNumber: Int,
                          @Query("region") region:String,
                          @Query("with_release_type") releaseType: String): Call<MovieRequest>

    @GET("movie/{movieId}/recommendations")
    fun getRecommendedMovies(@Path("movieId") movieId: String,
                             @Query("api_key") apiKey: String,
                             @Query("language") language: String,
                             @Query("page") pageNumber: Int): Call<MovieRequest>

    @GET("search/movie")
    fun getSearchMovies(@Query("api_key") apiKey: String,
                        @Query("language") language: String,
                        @Query("query") query: String,
                        @Query("page") pageNumber: Int,
                        @Query("include_adult") adult: String,
                        @Query("region") region:String,
                        @Query("with_release_type") releaseType: String): Call<MovieRequest>

    @GET("movie/{movieId}")
    fun getDetailMovie(@Path("movieId") movieId: String,
                       @Query("api_key") apiKey: String,
                       @Query("append_to_response") response: String): Call<MovieDetail>

    @GET("movie/{id}/videos")
    fun getMovieVideos(@Path("id") id: Long, @Query("api_key") apiKey: String): Call<MovieVideosRequest>

    @GET("movie/{id}/reviews")
    fun getMovieReviews(@Path("id") id: Long, @Query("api_key") apiKey: String): Call<MovieReviewsRequest>

    @GET("movie/{id}/credits")
    fun getMovieCredits(@Path("id") id: Long, @Query("api_key") apiKey: String): Call<MovieCreditRequest>
}

fun getSearchMovies( service: NetworkService,
                     query: String,
                     page: Int,
                    onSuccess: (movierequest: MovieRequest) -> Unit,
                    onError: (error: String) -> Unit){
    Log.d(TAG, "query: $query, page: $page")

    Log.i("SearchInfo", query + " is the API")

    service.tmdbApi.getSearchMovies(TMDB_API_KEY, "en-US",
            query, page,"false","US|IN|UK",
            "2|3").enqueue(
            object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )
}

fun getNowShowingMovies( service: NetworkService,
                         language: String = "en-US",
                         page: Int,
                         region: String = "US",
                         onSuccess: (movierequest: MovieRequest) -> Unit,
                         onError: (error: String) -> Unit){

    service.tmdbApi.getNowShowingMovies(TMDB_API_KEY,language,
            page,region,"2|3").enqueue(
            object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )

}


fun getUpcomingMovies( service: NetworkService,
                         language: String = "en-US",
                         page: Int,
                         region: String = "US",
                         onSuccess: (movierequest: MovieRequest) -> Unit,
                         onError: (error: String) -> Unit){

    service.tmdbApi.getUpcomingMovies(TMDB_API_KEY,language,
            page,region,"2|3").enqueue(
            object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )

}

fun getTopRatedMovies( service: NetworkService,
                         language: String = "en-US",
                         page: Int,
                         region: String = "US",
                         onSuccess: (movierequest: MovieRequest) -> Unit,
                         onError: (error: String) -> Unit){

    service.tmdbApi.getTopRatedMovies(TMDB_API_KEY,language,
            page,region,"2|3").enqueue(
            object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )

}

fun getPopularMovies( service: NetworkService,
                       language: String = "en-US",
                       page: Int,
                       region: String = "US",
                       onSuccess: (movierequest: MovieRequest) -> Unit,
                       onError: (error: String) -> Unit){

    service.tmdbApi.getPopularMovies(TMDB_API_KEY,language,
            page,region,"2|3").enqueue(
            object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )

}

fun getRecommendedMovies( service: NetworkService,
                          movieId: String,
                      language: String = "en-US",
                      page: Int,
                      onSuccess: (movierequest: MovieRequest) -> Unit,
                      onError: (error: String) -> Unit){

    service.tmdbApi.getRecommendedMovies(movieId,TMDB_API_KEY,language,page).
            enqueue( object : Callback<MovieRequest> {
                override fun onFailure(call: Call<MovieRequest>?, t: Throwable) {
                    Log.d(TAG, "fail to get data")
                    onError(t.message ?: "unknown error")
                }

                override fun onResponse(
                        call: Call<MovieRequest>?,
                        response: Response<MovieRequest>
                ) {
                    Log.d(TAG, "got a response $response")
                    if (response.isSuccessful) {
                        val movierequest = response.body() ?: MovieRequest()
                        onSuccess(movierequest)
                    } else {
                        onError(response.errorBody()?.string() ?: "Unknown error")
                    }
                }
            }
    )

}
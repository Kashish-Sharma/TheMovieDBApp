package kashish.com.network

import kashish.com.models.MovieDetail
import kashish.com.requestmodels.MovieCreditRequest
import kashish.com.requestmodels.MovieRequest
import kashish.com.requestmodels.MovieReviewsRequest
import kashish.com.requestmodels.MovieVideosRequest
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Kashish on 12-08-2018.
 */
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

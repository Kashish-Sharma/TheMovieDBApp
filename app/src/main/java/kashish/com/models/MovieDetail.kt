package kashish.com.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kashish.com.requestmodels.MovieVideosRequest

/**
 * Created by Kashish on 02-08-2018.
 */

class MovieDetail() {

    @SerializedName("homepage")
    @Expose
    var homePage: String? = null
    @SerializedName("imdb_id")
    @Expose
    var imdbId: String = ""
    @SerializedName("revenue")
    @Expose
    var revenue: Int = 0
    @SerializedName("runtime")
    @Expose
    var runtime: Int = 0
    @SerializedName("status")
    @Expose
    var releaseStatus: String? = null
    @SerializedName("tagline")
    @Expose
    var tagLine: String? = null
    @SerializedName("budget")
    @Expose
    var budget: Int = 0
    @SerializedName("videos")
    @Expose()
    var videosResult: MovieVideosRequest? = null

}
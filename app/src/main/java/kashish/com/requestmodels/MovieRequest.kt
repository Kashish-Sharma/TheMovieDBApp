package kashish.com.requestmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kashish.com.models.Movie

/**
 * Created by Kashish on 12-08-2018.
 */
class MovieRequest {
    @SerializedName("total_pages")
    @Expose
    var totalPages: Int = 0
    @SerializedName("page")
    @Expose
    var page: Int = 0
    @SerializedName("results")
    @Expose
    var results: List<Movie>? = null
}
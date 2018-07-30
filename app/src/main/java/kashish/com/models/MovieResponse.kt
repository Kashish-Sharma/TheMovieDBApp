package kashish.com.models

/**
 * Created by Kashish on 31-07-2018.
 */
class MovieResponse(var page: Int?, var totalResults: Int?, var totalPages: Int?, results: List<Result>) {
    var results: List<Result>? = results
}
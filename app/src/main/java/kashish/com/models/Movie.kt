package kashish.com.models

import android.os.Parcel
import android.os.Parcelable.Creator
import android.os.Parcelable



/**
 * Created by Kashish on 31-07-2018.
 */
class Movie{

    var voteCount: Int? = null
    var id: Int? = null
    var video: Boolean? = null
    var voteAverage: Float? = null
    var title: String? = null
    var popularity: Float? = null
    var posterPath: String? = null
    var originalLanguage: String? = null
    var originalTitle: String? = null
    var genreIds: List<Int>? = null
    var backdropPath: String? = null
    var adult: Boolean? = null
    var overview: String? = null
    var releaseDate: String? = null
    var contentType: Int? = null


    constructor() {}

    constructor(voteCount: Int?, id: Int?, video: Boolean?,
                voteAverage: Float?, title: String, popularity: Float?,
                posterPath: String, originalLanguage: String,
                originalTitle: String, genreIds: List<Int>,
                backdropPath: String, adult: Boolean?,
                overview: String, releaseDate: String, contentType: Int) : super() {
        this.voteCount = voteCount
        this.id = id
        this.video = video
        this.voteAverage = voteAverage
        this.title = title
        this.popularity = popularity
        this.posterPath = posterPath
        this.originalLanguage = originalLanguage
        this.originalTitle = originalTitle
        this.genreIds = genreIds
        this.backdropPath = backdropPath
        this.adult = adult
        this.overview = overview
        this.releaseDate = releaseDate
        this.contentType = contentType
    }
}
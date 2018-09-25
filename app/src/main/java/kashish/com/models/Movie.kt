package kashish.com.models

import android.os.Parcel
import android.os.Parcelable.Creator
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kashish.com.utils.Constants


/**
 * Created by Kashish on 31-07-2018.
 */
class Movie() : Parcelable {

    @SerializedName("vote_count")
    @Expose
    var voteCount: Int? = null
    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("video")
    @Expose
    var video: Boolean? = null
    @SerializedName("vote_average")
    @Expose
    var voteAverage: Float? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("popularity")
    @Expose
    var popularity: Float? = null
    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null
    @SerializedName("original_language")
    @Expose
    var originalLanguage: String? = null
    @SerializedName("original_title")
    @Expose
    var originalTitle: String? = null
    @SerializedName("genre_ids")
    @Expose
    var genreIds: List<Int>? = null
    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null
    @SerializedName("adult")
    @Expose
    var adult: Boolean? = null
    @SerializedName("overview")
    @Expose
    var overview: String? = null
    @SerializedName("release_date")
    @Expose
    var releaseDate: String? = null
    @Expose
    var genreString: String = ""
    var contentType: Int = Constants.CONTENT_MOVIE
    var tableName: Int? = null

    constructor(parcel: Parcel) : this() {
        voteCount = parcel.readValue(Int::class.java.classLoader) as? Int
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        video = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        voteAverage = parcel.readValue(Float::class.java.classLoader) as? Float
        title = parcel.readString()
        popularity = parcel.readValue(Float::class.java.classLoader) as? Float
        posterPath = parcel.readString()
        originalLanguage = parcel.readString()
        originalTitle = parcel.readString()
        backdropPath = parcel.readString()
        adult = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        overview = parcel.readString()
        releaseDate = parcel.readString()
        genreString = parcel.readString()
        contentType = parcel.readInt()
        tableName = parcel.readValue(Int::class.java.classLoader) as? Int
    }


    constructor(voteCount: Int?, id: Int?, video: Boolean?,
                voteAverage: Float?, title: String, popularity: Float?,
                posterPath: String, originalLanguage: String,
                originalTitle: String, genreIds: List<Int>,
                backdropPath: String, adult: Boolean?,
                overview: String, releaseDate: String,
                genreString: String, contentType: Int) : this() {
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
        this.genreString = genreString
        this.contentType = contentType
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(voteCount)
        parcel.writeValue(id)
        parcel.writeValue(video)
        parcel.writeValue(voteAverage)
        parcel.writeString(title)
        parcel.writeValue(popularity)
        parcel.writeString(posterPath)
        parcel.writeString(originalLanguage)
        parcel.writeString(originalTitle)
        parcel.writeString(backdropPath)
        parcel.writeValue(adult)
        parcel.writeString(overview)
        parcel.writeString(releaseDate)
        parcel.writeString(genreString)
        parcel.writeInt(contentType)
        parcel.writeValue(tableName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<Movie> {
        override fun createFromParcel(parcel: Parcel): Movie {
            return Movie(parcel)
        }

        override fun newArray(size: Int): Array<Movie?> {
            return arrayOfNulls(size)
        }
    }


}
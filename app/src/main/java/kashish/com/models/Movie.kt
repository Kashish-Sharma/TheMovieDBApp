package kashish.com.models

import android.os.Parcel
import android.os.Parcelable.Creator
import android.os.Parcelable



/**
 * Created by Kashish on 31-07-2018.
 */
class Movie(): Parcelable {

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
    var totalPages: Int? = null
    var genreString: String? = ""

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
        contentType = parcel.readValue(Int::class.java.classLoader) as? Int
        totalPages = parcel.readValue(Int::class.java.classLoader) as? Int
        genreString = parcel.readString()
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
        parcel.writeValue(contentType)
        parcel.writeValue(totalPages)
        parcel.writeString(genreString)
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
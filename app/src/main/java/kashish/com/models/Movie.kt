package kashish.com.models

import android.os.Parcel
import android.os.Parcelable.Creator
import android.os.Parcelable



/**
 * Created by Kashish on 31-07-2018.
 */
class Movie : Parcelable {


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

    /**
     * No args constructor for use in serialization
     *
     */
    constructor() {}

    /**
     *
     * @param genreIds
     * @param id
     * @param title
     * @param releaseDate
     * @param overview
     * @param posterPath
     * @param originalTitle
     * @param voteAverage
     * @param originalLanguage
     * @param adult
     * @param backdropPath
     * @param voteCount
     * @param video
     * @param popularity
     */
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

    protected constructor(`in`: Parcel) {
        if (`in`.readByte().toInt() == 0) {
            voteCount = null
        } else {
            voteCount = `in`.readInt()
        }
        if (`in`.readByte().toInt() == 0) {
            contentType = null
        } else {
            contentType = `in`.readInt()
        }
        if (`in`.readByte().toInt() == 0) {
            id = null
        } else {
            id = `in`.readInt()
        }
        val tmpVideo = `in`.readByte()
        video = if (tmpVideo.toInt() == 0) null else tmpVideo.toInt() == 1
        if (`in`.readByte().toInt() == 0) {
            voteAverage = null
        } else {
            voteAverage = `in`.readFloat()
        }
        title = `in`.readString()
        if (`in`.readByte().toInt() == 0) {
            popularity = null
        } else {
            popularity = `in`.readFloat()
        }
        posterPath = `in`.readString()
        originalLanguage = `in`.readString()
        originalTitle = `in`.readString()
        backdropPath = `in`.readString()
        val tmpAdult = `in`.readByte()
        adult = if (tmpAdult.toInt() == 0) null else tmpAdult.toInt() == 1
        overview = `in`.readString()
        releaseDate = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        if (voteCount == null) {
            parcel.writeByte(0.toByte())
        } else {
            parcel.writeByte(1.toByte())
            parcel.writeInt(voteCount!!)
        }
        if (id == null) {
            parcel.writeByte(0.toByte())
        } else {
            parcel.writeByte(1.toByte())
            parcel.writeInt(id!!)
        }
        parcel.writeByte((if (video == null) 0 else if (video as Boolean) 1 else 2).toByte())
        if (voteAverage == null) {
            parcel.writeByte(0.toByte())
        } else {
            parcel.writeByte(1.toByte())
            parcel.writeFloat(voteAverage!!)
        }
        parcel.writeString(title)
        if (popularity == null) {
            parcel.writeByte(0.toByte())
        } else {
            parcel.writeByte(1.toByte())
            parcel.writeFloat(popularity!!)
        }
        parcel.writeString(posterPath)
        parcel.writeString(originalLanguage)
        parcel.writeString(originalTitle)
        parcel.writeString(backdropPath)
        parcel.writeByte((if (adult == null) 0 else if (adult as Boolean) 1 else 2).toByte())
        parcel.writeString(overview)
        parcel.writeString(releaseDate)
        parcel.writeInt(this.contentType!!)
    }

    companion object {

        val CREATOR: Creator<Movie> = object : Creator<Movie> {
            override fun createFromParcel(`in`: Parcel): Movie {
                return Movie(`in`)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}
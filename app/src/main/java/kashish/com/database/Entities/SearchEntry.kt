package kashish.com.database.Entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import kashish.com.utils.Constants.Companion.SEARCHES
import java.util.*

/**
 * Created by Kashish on 13-08-2018.
 */
@Entity(tableName = "search")
class SearchEntry {

    @PrimaryKey
    var movieId: Int? = null
    var voteCount: Int? = null
    var video: Boolean? = null
    var voteAverage: Float? = null
    var title: String? = null
    var popularity: Float? = null
    var posterPath: String? = null
    var originalLanguage: String? = null
    var originalTitle: String? = null
    var genreIds: String? = null
    var backdropPath: String? = null
    var adult: Boolean? = null
    var overview: String? = null
    var releaseDate: String? = null
    var contentType: Int? = null
    var totalPages: Int? = null
    var genreString: String? = ""
    var timeAdded: Date? = null
    var tableName: Int = SEARCHES
}
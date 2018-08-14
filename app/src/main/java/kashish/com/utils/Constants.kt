package kashish.com.utils


/**
 * Created by Kashish on 31-07-2018.
 */
class Constants {

    companion object {
        fun getGenre(id: Int): String {
            val genreMap = HashMap<Int,String>()
            genreMap.put(28, "Action")
            genreMap.put(12, "Adventure")
            genreMap.put(16, "Animation")
            genreMap.put(35, "Comedy")
            genreMap.put(80, "Crime")
            genreMap.put(99, "Documentary")
            genreMap.put(18, "Drama")
            genreMap.put(10751, "Family")
            genreMap.put(14, "Fantasy")
            genreMap.put(36, "History")
            genreMap.put(27, "Horror")
            genreMap.put(10402, "Music")
            genreMap.put(9648, "Mystery")
            genreMap.put(10749, "Romance")
            genreMap.put(878, "Science Fiction")
            genreMap.put(10770, "TV Movie")
            genreMap.put(53, "Thriller")
            genreMap.put(10752, "War")
            genreMap.put(37, "Western")

            return genreMap.get(id)!!
        }

        //upcoming movies json keys
        val RESULTS = "results"
        val VOTE_COUNT = "vote_count"
        val ID = "id"
        val VIDEO = "video"
        val VOTE_AVERAGE = "vote_average"
        val TITLE = "title"
        val POPULARITY = "popularity"
        val POSTER_PATH = "poster_path"
        val ORIGINAL_LANGUAGE = "original_language"
        val ORIGINAL_TITLE = "original_title"
        val GENRE_IDS = "genre_ids"
        val BACKDROP_PATH = "backdrop_path"
        val ADULT = "adult"
        val OVERVIEW = "overview"
        val RELEASE_DATE = "release_date"
        val TOTAL_PAGES = "total_pages"

        //cast
        val CAST = "cast"
        val CREW = "crew"

        val RANDOM_PATH = "asdfghjkl"

        //CONTENT_TYPES
        val CONTENT_MOVIE = 0
        val CONTENT_REVIEW = 1
        val CONTENT_DISCOVER = 2
        val CONTENT_SIMILAR = 3
        val CONTENT_PROGRESS = 777

        //TABLE CONTENT TYPES
        val CONTENT_FAVOURITE = 111

        //Shared preferences
        val USER_DEFAULT_REGION = "user_default_region"
        val USER_DEFAULT_REGION_STATUS = "user_default_region_status"

        //Constants
        val FAVOURITES: Int = 1231
        val TOP_RATED: Int = 1232
        val UPCOMING: Int = 1233
        val NOWSHOWING: Int = 1234
        val POPULAR: Int = 1235
        val SEARCHES: Int = 1236

    }

}
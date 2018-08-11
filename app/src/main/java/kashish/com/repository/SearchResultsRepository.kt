package kashish.com.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kashish.com.R
import kashish.com.adapters.MovieAdapter
import kashish.com.models.Movie
import kashish.com.singleton.VolleySingleton
import kashish.com.utils.Constants
import kashish.com.utils.Helpers
import org.json.JSONArray
import org.json.JSONObject


/**
 * Created by Kashish on 12-08-2018.
 */
class SearchResultsRepository(context: Context){


    val TAG:String = "SearchRepository"
    val mSharedPreferences: SharedPreferences
    val context: Context

    init {
        this.context = context
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    public fun getSearchResults(query:String, pageNumber:Int,
                                adult:String = "false",
                                mSearchAdapter: MovieAdapter):LiveData<MutableList<Movie>>{

        val data: MutableLiveData<MutableList<Movie>> = MutableLiveData()
        val searchData: MutableList<Movie> = mutableListOf()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,
                Helpers.buildSearchMovieUrl(query,pageNumber,"false"),
                null, Response.Listener { response ->


            val jsonArray: JSONArray = response.getJSONArray(Constants.RESULTS)

            if (jsonArray.length() == 0){
                //stop call to pagination in any case

                //show msg no posts
                //if(pageNumber == 1)
                //Toast.makeText(this,"Something went wrong", Toast.LENGTH_SHORT).show()
                searchData.removeAt(searchData.size - 1)
                mSearchAdapter.notifyItemRemoved(searchData.size-1)

            } else {

                //Data loaded, remove progress
                if (searchData.size > 0){
                    searchData.removeAt(searchData.size-1)
                    mSearchAdapter.notifyItemRemoved(searchData.size-1)
                }


                for (i in 0 until jsonArray.length()) {
                    val jresponse: JSONObject = jsonArray.getJSONObject(i)

                    val movie = Movie()

                    movie.totalPages = response.getInt(Constants.TOTAL_PAGES)
                    movie.voteCount = jresponse.getInt(Constants.VOTE_COUNT)
                    movie.id = jresponse.getInt(Constants.ID)
                    movie.video = jresponse.getBoolean(Constants.VIDEO)
                    movie.voteAverage = jresponse.getDouble(Constants.VOTE_AVERAGE).toFloat()
                    movie.title = jresponse.getString(Constants.TITLE)
                    movie.popularity = jresponse.getDouble(Constants.POPULARITY).toFloat()
                    movie.posterPath = jresponse.getString(Constants.POSTER_PATH)
                    movie.originalLanguage = jresponse.getString(Constants.ORIGINAL_LANGUAGE)
                    movie.originalTitle = jresponse.getString(Constants.ORIGINAL_TITLE)

                    val array: JSONArray = jresponse.getJSONArray(Constants.GENRE_IDS)
                    //val genreList: MutableList<Int> = mutableListOf()
                    for (j in 0 until array.length()) {
                        //genreList.add(array.getInt(j))
                        movie.genreString += Constants.getGenre(array.getInt(j)) + ", "
                    }

                    //movie.genreIds = genreList
                    movie.backdropPath = jresponse.getString(Constants.BACKDROP_PATH)
                    movie.adult = jresponse.getBoolean(Constants.ADULT)
                    movie.overview = jresponse.getString(Constants.OVERVIEW)
                    movie.releaseDate = jresponse.getString(Constants.RELEASE_DATE)
                    movie.contentType = Constants.CONTENT_SIMILAR

                    searchData.add(movie)
                }

                //addProgressBarInList()

                mSearchAdapter.notifyItemRangeInserted(searchData.size - jsonArray.length(), jsonArray.length())

            }

        }, Response.ErrorListener { error ->
            Log.i(TAG,error.message+" is the error message")
        })

        jsonObjectRequest.setShouldCache(mSharedPreferences.getBoolean(context.getString(R.string.pref_cache_data_key),true))
        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
        data.value = searchData
        return data
    }

}
package kashish.com.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

/**
 * Created by Kashish on 11-08-2018.
 */

@Dao
public interface MovieDao{

    @Query("SELECT * FROM favourites ORDER BY timeAdded")
    fun loadAllFavourites(): LiveData<List<MovieEntry>>

    @Query("SELECT * FROM favourites WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfFavourite(id: Int):Boolean

    @Insert
    fun insertFavourite(movieEntry: MovieEntry)

    @Delete
    fun deleteFavourite(movieEntry: MovieEntry)

}
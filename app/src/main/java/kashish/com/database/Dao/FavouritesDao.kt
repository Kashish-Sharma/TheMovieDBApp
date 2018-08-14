package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.util.Log
import kashish.com.database.Entities.FavouritesEntry

/**
 * Created by Kashish on 11-08-2018.
 */

@Dao
public interface FavouritesDao {

    @Query("SELECT * FROM favourites ORDER BY timeAdded DESC")
    fun loadAllFavourites(): LiveData<MutableList<FavouritesEntry>>

    @Query("SELECT * FROM favourites WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfFavourite(id: Int):Boolean

    @Insert
    fun insertFavourite(favouritesEntry: FavouritesEntry)

    @Delete
    fun deleteFavourite(favouritesEntry: FavouritesEntry)

}
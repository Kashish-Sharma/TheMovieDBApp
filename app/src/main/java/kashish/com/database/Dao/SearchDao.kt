package kashish.com.database.Dao

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.*
import kashish.com.database.Entities.SearchEntry

/**
 * Created by Kashish on 13-08-2018.
 */
@Dao
public interface SearchDao {

    @Query("SELECT * FROM search ORDER BY timeAdded ASC")
    fun loadAllSearch(): LiveData<MutableList<SearchEntry>>

    @Query("SELECT * FROM search WHERE movieId = :id ORDER BY timeAdded")
    fun checkIfSearch(id: Int):Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearch(searchEntry: SearchEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searches: List<SearchEntry>)

    @Delete
    fun deleteSearch(searchEntry: SearchEntry)

    @Query("SELECT * FROM search WHERE (title LIKE :queryString) ORDER BY timeAdded ASC")
    fun searchesByName(queryString: String): DataSource.Factory<Int, SearchEntry>

    @Query("DELETE FROM search")
    fun deleteAll()

    @Query("SELECT COUNT(movieId) FROM search")
    fun getNumberOfRows(): Int

}
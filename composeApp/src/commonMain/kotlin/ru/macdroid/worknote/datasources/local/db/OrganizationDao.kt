//package ru.macdroid.worknote.datasources.local.db
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface OrganizationDao {
//    @Query("SELECT * FROM organizations")
//    fun getAllOrganizations(): Flow<List<OrganizationEntity>>
//
//    @Query("SELECT * FROM organizations WHERE id = :id")
//    suspend fun getOrganizationById(id: Int): OrganizationEntity?
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrganization(organization: OrganizationEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertOrganizations(organizations: List<OrganizationEntity>)
//
//    @Query("DELETE FROM organizations")
//    suspend fun deleteAllOrganizations()
//}
//

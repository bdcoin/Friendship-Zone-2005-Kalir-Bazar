package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
  @Query("SELECT * FROM members ORDER BY memberId ASC")
  fun getAllMembers(): Flow<List<Member>>

  @Query("SELECT * FROM members WHERE memberId = :memberId LIMIT 1")
  fun getMemberByMemberId(memberId: String): Flow<Member?>

  @Query("SELECT * FROM members WHERE memberId = :memberId LIMIT 1")
  suspend fun getMemberByMemberIdSync(memberId: String): Member?

  @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
  fun getMemberById(id: Long): Flow<Member?>

  @Query("""
    SELECT * FROM members 
    WHERE (memberId = :identifier OR mobileNumber = :identifier) 
      AND loginPin = :pin 
    LIMIT 1
  """)
  suspend fun authenticateMember(identifier: String, pin: String): Member?

  @Query("SELECT COUNT(*) FROM members")
  fun getMemberCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.ABORT)
  suspend fun insertMember(member: Member): Long

  @Update
  suspend fun updateMember(member: Member)

  @Query("UPDATE members SET loginPin = :newPin WHERE memberId = :memberId")
  suspend fun updateLoginPin(memberId: String, newPin: String)

  @Delete
  suspend fun deleteMember(member: Member)

  @Query("SELECT memberId FROM members ORDER BY id DESC LIMIT 1")
  suspend fun getLastMemberId(): String?
}

package com.example.simplecallerid.db

import androidx.lifecycle.LiveData
import com.example.simplecallerid.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    suspend fun getUser(phoneNumber: String) = withContext(Dispatchers.Default) {
        val users = userDao.getUsersList()
        users.firstOrNull { it.hasPhone(phoneNumber) }
    }

    suspend fun insert(user: User) = userDao.insert(user)

    suspend fun update(user: User) = userDao.update(user)

    suspend fun delete(user: User) = userDao.delete(user)
}
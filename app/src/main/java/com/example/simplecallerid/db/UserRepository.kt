package com.example.simplecallerid.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.simplecallerid.models.User

class UserRepository(private val userDao: UserDao) {
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    @WorkerThread
    suspend fun insert(user: User) = userDao.insert(user)

    @WorkerThread
    suspend fun update(user: User) = userDao.update(user)

    @WorkerThread
    suspend fun delete(user: User) = userDao.delete(user)
}
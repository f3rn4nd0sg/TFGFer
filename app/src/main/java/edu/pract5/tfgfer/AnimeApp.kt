package edu.pract5.tfgfer

import android.app.Application
import edu.pract5.tfgfer.data.AnimeDatabase
import edu.pract5.tfgfer.data.LocalDataSource
import edu.pract5.tfgfer.data.RemoteDataSource
import edu.pract5.tfgfer.data.Repository

class AnimeApp : Application() {
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        val database = AnimeDatabase.getDatabase(this)
        val localDataSource = LocalDataSource(database)
        val remoteDataSource = RemoteDataSource()

        repository = Repository(remoteDataSource, localDataSource)
    }

    companion object {
        @JvmStatic
        fun getRepository(application: Application): Repository {
            return (application as AnimeApp).repository
        }
    }
}
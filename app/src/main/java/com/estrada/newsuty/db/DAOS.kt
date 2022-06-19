package com.estrada.newsuty.db

import androidx.room.*

@Dao
interface NewsDAO {
    @Query("SELECT * FROM news ORDER BY fechaPublicacion DESC")
    fun obtenerRecentNews(): List<News>

    @Query("SELECT * FROM news")
    fun obtenerNews(): List<News>

    @Query("SELECT * FROM news WHERE newsID = :newsID")
    fun obtenerNewsByID(newsID: Long): News

    @Query("SELECT * FROM news WHERE url = :url")
    fun obtenerNewsByURL(url: String): News

    @Query("SELECT * FROM news WHERE userCreatorUID = :userCreatorUID")
    fun obtenerNewsUsuario(userCreatorUID: String): List<News>

    @Insert
    fun insertarNews(vararg news: News)

    @Update
    fun actualizarNews(vararg news: News)

    @Delete
    fun borrarNews(vararg news: News)
}

@Dao
interface UserDAO {
    @Query("SELECT * FROM user WHERE userUID = :userUID")
    fun obtenerUser(userUID: String): User

    @Insert
    fun insertarUser(vararg users: User)

    @Update
    fun actualizarUser(vararg users: User)

    @Delete
    fun borrarUser(vararg users: User)
}

@Dao
interface VoteDAO {
    @Transaction
    @Query("SELECT * FROM news")
    fun getAllNewsVotes(): List<NewsWithVotes>

    @Transaction
    @Query("SELECT * FROM news WHERE newsID = :newsID")
    fun getNewsVotes(newsID: Long): NewsWithVotes

    @Query("SELECT * FROM newsvotes WHERE newsID = :newsID AND userUID = :userUID")
    fun userHasVote(newsID: Long, userUID: String): NewsVotes

    @Query("SELECT * FROM newsvotes WHERE newsID = :newsID AND esLike = 1")
    fun getNewsLikeVotes(newsID: Long): List<NewsVotes>

    @Query("SELECT * FROM newsvotes WHERE newsID = :newsID AND esLike = 0")
    fun getNewsDislikeVotes(newsID: Long): List<NewsVotes>

    @Query("SELECT * FROM user WHERE userUID = :userUID")
    fun obtenerVote(userUID: String): User

    @Insert
    fun insertarVote(vararg votes: NewsVotes)

    @Update
    fun actualizarVote(vararg votes: NewsVotes)

    @Delete
    fun borrarVote(vararg votes: NewsVotes)
}
package com.estrada.newsuty.db

import androidx.room.*

@Entity
data class News(
    @PrimaryKey(autoGenerate = true) val newsID: Long,
    @ColumnInfo val url: String?,
    @ColumnInfo val urlImagen: String,
    @ColumnInfo val titulo: String,
    @ColumnInfo val userCreatorUID: String,
    @ColumnInfo val spanish: Boolean,
    @ColumnInfo
    val fechaPublicacion: Long = System.currentTimeMillis(),
)

@Entity
data class User(
    @PrimaryKey val userUID: String,
    @ColumnInfo val email: String,
)

@Entity(primaryKeys = ["newsID", "userUID"])
data class NewsVotes(
    val newsID: Long,
    val userUID: String,
    var esLike: Boolean
)

data class NewsWithVotes(
    @Embedded
    val news: News,
    @Relation(
        parentColumn = "newsID",
        entity = User::class,
        entityColumn = "userUID",
        associateBy = Junction(
            value = NewsVotes::class,
            parentColumn = "newsID",
            entityColumn = "userUID"
        )
    )
    val users: List<User>
)

data class UserNews(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userCreatorUID"
    )
    val newss: List<News>
)
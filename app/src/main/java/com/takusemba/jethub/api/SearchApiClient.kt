package com.takusemba.jethub.api

import com.takusemba.jethub.api.response.ListResponse
import com.takusemba.jethub.api.response.RepositoryResponse
import com.takusemba.jethub.api.response.UserResponse
import com.takusemba.jethub.model.Language
import com.takusemba.jethub.model.Repository
import com.takusemba.jethub.model.User
import kotlinx.coroutines.Deferred
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

class SearchApiClient(retrofit: Retrofit) : SearchApi {

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  interface Service {

    @GET("search/repositories")
    fun getRepositories(
      @Query("q") query: String,
      @Query("sort") sort: String = "stars"
    ): Deferred<ListResponse<RepositoryResponse>>

    @GET("search/users")
    fun getUsers(
      @Query("q") query: String,
      @Query("sort") sort: String = "stars"
    ): Deferred<ListResponse<UserResponse>>
  }

  private val service = retrofit.create(Service::class.java)

  override suspend fun getHotRepositories(
    language: Language,
    from: LocalDateTime
  ): List<Repository> {
    return service.getRepositories("language:${language.name} created:>${from.format(formatter)}")
      .await()
      .items
      ?.map { response -> response.toModel() } ?: emptyList()
  }

  override suspend fun getHotUsers(
    language: Language,
    from: LocalDateTime
  ): List<User> {
    return service.getUsers("language:${language.name} created:>${from.format(formatter)}")
      .await()
      .items
      ?.map { response -> response.toModel() } ?: emptyList()
  }
}
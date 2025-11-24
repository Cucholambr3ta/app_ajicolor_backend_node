package com.example.appajicolorgrupo4.viewmodel

import com.example.appajicolorgrupo4.data.models.Post
import com.example.appajicolorgrupo4.data.repository.PostRepository
import com.example.appajicolorgrupo4.rules.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val postRepository: PostRepository = mockk()
    private lateinit var viewModel: PostViewModel

    @Test
    fun `fetchPosts updates state with posts when success`() = runTest {
        // Given
        val posts = listOf(Post(1, 1, "Title", "Body"))
        coEvery { postRepository.getAllPosts() } returns posts

        // When
        viewModel = PostViewModel(postRepository)

        // Then
        // Wait for coroutines to finish (since fetchAllPosts is called in init)
        // We can access the value directly from StateFlow
        assertEquals(posts, viewModel.posts.value)
        assertFalse(viewModel.isLoading.value)
    }
}

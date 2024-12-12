package com.khrlanamm.dicodingtales.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.khrlanamm.dicodingtales.DataDummy
import com.khrlanamm.dicodingtales.MainDispatcherRule
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity
import com.khrlanamm.dicodingtales.getOrAwaitValue
import com.khrlanamm.dicodingtales.helper.HomeAdapter
import com.khrlanamm.dicodingtales.helper.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val storyRepository: Repository = mockk()

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val pagingData = PagingData.from(dummyStories) // Data paging dummy
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = pagingData

        coEvery { storyRepository.getStoriesPagingWithMediator("token") } returns expectedStories

        val mainViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = mainViewModel.stories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val emptyPagingData = PagingData.from(emptyList<StoryEntity>())
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = emptyPagingData

        coEvery { storyRepository.getStoriesPagingWithMediator("token") } returns expectedStories

        val mainViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = mainViewModel.stories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = HomeAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}

        override fun onRemoved(position: Int, count: Int) {}

        override fun onMoved(fromPosition: Int, toPosition: Int) {}

        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}

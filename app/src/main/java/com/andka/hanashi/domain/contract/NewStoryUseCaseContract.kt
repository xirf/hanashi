package com.andka.hanashi.domain.contract

import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NewStoryUseCaseContract {
    operator fun invoke(file: File, description: String): Flow<ResultState<String>>
}
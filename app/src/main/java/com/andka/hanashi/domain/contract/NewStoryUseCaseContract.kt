package com.andka.hanashi.domain.contract

import com.andka.hanashi.utils.ResultState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NewStoryUseCaseContract {
    operator fun invoke(file: File, description: String, latLng: LatLng?): Flow<ResultState<String>>
}
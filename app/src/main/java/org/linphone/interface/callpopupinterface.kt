package org.linphone.`interface`

import org.linphone.model.callpopupjson
import retrofit2.Call as Retrofit2Call
import retrofit2.http.Body
import retrofit2.http.POST

interface callpopupinterface {
    @POST("callLog")
    fun calllogpost(
        @Body
        callpopupjson: callpopupjson
    ): Retrofit2Call<callpopupjson>
}

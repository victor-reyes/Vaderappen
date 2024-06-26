package nu.vaderappen.data.service.location

import com.squareup.moshi.Moshi
import nu.vaderappen.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {

    @GET("search")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("format") format: String = "geojson",
    ): Location


    @GET("reverse?format=geojson")
    suspend fun getReverseGeoCoding(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("zoom") zoom: Int = 15
    ): ReverseLocation.Location


    companion object {
        private const val BASE_URL = "https://nominatim.openstreetmap.org/"

        fun create(): LocationService {
            val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header(
                                "User-Agent",
                                "${BuildConfig.APPLICATION_ID} ${BuildConfig.VERSION_NAME}"
                            )
                            .build()
                    )
                }
                .build()


            val moshi = Moshi.Builder()
//                .add(
//                    PolymorphicJsonAdapterFactory.of(ReverseLocation::class.java, "type")
//                        .withSubtype(ReverseLocation.Location::class.java, "Success")
//                        .withSubtype(ReverseLocation.Error::class.java, "Error")
//                )
                .build()


            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(LocationService::class.java)
        }
    }

}
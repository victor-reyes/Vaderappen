package nu.vaderappen.data.service

import com.squareup.moshi.Moshi
import nu.vaderappen.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal
import java.math.RoundingMode

fun Double.format(digits: Int) = BigDecimal(this)
    .setScale(digits, RoundingMode.HALF_EVEN)
    .toDouble()

interface YrService {

    @GET("weatherapi/locationforecast/2.0/complete")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherData

    companion object {
        private const val BASE_URL = "https://api.met.no/"

        fun create(): YrService {
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
                .add(LocaleDateTimeAdapter())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    MoshiConverterFactory.create(moshi)
                        .failOnUnknown()
                )
                .build()

            return retrofit.create(YrService::class.java)
        }
    }
}


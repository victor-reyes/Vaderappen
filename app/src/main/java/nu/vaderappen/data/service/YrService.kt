package nu.vaderappen.data.service

import kotlinx.coroutines.runBlocking
import nu.vaderappen.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random


fun main() = runBlocking {
    val service = WeatherService.create()
    val r = Random

    val latitude = (r.nextDouble()  * 180 - 90).format(3);
    val longitude = (r.nextDouble()  * 360 - 180).format(3)
    val weatherData = service.getWeatherData(latitude, longitude)


    println(weatherData)


}

private  fun Double.format(digits: Int) = BigDecimal(this)
    .setScale(digits, RoundingMode.HALF_EVEN)
    .toDouble()

interface WeatherService {

    @GET("weatherapi/locationforecast/2.0/compact")
    suspend fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): WeatherData

    companion object {
        private const val BASE_URL = "https://api.met.no/"

        fun create(): WeatherService {
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


            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(
                    MoshiConverterFactory.create()
                        .failOnUnknown()
                )
                .build()

            return retrofit.create(WeatherService::class.java)
        }
    }
}

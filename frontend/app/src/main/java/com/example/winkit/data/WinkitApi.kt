package com.example.winkit.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// 1. Data class matches the Supabase table columns exactly
data class SupabasePolicyOffer(
    val rider_id: String,
    val weekly_premium: Double,
    val max_payout: Double,
    val civic_reason: String
)

// 2. The API Interface calling Supabase's auto-generated REST endpoint
interface SupabaseApiService {
    // We query the table and filter by rider_id using Supabase's "eq" syntax
    @GET("rest/v1/policy_offers")
    suspend fun getOfferForRider(
        @Query("rider_id") riderId: String = "eq.WKT-1001" 
    ): List<SupabasePolicyOffer> // Supabase returns a list, even for one row
}

// 3. The Retrofit Builder with your ANON KEY
object NetworkModule {
    private const val SUPABASE_URL = "https://YOUR-PROJECT-ID.supabase.co/"
    private const val SUPABASE_ANON_KEY = "your-public-anon-key"

    // Interceptor to inject the Supabase Auth headers automatically
    private val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("apikey", SUPABASE_ANON_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_ANON_KEY")
            .build()
        chain.proceed(request)
    }.build()

    val api: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}

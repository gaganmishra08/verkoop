package com.verkoopapp.network

import com.verkoopapp.VerkoopApplication
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

import java.util.concurrent.TimeUnit
import io.fabric.sdk.android.services.settings.IconRequest.build




 object ServiceGenerator{
    private val API_BASE_URL = "http://verkoopadmin.com/VerkoopApp/api/V1/"
//    private val API_BASE_URL = "http://mobile.serveo.net/verkoop/api/V1/"

    private val httpClient = OkHttpClient.Builder()
/*     OkHttpClient client = OkHttpClient.Builder()
     .certificatePinner(certificatePinner)
     .build();

     Request request = new Request.Builder()
     .url("https://" + hostname)
     .build();
     client.newCall(request).execute();*/
    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(buildClient())
            .addConverterFactory(GsonConverterFactory.create())

     var request = Request.Builder()
             .url(API_BASE_URL)
             .build()

    private var retrofit: Retrofit? = null

    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null, null)
    }


    fun <S> createService(
            serviceClass: Class<S>, username: String?, password: String?): S {
        if (!checkEmptyString(username) && !checkEmptyString(password)) {
            val authToken = Credentials.basic(username, password)
            return createService(serviceClass, authToken)
        }

        return createService(serviceClass, null, null)
    }

     fun <S> createService(
            serviceClass: Class<S>, authToken: String): S {
        if (!checkEmptyString(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)
            val logInterceptor = HttpLoggingInterceptor()
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(MyOkHttpInterceptor(authToken))
                httpClient.addInterceptor(logInterceptor)
                builder.client(httpClient.build())
//                buildClient().newCall(request).execute()
                retrofit = builder.build()
            }
        }

        return retrofit!!.create(serviceClass)
    }
     fun <S> createServiceWithoutToken(serviceClass: Class<S>): S {
         if (!checkEmptyString(VerkoopApplication.getToken())) {
             val interceptor = AuthenticationInterceptor(VerkoopApplication.getToken())
             val logInterceptor = HttpLoggingInterceptor()
             logInterceptor.level = HttpLoggingInterceptor.Level.BODY
             if (!httpClient.interceptors().contains(interceptor)) {
                 httpClient.addInterceptor(MyOkHttpInterceptor(VerkoopApplication.getToken()))
                 httpClient.addInterceptor(logInterceptor)
                 builder.client(httpClient.build())
//                buildClient().newCall(request).execute()
                 retrofit = builder.build()
             }
         }

         return retrofit!!.create(serviceClass)
     }
    private fun buildClient(): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

//        val certificatePinner =  CertificatePinner.Builder()
//                .add(API_BASE_URL,"sha256/3hsvkY3Xs8lhp2unKQfrmkJclV1koDNlO04oUsPD5OI=")
//                .add(API_BASE_URL,"sha256/E3tYcwo9CiqATmKtpMLW5V+pzIq+ZoDmpXSiJlXGmTo=")
//                .add(API_BASE_URL,"sha256/i7WTqTvh0OioIruIfFR4kMPnBqrS2rdiVPl/s2uC/CY=")
//                .add(API_BASE_URL,"sha1/G33dWlmoHgu4x4uHCysoJGoE/Dg=")
//                .add(API_BASE_URL,"sha256/bRvKO2gwj0x9ADWWpEdvneLG1aw1GJrLBnkTEJub7gU=")
//                .build()

        return OkHttpClient.Builder()
//                .certificatePinner(certificatePinner)
                .addInterceptor(logInterceptor)
                .connectTimeout(70000, TimeUnit.SECONDS)
                .readTimeout(70000, TimeUnit.SECONDS)
                .build()
    }

     class MyOkHttpInterceptor internal constructor(internal val tokenServer: String) : Interceptor {
         @Throws(IOException::class)
         override fun intercept(chain: Interceptor.Chain): Response {
             val originalRequest = chain.request()
             val token = tokenServer// get token logic
             val newRequest = originalRequest.newBuilder()
                     .header("Authorization", token)
                     .build()
             return chain.proceed(newRequest)
         }
     }

     fun checkEmptyString(string: String?): Boolean {
         return string == null || string.trim { it <= ' ' }.isEmpty()
     }
}
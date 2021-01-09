package com.verkoopapp.network

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.activity.WalkThroughActivity

import com.verkoopapp.models.*
import com.verkoopapp.utils.Utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*


class ServiceHelper {
    interface OnResponse {
        fun onSuccess(response: Response<*>)
        fun onFailure(msg: String?)
    }

    fun userSignUPService(request: SignUpRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userSignUpApi(request)
        responseCall.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                Log.e("TAG", "onResponse: "+request.country_code)
//                Log.e("TAG", "userSignUPService: "+request.currency)
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun userLoginService(request: LoginRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<LogInResponse> {
            override fun onResponse(call: Call<LogInResponse>, response: Response<LogInResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<LogInResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun socialLoginService(request: LoginSocialRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.userLoginApi(request)
        responseCall.enqueue(object : Callback<SocialGoogleResponse> {
            override fun onResponse(call: Call<SocialGoogleResponse>, response: Response<SocialGoogleResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SocialGoogleResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun categoriesService(onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getCategoriesService()
        responseCall.enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(call: Call<CategoriesResponse>, response: Response<CategoriesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun reportListService(type:Int,onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getReportList(type)
        responseCall.enqueue(object : Callback<ReportListResponse> {
            override fun onResponse(call: Call<ReportListResponse>, response: Response<ReportListResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ReportListResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun addItemsApi(request: AddItemRequest, onResponse: OnResponse) {
        //  val service = ServiceGenerator().createService(MyService::class.java, "idress", "idress")
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val parts = ArrayList<MultipartBody.Part>()
        for (i in 0 until request.imageList.size) {
            if (!TextUtils.isEmpty(request.imageList[i])) {
                val file = File(request.imageList[i])
                val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
                val body = MultipartBody.Part.createFormData("image[]", file.name, reqFile)
                parts.add(body)
            }
        }
        val call: Call<AddItemResponse>
        val categoryId = RequestBody.create(MediaType.parse("text/plain"), request.categoriesId)
        val name = RequestBody.create(MediaType.parse("text/plain"), request.name)
        val price = RequestBody.create(MediaType.parse("text/plain"), request.price)
        val countyid = RequestBody.create(MediaType.parse("text/plain"), request.country_code)
        val itemType = RequestBody.create(MediaType.parse("text/plain"), request.item_type)
        val description = RequestBody.create(MediaType.parse("text/plain"), request.description)
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val address = RequestBody.create(MediaType.parse("text/plain"), request.Address)
        val lat = RequestBody.create(MediaType.parse("text/plain"), request.Latitude)
        val label = RequestBody.create(MediaType.parse("text/plain"), request.label)
        val lng = RequestBody.create(MediaType.parse("text/plain"), request.Longitude)
        val meetUp = RequestBody.create(MediaType.parse("text/plain"), request.meet_up)
        val type = RequestBody.create(MediaType.parse("text/plain"), request.type.toString())
        val carBrandId = RequestBody.create(MediaType.parse("text/plain"), request.brand_id.toString())
        val carType = RequestBody.create(MediaType.parse("text/plain"), request.car_type_id.toString())
        val zoneId = RequestBody.create(MediaType.parse("text/plain"), request.zone_id.toString())
        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = prettyGson.toJson(request.additional_info)


        Log.e("<<stringRequest>>", prettyJson)
        Log.e("<<stringRequest>>", request.toString())
        val additionalInfo = RequestBody.create(okhttp3.MultipartBody.FORM, prettyJson)
        call = myService.addClothApi(parts, categoryId, name, price,countyid, itemType, description, userId, address, lat, label, lng, meetUp, type, carBrandId, carType, additionalInfo, zoneId)
        call.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.body() != null) {
                        onResponse.onFailure(response.body()!!.message)
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }

                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                //        Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun myProfileService(userId: String, onResponse: OnResponse) {
        //    val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)

        val responseCall = myService.getMyProfileService(userId)
        responseCall.enqueue(object : Callback<MyProfileResponse> {
            override fun onResponse(call: Call<MyProfileResponse>, response: Response<MyProfileResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<MyProfileResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }




    fun getPlacesService(placeSearchRequest: PlaceSearchRequest, onResponse: OnResponse) {
        val service = ApiClientLocation.client
        val responseCall = service.getDetails(placeSearchRequest.loc, placeSearchRequest.radius, placeSearchRequest.key)
        responseCall.enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getSearchPlaceService(placeSearchRequest: PlaceSearchRequest, onResponse: OnResponse) {
        val service = ApiClientLocation.client
        val responseCall = service.getSearchPlaceApi(placeSearchRequest.keyword, placeSearchRequest.key)
        responseCall.enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getItemDetailService(itemId: Int, userId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getItemDetailsApi(itemId, userId)
        responseCall.enqueue(object : Callback<ItemDetailsResponse> {
            override fun onResponse(call: Call<ItemDetailsResponse>, response: Response<ItemDetailsResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ItemDetailsResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getHomeDataService(userId: String, pageCount: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getItemsService()
        responseCall.enqueue(object : Callback<HomeDataResponse> {
            override fun onResponse(call: Call<HomeDataResponse>, response: Response<HomeDataResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCurrencyRateList(onResponse: OnResponse) {
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getCurrencies()
        responseCall.enqueue(object : Callback<CurrencyResponse> {
            override fun onResponse(call: Call<CurrencyResponse>, response: Response<CurrencyResponse>) {
                val res = response.body()
                if (response.code() == 401) {
                    doLogout()
                }
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    private fun doLogout() {
        Utils.clearPreferences(VerkoopApplication.instance.baseContext)
        val mStartActivity = Intent(VerkoopApplication.instance.baseContext, WalkThroughActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(VerkoopApplication.instance.baseContext, mPendingIntentId, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = VerkoopApplication.instance.baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    fun getItemsService(homeRequest: HomeRequest,userId: String, onResponse: OnResponse) {
        // val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
//        Log.e("TAG", "getItemsService: "+country_code)
//        Log.e("TAG", "getItemsService: "+pageCount)
        Log.e("TAG", "getItemsService: "+userId)
        Log.e("TAG", "getItemsService: "+homeRequest.type)
        val responseCall = myService.getHomeDataApi(homeRequest, userId)
        responseCall.enqueue(object : Callback<HomeDataResponse> {
            override fun onResponse(call: Call<HomeDataResponse>, response: Response<HomeDataResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<HomeDataResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getBuyCarService(homeRequest: HomeRequest,  userId: String,onResponse: OnResponse) {
        //val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getBuyCarDataApi(homeRequest, userId)
        responseCall.enqueue(object : Callback<BuyCarResponse> {
            override fun onResponse(call: Call<BuyCarResponse>, response: Response<BuyCarResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<BuyCarResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun likeService(lickedRequest: LickedRequest, onResponse: OnResponse) {
        //val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.likedApi(lickedRequest)
        responseCall.enqueue(object : Callback<LikedResponse> {
            override fun onResponse(call: Call<LikedResponse>, response: Response<LikedResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 201 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun disLikeService(lickedId: Int, onResponse: OnResponse) {
        //  val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.disLikedApi(lickedId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun categoryPostService(categoryPostRequest: FilterRequest, onResponse: OnResponse) {
        // val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.toString())
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.category_id)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.item_id)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.item_type)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.latitude)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.longitude)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.max_price)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.meet_up)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.min_price)
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.search)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.sort_no)
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.type)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.userId)

        try {

            val responseCall = myService.categoryPostApi(categoryPostRequest, categoryPostRequest.userId)
            responseCall.enqueue(object : Callback<CategoryPostResponse> {
                override fun onResponse(call: Call<CategoryPostResponse>, response: Response<CategoryPostResponse>) {
                    val res = response.body()
                    Log.e("TAG", "onResponse: " + res?.data?.items)
                    Log.e("TAG", "onResponse: " + res?.message)
                    Log.e("<<<Response>>>", Gson().toJson(res))
                    if (res != null) {
                        when {
                            response.code() == 200 -> onResponse.onSuccess(response)
                            else -> onResponse.onFailure(response.message())
                        }
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }
                }

                override fun onFailure(call: Call<CategoryPostResponse>, t: Throwable) {
                    onResponse.onFailure(t.message)
                    Log.e("TAG", "onFailure: "+t.message)
                }
            })
        }
        catch (exc: Exception){
            Log.e("TAG", "categoryPostService: "+exc.message)
        }
    }


    fun categoryPostService_android(categoryPostRequest: FilterRequest, onResponse: OnResponse) {
        // val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        Log.e("TAG", "categoryPostService_android: "+categoryPostRequest.toString())
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.category_id)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.item_id)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.item_type)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.latitude)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.longitude)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.max_price)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.meet_up)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.min_price)
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.search)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.sort_no)
        Log.e("TAG", "categoryPostService: "+categoryPostRequest.type)
//        Log.e("TAG", "categoryPostService: "+categoryPostRequest.userId)

        try {

            val responseCall = myService.categoryPostApi_android(categoryPostRequest, categoryPostRequest.userId)
            responseCall.enqueue(object : Callback<CategoryPostResponse> {
                override fun onResponse(call: Call<CategoryPostResponse>, response: Response<CategoryPostResponse>) {
                    val res = response.body()
                    Log.e("TAG", "onResponse: " + res?.data?.items)
                    Log.e("TAG", "onResponse: " + res?.message)
                    Log.e("<<<Response>>>", Gson().toJson(res))
                    if (res != null) {
                        when {
                            response.code() == 200 -> onResponse.onSuccess(response)
                            else -> onResponse.onFailure(response.message())
                        }
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }
                }

                override fun onFailure(call: Call<CategoryPostResponse>, t: Throwable) {
                    onResponse.onFailure(t.message)
                    Log.e("TAG", "onFailure: "+t.message)
                }
            })
        }
        catch (exc: Exception){
            Log.e("TAG", "categoryPostService: "+exc.message)
        }
    }

    fun updateCategoryService(updateCategoryRequest: UpdateCategoryRequest, onResponse: OnResponse) {
        //  val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.updateServiceApi(updateCategoryRequest)
        responseCall.enqueue(object : Callback<LikedResponse> {
            override fun onResponse(call: Call<LikedResponse>, response: Response<LikedResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<LikedResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getFavouritesService(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getFavouritesApi(userId)
        responseCall.enqueue(object : Callback<FavouritesResponse> {
            override fun onResponse(call: Call<FavouritesResponse>, response: Response<FavouritesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun updateProfileService(request: ProfileUpdateRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(request.profile_pic)) {
            val file = File(request.profile_pic)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("profile_pic", file.name, reqFile)
        }

        var call: Call<ProfileUpdateResponse>? = null
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val userName = RequestBody.create(MediaType.parse("text/plain"), request.username)
        val firstName = RequestBody.create(MediaType.parse("text/plain"), request.first_name)
        val lastName = RequestBody.create(MediaType.parse("text/plain"), request.last_name)
        val city = RequestBody.create(MediaType.parse("text/plain"), request.city)
        val state = RequestBody.create(MediaType.parse("text/plain"), request.state)
        val country = RequestBody.create(MediaType.parse("text/plain"), request.country)
        val countryCode = RequestBody.create(MediaType.parse("text/plain"), request.country_code)
        val cityId = RequestBody.create(MediaType.parse("text/plain"), request.City_id)
        val stateId = RequestBody.create(MediaType.parse("text/plain"), request.State_id)
        val countryId = RequestBody.create(MediaType.parse("text/plain"), request.country_id)
        val webSite = RequestBody.create(MediaType.parse("text/plain"), request.website)
        val bio = RequestBody.create(MediaType.parse("text/plain"), request.bio)
        val mobileNo = RequestBody.create(MediaType.parse("text/plain"), request.mobile_no)
        val gender = RequestBody.create(MediaType.parse("text/plain"), request.gender)
        val dob = RequestBody.create(MediaType.parse("text/plain"), request.DOB)

        call = if (body != null) {
            myService.editProfileWthImageApi(body, userId, userName, firstName, lastName, city, state, country,countryCode ,cityId, stateId, countryId, webSite, bio, mobileNo, gender, dob)
        } else {
            myService.editProfileWthOutImgApi(userId, userName, firstName, lastName, city, state, country, countryCode,cityId, stateId, countryId, webSite, bio, mobileNo, gender, dob)
        }

        call.enqueue(object : Callback<ProfileUpdateResponse> {
            override fun onResponse(call: Call<ProfileUpdateResponse>, response: Response<ProfileUpdateResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    onResponse.onFailure(response.body()!!.message)
                }
            }

            override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                //           Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getMyProfileInfoService(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getMyProfileApi(userId)
        responseCall.enqueue(object : Callback<MyProfileIngoResponse> {
            override fun onResponse(call: Call<MyProfileIngoResponse>, response: Response<MyProfileIngoResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<MyProfileIngoResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getPaymentCreditCard(makePaymentRequest: MakePaymentRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.stripepayment(makePaymentRequest)
        responseCall.enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {

                Log.e("TAG", "onResponse: "+response.errorBody())
                Log.e("TAG", "onResponse: "+response.message())
                Log.e("TAG", "onResponse: "+response.code())
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getActivityList(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getActivityList(userId)
        responseCall.enqueue(object : Callback<ActivityListResponseModel> {
            override fun onResponse(call: Call<ActivityListResponseModel>, response: Response<ActivityListResponseModel>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ActivityListResponseModel>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updatePasswordService(request: UpdatePasswordRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.updatePasswordApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun postCommentService(request: PostCommentRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.postCommentApi(request)
        responseCall.enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {

                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun deleteCommentService(commentId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.deleteCommentApi(commentId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun deleteBannerService(bannerId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.deleteBannerApi(bannerId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun renewBannerService(request:RenewBannerRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.renewBannerApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun reportAddedService(request: ReportUserRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.reportUserApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun followService(request: FollowRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.followApi(request)
        responseCall.enqueue(object : Callback<FollowResponse> {
            override fun onResponse(call: Call<FollowResponse>, response: Response<FollowResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<FollowResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun unFollowService(followId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.unFollowApi(followId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun blockUserService(request: BlockUserRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.blockUserApi(request)
        responseCall.enqueue(object : Callback<BlockUserResponse> {
            override fun onResponse(call: Call<BlockUserResponse>, response: Response<BlockUserResponse>) {
                if (response.code() == 201) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<BlockUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun unBlockUserService(followId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.unBlockUserApi(followId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun userProfileService(request: FollowRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.userProfileApi(request)
        responseCall.enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun markAsSoldService(item_id: Int, markAsSoldRequest: MarkAsSoldRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.markAsSoldApi(item_id, markAsSoldRequest)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun deleteListingService(itemId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.deleteListingApi(itemId)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun searchItemService(request: SearchItemRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.searchItemApi(request)
        responseCall.enqueue(object : Callback<SearchItemResponse> {
            override fun onResponse(call: Call<SearchItemResponse>, response: Response<SearchItemResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun searchByUserService(UserId: Int, request: SearchUserRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.searchByUserApi(UserId, request)
        responseCall.enqueue(object : Callback<SearchByUserResponse> {
            override fun onResponse(call: Call<SearchByUserResponse>, response: Response<SearchByUserResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchByUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getDeactivateAccountService(request: DeactivateAccountRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.deactivateAccount(request)
        responseCall.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun searchKeywordMultipleDataService(request: SearchKeywordMultipleDataRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.searchKeywordMultipleData(request)
        responseCall.enqueue(object : Callback<SearchMultipleKeywordResponse> {
            override fun onResponse(call: Call<SearchMultipleKeywordResponse>, response: Response<SearchMultipleKeywordResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchMultipleKeywordResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun forgotPasswordService(request: ForgotPasswordRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
//        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.forgotPasswordApi(request)
        responseCall.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun editItemsApi(request: EditItemRequest, onResponse: OnResponse) {
        //  val service = ServiceGenerator().createService(MyService::class.java, "idress", "idress")
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val parts = ArrayList<MultipartBody.Part>()
        for (i in 0 until request.imageList.size) {
            if (!TextUtils.isEmpty(request.imageList[i])) {
                val file = File(request.imageList[i])
                val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
                val body = MultipartBody.Part.createFormData("image[]", file.name, reqFile)
                parts.add(body)
            }
        }
        val call: Call<AddItemResponse>
        val categoryId = RequestBody.create(MediaType.parse("text/plain"), request.categoriesId)
        val name = RequestBody.create(MediaType.parse("text/plain"), request.name)
        val price = RequestBody.create(MediaType.parse("text/plain"), request.price)
        val countryid = RequestBody.create(MediaType.parse("text/plain"), request.country_code)
        val itemType = RequestBody.create(MediaType.parse("text/plain"), request.item_type)
        val description = RequestBody.create(MediaType.parse("text/plain"), request.description)
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id)
        val address = RequestBody.create(MediaType.parse("text/plain"), request.Address)
        val lat = RequestBody.create(MediaType.parse("text/plain"), request.Latitude)
        val lng = RequestBody.create(MediaType.parse("text/plain"), request.Longitude)
        val meetUp = RequestBody.create(MediaType.parse("text/plain"), request.meet_up)
        val itemId = RequestBody.create(MediaType.parse("text/plain"), request.item_id.toString())
        val deleteImageId = RequestBody.create(MediaType.parse("text/plain"), request.deleteImageList)

        val type = RequestBody.create(MediaType.parse("text/plain"), request.type.toString())
        val carBrandId = RequestBody.create(MediaType.parse("text/plain"), request.brand_id.toString())
        val carType = RequestBody.create(MediaType.parse("text/plain"), request.car_type_id.toString())
        val zoneId = RequestBody.create(MediaType.parse("text/plain"), request.zone_id.toString())

        val prettyGson = GsonBuilder().setPrettyPrinting().create()
        val prettyJson = prettyGson.toJson(request.additional_info)
        val additionalInfo = RequestBody.create(okhttp3.MultipartBody.FORM, prettyJson)
        if (request.imageList.size > 0) {
            call = myService.updateProductApi(parts, deleteImageId, itemId, categoryId, name, price, countryid,itemType, description, userId, address, lat, lng, meetUp, type, carBrandId, carType, additionalInfo, zoneId)
        } else {
            call = myService.updateWithoutImageApi(deleteImageId, itemId, categoryId, name, price, countryid,itemType, description, userId, address, lat, lng, meetUp, type, carBrandId, carType, additionalInfo, zoneId)
        }
        call.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                Log.e("<<<<Response>>>>", request.toString())
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.body() != null) {
                        onResponse.onFailure(response.body()!!.message)
                    } else {
                        onResponse.onFailure("Something went wrong!")
                    }

                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                //       Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getCarBrandService(comingFrom: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall: Call<CarBrandResponse>
        responseCall = myService.getCarBrandApi()
        responseCall.enqueue(object : Callback<CarBrandResponse> {
            override fun onResponse(call: Call<CarBrandResponse>, response: Response<CarBrandResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CarBrandResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCarFilterService(userId: Int, carFilterRequest: CarsFilterRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.carsFilterApi(userId, carFilterRequest)
        responseCall.enqueue(object : Callback<FavouritesResponse> {
            override fun onResponse(call: Call<FavouritesResponse>, response: Response<FavouritesResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun followFollowingService(UserId: Int, request: HomeRequestID, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.followFollowingApi(UserId, request)
        responseCall.enqueue(object : Callback<SearchByUserResponse> {
            override fun onResponse(call: Call<SearchByUserResponse>, response: Response<SearchByUserResponse>) {
                //  Log.e("<<<Response>>>", Gson().toJson(res))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<SearchByUserResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun addMoneyService(request: AddMoneyRequest, onResponse: OnResponse) {
        Log.e("TAG", "addMoneyService: "+request.amount)
        Log.e("TAG", "addMoneyService: "+request.currency)
        Log.e("TAG", "addMoneyService: "+request.token)
        Log.e("TAG", "addMoneyService: "+request.user_id)
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.addMoneyApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun sendMoneyService(request: SendMoneyRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.sendMoneyApi(request)
        responseCall.enqueue(object : Callback<TransferCoinResponse> {
            override fun onResponse(call: Call<TransferCoinResponse>, response: Response<TransferCoinResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<TransferCoinResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getWalletHistoryService(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getWalletHistoryApi(userId.toInt())
        responseCall.enqueue(object : Callback<WalletHistoryResponse> {
            override fun onResponse(call: Call<WalletHistoryResponse>, response: Response<WalletHistoryResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<WalletHistoryResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getLogOutService(request: LogOutRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.logOutApi(request)
        responseCall.enqueue(object : Callback<AddItemResponse> {
            override fun onResponse(call: Call<AddItemResponse>, response: Response<AddItemResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<AddItemResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCoinPlanService(userUd: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getCoinPlanApi(userUd)
        responseCall.enqueue(object : Callback<CoinPlanResponse> {
            override fun onResponse(call: Call<CoinPlanResponse>, response: Response<CoinPlanResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CoinPlanResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getAdvertisementPlanService(userId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getAdvertPlanApi(userId)
        responseCall.enqueue(object : Callback<AdvertPlanActivity> {
            override fun onResponse(call: Call<AdvertPlanActivity>, response: Response<AdvertPlanActivity>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<AdvertPlanActivity>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun purchaseCoinService(request: PurchaseCoinRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.coinPurchaseApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())

                            val messageE = JSONObject(messageError.getString("errors"))
                            val lessAmount = JSONArray(messageE.getString("less_amount"))
                            val value = lessAmount[0].toString()

                            // onResponse.onFailure(messageError.getString("message"))
                            onResponse.onFailure(value)
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getCoinHistoryService(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getCoinHistoryApi(userId.toInt())
        responseCall.enqueue(object : Callback<WalletHistoryResponse> {
            override fun onResponse(call: Call<WalletHistoryResponse>, response: Response<WalletHistoryResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<WalletHistoryResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updateBannerService(request: UploadBannerRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(request.banner)) {
            val file = File(request.banner)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("banner", file.name, reqFile)
        }
        val userId = RequestBody.create(MediaType.parse("text/plain"), request.user_id.toString())
        val advertPlan = RequestBody.create(MediaType.parse("text/plain"), request.advertisement_plan_id.toString())
        val categoryId = RequestBody.create(MediaType.parse("text/plain"), request.category_id.toString())

        val call = myService.uploadBannerApi(body!!, userId, advertPlan, categoryId)
        call.enqueue(object : Callback<ProfileUpdateResponse> {
            override fun onResponse(call: Call<ProfileUpdateResponse>, response: Response<ProfileUpdateResponse>) {

                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    try {
                        val messageError = JSONObject(response.errorBody()!!.string())
                        val messageE = JSONObject(messageError.getString("errors"))
                        val lessAmount = JSONArray(messageE.getString("less_coin"))
                        val value = lessAmount[0].toString()
                        onResponse.onFailure(value)
                    } catch (e: Exception) {
                    }
                }
            }

            override fun onFailure(call: Call<ProfileUpdateResponse>, t: Throwable) {
                //    Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getAlBannerService(userId: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getAllBannerApi(userId)
        responseCall.enqueue(object : Callback<ViewAllBannerResponse> {
            override fun onResponse(call: Call<ViewAllBannerResponse>, response: Response<ViewAllBannerResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<ViewAllBannerResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun sendQrService(request: SendQrCodeRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.qrCodeApi(request)
        responseCall.enqueue(object : Callback<DisLikeResponse> {
            override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            val messageE = JSONObject(messageError.getString("errors"))
                            val lessAmount = JSONArray(messageE.getString("less_coin"))
                            val value = lessAmount[0].toString()
                            onResponse.onFailure(value)
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun getAllUserInfoService(token: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getUserInfoApi(token)
        responseCall.enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun rateUserService(request: RateUserRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.rateUserApi(request)
        responseCall.enqueue(object : Callback<UpdateWalletResponse> {
            override fun onResponse(call: Call<UpdateWalletResponse>, response: Response<UpdateWalletResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateWalletResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun uploadImageService(imageUrl: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        var body: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(imageUrl)) {
            val file = File(imageUrl)
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), file)
            body = MultipartBody.Part.createFormData("chat_image", file.name, reqFile)
        }
        val call = myService.uploadImageApi(body!!)
        call.enqueue(object : Callback<ChatImageResponse> {
            override fun onResponse(call: Call<ChatImageResponse>, response: Response<ChatImageResponse>) {
                Log.e("<<<<Response>>>>", Gson().toJson(response.body()))
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<ChatImageResponse>, t: Throwable) {
                //   Log.d(LOG_TAG, "<<<Error>>>" + t.localizedMessage)
                onResponse.onFailure("Something went wrong!")

            }
        })
    }

    fun getBannerItemService(categoryId: String, userId: String, pageCount: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.getBannerDetailsApi(userId, categoryId)
        responseCall.enqueue(object : Callback<BannerDetailResponse> {
            override fun onResponse(call: Call<BannerDetailResponse>, response: Response<BannerDetailResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<BannerDetailResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun verifyMobileNo(request: VerifyNumberRequest, userId: Int, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.verifyMobileApi(userId, request)
        responseCall.enqueue(object : Callback<VerifyNumberResponse> {
            override fun onResponse(call: Call<VerifyNumberResponse>, response: Response<VerifyNumberResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<VerifyNumberResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun verifyOtpNo(request: VerifyOtpRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall = myService.verifyOtpApi(request)
        responseCall.enqueue(object : Callback<VerifyNumberResponse> {
            override fun onResponse(call: Call<VerifyNumberResponse>, response: Response<VerifyNumberResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<VerifyNumberResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }


    fun getMyRatingService(comingFrom: Int, userId: Int, token: String, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java, token)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
        val responseCall: Call<MyRatingResponse>
        if (comingFrom == 1) {
            responseCall = myService.getMyRatingGoodApi(userId)
        } else if (comingFrom == 2) {
            responseCall = myService.getMyRatingBadApi(userId)
        } else {
            responseCall = myService.getMyRatingPoorApi(userId)
        }
        //   responseCall = myService.getMyRatingGoodApi(userId)
        responseCall.enqueue(object : Callback<MyRatingResponse> {
            override fun onResponse(call: Call<MyRatingResponse>, response: Response<MyRatingResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<MyRatingResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun updateDeviceInfo(request: UpdateDeviceInfoRequest, onResponse: OnResponse) {
//        val myService = ApiClient.getClient().create(MyService::class.java)
        if (VerkoopApplication.getToken().isNotEmpty()) {
            val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)
            val responseCall = myService.updateDeviceInfoApi(request)
            responseCall.enqueue(object : Callback<DisLikeResponse> {
                override fun onResponse(call: Call<DisLikeResponse>, response: Response<DisLikeResponse>) {
                    if (response.code() == 200) {
                        onResponse.onSuccess(response)
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                val messageError = JSONObject(response.errorBody()!!.string())
                                onResponse.onFailure(messageError.getString("message"))
                            } catch (e: JSONException) {
                                onResponse.onFailure("Something went wrong")
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } else {
                            onResponse.onFailure("Something went wrong")
                        }
                    }
                }

                override fun onFailure(call: Call<DisLikeResponse>, t: Throwable) {
                    onResponse.onFailure(t.message)
                }
            })
        }
    }


    fun updateCountry(request: UpdateCountryRequest, onResponse: OnResponse) {
        val myService = ApiClient.getClient().create(MyService::class.java)
        val responseCall = myService.updateCountryApi(request)
        responseCall.enqueue(object : Callback<UpdateCountryResponse> {
            override fun onResponse(call: Call<UpdateCountryResponse>, response: Response<UpdateCountryResponse>) {
                if (response.code() == 200) {
                    onResponse.onSuccess(response)
                } else {
                    if (response.errorBody() != null) {
                        try {
                            val messageError = JSONObject(response.errorBody()!!.string())
                            onResponse.onFailure(messageError.getString("message"))
                        } catch (e: JSONException) {
                            onResponse.onFailure("Something went wrong")
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        onResponse.onFailure("Something went wrong")
                    }
                }
            }

            override fun onFailure(call: Call<UpdateCountryResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }




    fun myStateList(userId: String, onResponse: OnResponse) {
        //    val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)

        val responseCall = myService.getStateApi(userId)
        responseCall.enqueue(object : Callback<StateResponse> {
            override fun onResponse(call: Call<StateResponse>, response: Response<StateResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<StateResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

    fun myCityList(userId: Int, onResponse: OnResponse) {
        //    val myService = ApiClient.getClient().create(MyService::class.java)
        val myService = ServiceGenerator.createServiceWithoutToken(MyService::class.java)

        val responseCall = myService.getCityApi(userId)
        responseCall.enqueue(object : Callback<CityResponse> {
            override fun onResponse(call: Call<CityResponse>, response: Response<CityResponse>) {
                val res = response.body()
                Log.e("<<<Response>>>", Gson().toJson(res))
                if (res != null) {
                    when {
                        response.code() == 200 -> onResponse.onSuccess(response)
                        else -> onResponse.onFailure(response.message())
                    }
                } else {
                    onResponse.onFailure("Something went wrong!")
                }
            }

            override fun onFailure(call: Call<CityResponse>, t: Throwable) {
                onResponse.onFailure(t.message)
            }
        })
    }

}
package com.verkoopapp.network

import com.verkoopapp.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface MyService {

    @POST("user/register")
    fun userSignUpApi(@Body request: SignUpRequest): Call<SignUpResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginRequest): Call<LogInResponse>

    @POST("user/login")
    fun userLoginApi(@Body request: LoginSocialRequest): Call<SocialGoogleResponse>

    @POST("user/changePassword")
    fun updatePasswordApi(@Body request: UpdatePasswordRequest): Call<DisLikeResponse>

    @GET("categories")
    fun getCategoriesService(): Call<CategoriesResponse>

    @GET("user/profile/{userId}")
    fun getMyProfileService(@Path(value = "userId", encoded = true) fullUrl: String): Call<MyProfileResponse>

    @GET("item_details/{itemId}/{userId}")
    fun getItemDetailsApi(@Path(value = "itemId", encoded = true) fullUrl: Int, @Path(value = "userId", encoded = true) userId: Int): Call<ItemDetailsResponse>

    @GET("getUserFavouriteData/{userId}")
    fun getFavouritesApi(@Path(value = "userId", encoded = true) fullUrl: String): Call<FavouritesResponse>

    @GET("items")
    fun getItemsService(): Call<HomeDataResponse>

    @POST("likes")
    fun likedApi(@Body request: LickedRequest): Call<LikedResponse>

    @DELETE("likes/{licked_id}")
    fun disLikedApi(@Path(value = "licked_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @GET("getReportList/{type}")
    fun getReportList(@Path(value = "type", encoded = true) type: Int): Call<ReportListResponse>

    @POST("categoryFilterData/{user_id}")
    fun categoryPostApi(@Body request: FilterRequest, @Path(value = "user_id", encoded = true) userId: String): Call<CategoryPostResponse>


    @POST("categoryFilterData_android/{user_id}")
    fun categoryPostApi_android(@Body request: FilterRequest, @Path(value = "user_id", encoded = true) userId: String): Call<CategoryPostResponse>

    @POST("user/selectedUserCategroy")
    fun updateServiceApi(@Body request: UpdateCategoryRequest): Call<LikedResponse>

    @POST("dashboard/{userId}")
    fun getHomeDataApi(@Body request: HomeRequest, @Path(value = "userId", encoded = true) fullUrl: String): Call<HomeDataResponse>

    @POST("dashboard/{userId}")
    fun getBuyCarDataApi(@Body request: HomeRequest, @Path(value = "userId", encoded = true) fullUrl: String): Call<BuyCarResponse>

    @GET("nearbysearch/json")
    fun getDetails(@Query("location") loc: String,
                   @Query("radius") radius: String,
                   @Query("key") key: String): Call<PlaceApiResponse>

    @GET("textsearch/json")
    fun getSearchPlaceApi(@Query("query") keyWord: String,
                          @Query("key") key: String): Call<PlaceApiResponse>

    @GET("get_activity_list/{userId}")
    fun getActivityList(@Path(value = "userId", encoded = true) fullUrl: String): Call<ActivityListResponseModel>

    @GET("user/profileData/{userId}")
    fun getMyProfileApi(@Path(value = "userId", encoded = true) fullUrl: String): Call<MyProfileIngoResponse>

    @POST("comments")
    fun postCommentApi(@Body request: PostCommentRequest): Call<CommentResponse>

    @DELETE("comments/{comment_id}")
    fun deleteCommentApi(@Path(value = "comment_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @POST("reports")
    fun reportUserApi(@Body request: ReportUserRequest): Call<DisLikeResponse>

    @POST("follows")
    fun followApi(@Body request: FollowRequest): Call<FollowResponse>

    @DELETE("follows/{follow_id}")
    fun unFollowApi(@Path(value = "follow_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @POST("block_users")
    fun blockUserApi(@Body request: BlockUserRequest): Call<BlockUserResponse>

    @DELETE("block_users/{block_id}")
    fun unBlockUserApi(@Path(value = "block_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @POST("user/itemCreateProfileData")
    fun userProfileApi(@Body request: FollowRequest): Call<UserProfileResponse>

    @PUT("markAsSold/{item_id}")
    fun markAsSoldApi(@Path(value = "item_id", encoded = true) fullUrl: Int, @Body request: MarkAsSoldRequest): Call<DisLikeResponse>

    @DELETE("items/{item_id}")
    fun deleteListingApi(@Path(value = "item_id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @DELETE("userPurchaseAdvertisement/{id}")
    fun deleteBannerApi(@Path(value = "id", encoded = true) fullUrl: Int): Call<DisLikeResponse>

    @POST("searchKeywordData")
    fun searchItemApi(@Body request: SearchItemRequest): Call<SearchItemResponse>

    @POST("user/searchByUserName/{user_id}")
    fun searchByUserApi(@Path(value = "user_id", encoded = true) fullUrl: Int, @Body request: SearchUserRequest): Call<SearchByUserResponse>

    @POST("renew_advertisement")
    fun renewBannerApi(@Body request: RenewBannerRequest): Call<DisLikeResponse>

    @POST("searchKeywordMultipleData")
    fun searchKeywordMultipleData(@Body request: SearchKeywordMultipleDataRequest): Call<SearchMultipleKeywordResponse>

    @POST("user/forgot_password")
    fun forgotPasswordApi(@Body request: ForgotPasswordRequest): Call<AddItemResponse>

    @POST("user/logout")
    fun logOutApi(@Body request: LogOutRequest): Call<AddItemResponse>

    @POST("send_money")
    fun sendMoneyApi(@Body request: SendMoneyRequest): Call<TransferCoinResponse>

    @GET("getBrandWithModels")
    fun getCarBrandApi(): Call<CarBrandResponse>

    @GET("carsType")
    fun getCarTypeApi(): Call<CarBrandResponse>

    @PUT("carAndPropertyFilterData/{user_id}")
    fun carsFilterApi(@Path(value = "user_id", encoded = true) fullUrl: Int, @Body request: CarsFilterRequest): Call<FavouritesResponse>

    @PUT("getUserListFollow/{user_id}")
    fun followFollowingApi(@Path(value = "user_id", encoded = true) fullUrl: Int, @Body request: HomeRequestID): Call<SearchByUserResponse>

    @POST("payments")
    fun addMoneyApi(@Body request: AddMoneyRequest): Call<UpdateWalletResponse>

    @Headers("Accept: application/json")
    @POST("send_friendCoins")
    fun qrCodeApi(@Body request: SendQrCodeRequest): Call<DisLikeResponse>

    @GET("payments")
    fun getWalletHistoryApi(@Query(value = "user_id") userId: Int): Call<WalletHistoryResponse>

    @GET("user_coin")
    fun getCoinHistoryApi(@Query(value = "user_id") userId: Int): Call<WalletHistoryResponse>

    @GET("coin_plans")
    fun getCoinPlanApi(@Query(value = "user_id") userId: Int): Call<CoinPlanResponse>

    @GET("advertisment_plans")
    fun getAdvertPlanApi(@Query(value = "user_id") userId: Int): Call<AdvertPlanActivity>

    @Headers("Accept: application/json")
    @POST("user_coin")
    fun coinPurchaseApi(@Body request: PurchaseCoinRequest): Call<UpdateWalletResponse>

    @GET("userPurchaseAdvertisement")
    fun getAllBannerApi(@Query(value = "user_id") fullUrl: String): Call<ViewAllBannerResponse>

    @GET("user/friendInfo/{token}")
    fun getUserInfoApi(@Path(value = "token", encoded = true) fullUrl: String): Call<UserInfoResponse>

    @POST("ratings")
    fun rateUserApi(@Body request: RateUserRequest): Call<UpdateWalletResponse>

    @GET("bannerDetails/{userId}/{category_id}")
    fun getBannerDetailsApi(@Path(value = "userId", encoded = true) fullUrl: String, @Path(value = "category_id", encoded = true) categoryId: String): Call<BannerDetailResponse>

    @PUT("user/changePhoneNo/{user_id}")
    fun verifyMobileApi(@Path(value = "user_id", encoded = true) fullUrl: Int, @Body request: VerifyNumberRequest): Call<VerifyNumberResponse>

    @POST("user/mobileVerified")
    fun verifyOtpApi(@Body request: VerifyOtpRequest): Call<VerifyNumberResponse>

    @GET("listRatedUserGood/{user_id}")
    fun getMyRatingGoodApi(@Path(value = "user_id", encoded = true) fullUrl: Int): Call<MyRatingResponse>

    @GET("listRatedUserAverage/{user_id}")
    fun getMyRatingBadApi(@Path(value = "user_id", encoded = true) fullUrl: Int): Call<MyRatingResponse>

    @GET("listRatedUserBad/{user_id}")
    fun getMyRatingPoorApi(@Path(value = "user_id", encoded = true) fullUrl: Int): Call<MyRatingResponse>

    @GET("state_list/{country_id}")
    fun getStateApi(@Path(value = "country_id") fullUrl: String): Call<StateResponse>

    @GET("city_list/{state_id}")
    fun getCityApi(@Path(value = "state_id", encoded = true) fullUrl: Int): Call<CityResponse>

    @POST("user/updateDeviceInfo")
    fun updateDeviceInfoApi(@Body request: UpdateDeviceInfoRequest): Call<DisLikeResponse>

    @POST("user/updateCountry")
    fun updateCountryApi(@Body request: UpdateCountryRequest): Call<UpdateCountryResponse>


    @POST("user/authpay")
    fun stripepayment(@Body request: MakePaymentRequest): Call<PaymentResponse>




    @PUT("user/deactivate_account")
    fun deactivateAccount(@Body request: DeactivateAccountRequest): Call<AddItemResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("items")
    fun addClothApi(@Part files: List<MultipartBody.Part>,
                    @Part("category_id") categoryId: RequestBody,
                    @Part("name") name: RequestBody,
                    @Part("price") price: RequestBody,
                    @Part("country_code") countryid: RequestBody,
                    @Part("item_type") itemType: RequestBody,
                    @Part("description") description: RequestBody,
                    @Part("user_id") userId: RequestBody,
                    @Part("address") address: RequestBody,
                    @Part("latitude") lat: RequestBody,
                    @Part("label") label: RequestBody,
                    @Part("longitude") lng: RequestBody,
                    @Part("meet_up") meetUp: RequestBody,
                    @Part("type") type: RequestBody,
                    @Part("brand_id") brandId: RequestBody,
                    @Part("car_type_id") carTypeId: RequestBody,
                    @Part("additional_info") additionalInfo: RequestBody,
                    @Part("zone_id") zoneId: RequestBody
    ): Call<AddItemResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST("updateItem")
    fun updateProductApi(@Part files: List<MultipartBody.Part>,
                         @Part("image_remove_id") deleteList: RequestBody,
                         @Part("item_id") itemId: RequestBody,
                         @Part("category_id") categoryId: RequestBody,
                         @Part("name") name: RequestBody,
                         @Part("price") price: RequestBody,
                         @Part("country_code") countryid: RequestBody,
                         @Part("item_type") itemType: RequestBody,
                         @Part("description") description: RequestBody,
                         @Part("user_id") userId: RequestBody,
                         @Part("address") address: RequestBody,
                         @Part("latitude") lat: RequestBody,
                         @Part("longitude") lng: RequestBody,
                         @Part("meet_up") meetUp: RequestBody,
                         @Part("type") type: RequestBody,
                         @Part("brand_id") brandId: RequestBody,
                         @Part("car_type_id") carTypeId: RequestBody,
                         @Part("additional_info") additionalInfo: RequestBody,
                         @Part("zone_id") zoneId: RequestBody
    ): Call<AddItemResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("updateItem")
    fun updateWithoutImageApi(@Part("image_remove_id") deleteList: RequestBody,
                              @Part("item_id") itemId: RequestBody,
                              @Part("category_id") categoryId: RequestBody,
                              @Part("name") name: RequestBody,
                              @Part("price") price: RequestBody,
                              @Part("country_code") countryid: RequestBody,
                              @Part("item_type") itemType: RequestBody,
                              @Part("description") description: RequestBody,
                              @Part("user_id") userId: RequestBody,
                              @Part("address") address: RequestBody,
                              @Part("latitude") lat: RequestBody,
                              @Part("longitude") lng: RequestBody,
                              @Part("meet_up") meetUp: RequestBody,
                              @Part("type") type: RequestBody,
                              @Part("brand_id") brandId: RequestBody,
                              @Part("car_type_id") carTypeId: RequestBody,
                              @Part("additional_info") additionalInfo: RequestBody,
                              @Part("zone_id") zoneId: RequestBody
    ): Call<AddItemResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("user/profileUpdate")
    fun editProfileWthOutImgApi(@Part("user_id") userId: RequestBody,
                                @Part("username") userName: RequestBody,
                                @Part("first_name") firstName: RequestBody,
                                @Part("last_name") lastName: RequestBody,
                                @Part("city") city: RequestBody,
                                @Part("state") state: RequestBody,
                                @Part("country") country: RequestBody,
                                @Part("country_code") countryCode: RequestBody,
                                @Part("city_id") cityId: RequestBody,
                                @Part("state_id") stateId: RequestBody,
                                @Part("country_id") countryId: RequestBody,
                                @Part("website") webSite: RequestBody,
                                @Part("bio") bio: RequestBody,
                                @Part("mobile_no") mobileNo: RequestBody,
                                @Part("gender") gender: RequestBody,
                                @Part("DOB") dob: RequestBody): Call<ProfileUpdateResponse>


    @Multipart
    @Headers("Accept: application/json")
    @POST("user/profileUpdate")
    fun editProfileWthImageApi(@Part files: MultipartBody.Part,
                               @Part("user_id") userId: RequestBody,
                               @Part("username") userName: RequestBody,
                               @Part("first_name") firstName: RequestBody,
                               @Part("last_name") lastName: RequestBody,
                               @Part("city") city: RequestBody,
                               @Part("state") state: RequestBody,
                               @Part("country") country: RequestBody,
                               @Part("country_code") countryCode: RequestBody,
                               @Part("city_id") cityId: RequestBody,
                               @Part("state_id") stateId: RequestBody,
                               @Part("country_id") countryId: RequestBody,
                               @Part("website") webSite: RequestBody,
                               @Part("bio") bio: RequestBody,
                               @Part("mobile_no") mobileNo: RequestBody,
                               @Part("gender") gender: RequestBody,
                               @Part("DOB") dob: RequestBody): Call<ProfileUpdateResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("userPurchaseAdvertisement")
    fun uploadBannerApi(@Part files: MultipartBody.Part,
                        @Part("user_id") userId: RequestBody,
                        @Part("advertisement_plan_id") userName: RequestBody,
                        @Part("category_id") categoryId: RequestBody): Call<ProfileUpdateResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("chatImageUpload")
    fun uploadImageApi(@Part files: MultipartBody.Part): Call<ChatImageResponse>

    @GET("currencies")
    fun getCurrencies(): Call<CurrencyResponse>

}
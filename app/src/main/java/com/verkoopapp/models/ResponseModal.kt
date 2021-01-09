package com.verkoopapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CategoryModal(
        var itamName: String,
        var unselectedImage: Int,
        var selectedImage: Int,
        var selectedPosition: Boolean
) : Parcelable

@Parcelize
data class ImageModal(
        var imageUrl: String,
        var isSelected: Boolean,
        var type: Boolean,
        var countSelect: Int,
        var imagePosition: Int,
        var iseditable: Boolean,
        var imageId: Int

) : Parcelable

data class SignUpResponse(
        val message: String,
        val data: DataSignUp?
)

data class DataSignUp(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: Any,
        val last_name: Any,
        val login_type: String,
        val social_id: Any,
        val country: String,
        val is_active: Int,
        val is_use: Int,
        val created_at: CreatedAtSignUp,
        val token: String,
        val qrCode_image: String,
        val coin: Int = 0,
        val amount: Int = 0,
        val currency: String,
        val currency_symbol:String
)

data class CreatedAtSignUp(
        val date: String,
        val timezone_type: Int,
        val timezone: String
)

data class LogInResponse(
        val status: String,
        val status_code: Int,
        val message: String,
        val data: Data?
)

data class Data(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: Any,
        val last_name: Any,
        val login_type: String,
        val social_id: Any,
        val country: String,
        val is_active: Int,
        val is_use: Int,
        val created_at: CreatedAt,
        val token: String,
        val mobile_no: String,
        val qrCode_image: String,
        val coin: Int,
        val amount: Float,
        val currency: String,
        val currency_symbol:String
)

@Parcelize
data class CreatedAt(
        val date: String,
        val timezone_type: Int,
        val timezone: String
) : Parcelable

data class CategoriesResponse(
        val data: ArrayList<DataCategory>?,
        val message: String
)

@Parcelize
data class DataCategory(
        val id: Int,
        val name: String,
        val image: String,
        val parent_id: Int,
        val created_at: String,
        val updated_at: String,
        var isSelected: Boolean,
        val sub_category: ArrayList<SubCategory>
) : Parcelable

@Parcelize
data class SubCategory(
        val id: Int,
        val name: String,
        val image: String,
        val parent_id: Int,
        val created_at: String,
        val updated_at: String
) : Parcelable


data class AddItemResponse(
        val message: String
)

data class TransferCoinResponse(
        val message: String,
        val errors: TransferCoinError

)


data class TransferCoinError(
        val less_amount: ArrayList<String>
)

data class MyProfileResponse(
        val data: DataProfile?,
        val message: String
)

data class StateResponse(
        val data: StateData?,
        val message: String
)



data class StateData(
        val state: ArrayList<StateDataValue>

)

@Parcelize
data class StateDataValue(
        val id: Int,
        val name: String

): Parcelable

data class CityResponse(
        val data: CityData?,
        val message: String
)

data class PaymentResponse(
        val message: String,
        val status:String,
        val wallet: String
)

//data class PaymentData(
//
//)

data class CityData(
        val city: ArrayList<CityDataValue>
)


@Parcelize
data class CityDataValue(
        val id: Int,
        val name: String

): Parcelable


data class ReportListResponse(
        val data: ArrayList<ReportListData>,
        val message: String
)


@Parcelize
data class ReportListData(
        val created_at: String,
        val description: String,
        val id: Int,
        val name: String,
        val notes: String,
        val type: Int,
        val updated_at: String,
        var isSelected: Boolean
) : Parcelable

data class DataProfile(
        val id: Int,
        val username: String,
        val created_at: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: String,
        val state_id: String,
        val country_id: String,
        val follow_id: Int,
        val follow_count: Int,
        val follower_count: Int,
        val good: Int,
        val sad: Int,
        val average: Int,
        val items: ArrayList<ItemHome>
)

data class Item(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Double,
        val item_type: Int,
        val created_at: CreatedAtProfile,
        @SerializedName("items_like_count")
        var likes_count: Int,
        var like_id: Int,
        var is_like: Boolean,
        var is_sold: Int,
        val image_url: String
)

data class CreatedAtProfile(
        val date: String,
        val timezone_type: Int,
        val timezone: String
)


data class PlaceApiResponse(
        val html_attributions: List<Any>,
        val results: ArrayList<ResultLocation>,
        val status: String
)

data class ResultLocation(
        val geometry: Geometry,
        val icon: String,
        val id: String,
        val name: String,
        val opening_hours: OpeningHours,
        val photos: List<Photo>,
        val place_id: String,
        val plus_code: PlusCode,
        val rating: Double,
        val reference: String,
        val scope: String,
        val types: List<String>,
        val user_ratings_total: Int,
        val vicinity: String,
        val formatted_address: String
)

data class PlusCode(
        val compound_code: String,
        val global_code: String
)

data class OpeningHours(
        val open_now: Boolean
)

data class Geometry(
        val location: Location,
        val viewport: Viewport
)

data class Location(
        val lat: Double,
        val lng: Double
)

data class Viewport(
        val northeast: Northeast,
        val southwest: Southwest
)

data class Southwest(
        val lat: Double,
        val lng: Double
)

data class Northeast(
        val lat: Double,
        val lng: Double
)

data class Photo(
        val height: Int,
        val html_attributions: List<String>,
        val photo_reference: String,
        val width: Int
)


data class ItemDetailsResponse(
        val message: String,
        val data: DataItems?
)

@Parcelize
data class DataItems(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        var price: Double = 0.00,
        val currency: String,
        val item_type: Int,
        val description: String,
        val created_at: String,
        val updated_at: String,
        val items_like_count: Int,
        val is_like: Boolean,
        val like_id: Int,
        val username: String,
        val profile_pic: String,
        val category_name: String,
        val address: String?,
        val latitude: Double,
        val longitude: Double,
        val meet_up: Int,
        val is_sold: Int,
        val items_image: List<ItemsImage>,
        var comments: ArrayList<CommentModal>?,
        var reports: ArrayList<ReportResponse>?,
        var type: Int = 0,
        var brand_id: Int = 0,
        var car_type_id: Int = 0,
        var additional_info: AdditionalInfoResponse? = null,
        val zone_id: Int = 0,
        var offer_price: Double = 0.0,
        val chat_count: Int = 0,
        var make_offer: Boolean = false,
        var message_count: Int = 0

) : Parcelable

@Parcelize
data class AdditionalInfoResponse(
        val city: String? = null,
        val bathroom: Int,
        val bedroom: Int,
        val brand_name: String? = null,
        val car_brand_id: Int,
        val car_type: String? = null,
        val car_type_id: Int,
        val direct_owner: Int,
        val postal_code: String? = null,
        val registration_year: String? = null,
        val street_name: String? = null,
        val location: String? = null,
        val zoneId: Int,
        var min_price: Double = 0.0,
        var max_price: Double = 0.0,
        val property_type: String? = null,
        val parking_type: Int? = 0,
        val from_year: Int? = 0,
        val to_year: Int? = 0,
        val furnished: Int? = 0,
        val transmission_type: Int = 0,
        val model_id: Int = 0,
        val model_name: String? = null
) : Parcelable

@Parcelize
data class ReportResponse(
        val id: Int,
        val name: String,
        val description: String,
        val notes: String,
        val type: Int,
        val created_at: String,
        val updated_at: String,
        var isSelected: Boolean
) : Parcelable


@Parcelize
data class CommentModal(
        val username: String,
        val profile_pic: String,
        val id: Int,
        val user_id: Int,
        val comment: String,
        val created_at: String
) : Parcelable


@Parcelize
data class ItemsImage(
        val item_id: Int,
        val image_id: Int,
        val url: String
) : Parcelable


data class HomeDataResponse(
        val data: DataHome?,
        val message: String
)

data class CurrencyResponse(
        val data: DataCurrency?,
        val message: String
)

data class DataCurrency(
        val currency: ArrayList<Currency>
)

data class Currency(
        var code: String,
        var rate: String)

data class DataHome(
        val items: ArrayList<ItemHome>,
        val advertisments: ArrayList<Advertisment>?,
        val categories: ArrayList<Category>,
        val daily_pic: ArrayList<ItemHome>,
        val totalPage: Int
)

data class Advertisment(
        val id: Int,
        val name: String,
        val image: String,
        val user_id: String,
        val category_id: String
)

data class Category(
        val id: Int,
        val name: String,
        val parent_id: Int,
        val image: String
)

data class ItemHome(
        val id: Int = 0,
        val user_id: Int = 0,
        val category_id: Int = 0,
        val name: String? = null,
        val currency: String? = null,
        val price: Double = 0.0,
        val item_type: Int = 0,
        val created_at: CreatedAtHome? = null,
        var items_like_count: Int = 0,
        var like_id: Int = 0,
        var is_like: Boolean = false,
        var is_sold: Int = 0,
        val image_url: String? = null,
        val username: String? = null,
        val profile_pic: String? = null,
        var isClicked: Boolean = false,
        var isLoading: Boolean = false,
        var type: Int = 0
)

data class CreatedAtHome(
        val date: String,
        val timezone_type: Int,
        val timezone: String
)

data class LikedResponse(
        val message: String,
        val like_id: Int
)

data class DisLikeResponse(
        val message: String
)

data class UpdateCountryResponse(
        val message: String,
        val currency: String,
        val currency_symbol: String
)




data class SocialGoogleResponse(
        val data: DataGoogle?,
        val message: String
)

data class DataGoogle(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: String,
        val last_name: String,
        val login_type: String,
        val social_id: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: Int,
        val state_id: Int,
        val country_id: Int,
        val bio: String,
        val gender: String,
        val DOB: String,
        val profile_pic: String,
        val is_active: Int,
        val is_use: Int,
        val mobile_verified: Int,
        val created_at: CreatedAt,
        val token: String,
        val qrCode_image: String,
        val coin: Int,
        val amount: Float,
        val currency: String,
        val currency_symbol:String
)

data class SocialLoginResponse(
        val status_code: Int,
        val message: String,
        val data: DataSocial?
)

data class DataSocial(
        val id: Int,
        val username: String,
        val email: String,
        val first_name: String,
        val last_name: String,
        val mobile_no: String,
        val website: String,
        val bio: String,
        val profile_pic: String,
        val login_type: String,
        val social_id: String,
        val city: String,
        val country: String,
        val is_active: Int,
        val is_use: Int,
        val email_verified_at: Any,
        val created_at: String,
        val updated_at: String,
        val api_token: String
)

data class CategoryPostResponse(
        val data: DataPost,
        val message: String
)

data class DataPost(
        val subCategoryList: ArrayList<SubCategoryPost>,
        val items: ArrayList<ItemHome>
)

data class ItemData(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Int,
        val item_type: Int,
        val created_at: CreatedAt,
        val like_id: Int,
        val is_like: Boolean,
        val items_like_count: Int,
        val image_url: String,
        val username: String,
        val profile_pic: String
)

data class SubCategoryPost(
        val image: String,
        val name: String,
        val id: Int
)

data class FavouritesResponse(
        val data: FavouritesData,
        val message: String
)

class FavouritesData(
        val items: ArrayList<ItemHome>
)


/*data class Data(
    val id: Int,
    val user_id: Int,
    val category_id: Int,
    val name: String,
    val price: Int,
    val item_type: Int,
    val created_at: CreatedAt,
    val like_id: Int,
    val is_like: Boolean,
    val items_like_count: Int,
    val image_url: String,
    val username: String,
    val profile_pic: String
)*/

data class RegionResponse(
        val country: ArrayList<Country>
)

data class Country(
        val id: Int,
        val name: String,
        val states: ArrayList<State>
)

data class State(
        val id: Int,
        val name: String,
        var isSelected: Boolean,
        val cities: ArrayList<City>
)

@Parcelize
data class City(
        val id: Int,
        var isSelected: Boolean,
        val name: String
) : Parcelable


data class ProfileUpdateResponse(
        val data: DataProfileUpdate?,
        val message: String
)

data class DataProfileUpdate(
        val userId: Int,
        val email: String,
        val username: String,
        val first_name: String,
        val last_name: String,
        val login_type: String,
        val social_id: String,
        val country: String,
        val currency: String,
        val currency_symbol: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val bio: String,
        val gender: String,
        val DOB: String,
        val profile_pic: String,
        val is_active: Int,
        val is_use: Int,
        val mobile_verified: Int,
        val created_at: CreatedAt
)


data class MyProfileIngoResponse(
        val data: DataGetProfile?,
        val message: String
)

data class DataGetProfile(
        val id: Int,
        val username: String,
        val first_name: String,
        val last_name: String,
        val city: String,
        val state: String,
        val country: String,
        val country_code: String,
        val city_id: Int,
        val state_id: Int,
        val country_id: Int,
        val website: String,
        val bio: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val gender: String,
        val DOB: String
)

data class CommentResponse(
        val message: String,
        val data: CommentModal?
)

@Parcelize
data class DataComment(
        val id: Int,
        val comment: String,
        val created_at: CreatedAt,
        val username: String,
        val profile_pic: String
) : Parcelable


data class FollowResponse(
        val message: String,
        val data: DataFollow
)

data class DataFollow(
        val follower_id: String,
        val user_id: String,
        val updated_at: String,
        val created_at: String,
        val id: Int
)


data class UserProfileResponse(
        val data: DataUserProfile?,
        val message: String
)

data class DataUserProfile(
        val id: Int,
        val username: String,
        val created_at: String,
        val profile_pic: String,
        val email: String,
        val mobile_no: String,
        val website: String,
        val city: String,
        val state: String,
        val country: String,
        val city_id: Int,
        val state_id: Int,
        val country_id: Int,
        val follow_count: Int,
        var follower_count: Int = 0,
        var follower_id: Int = 0,
        var average: Int = 0,
        var sad: Int = 0,
        var good: Int = 0,
        val block_id: Int,
        val items: ArrayList<ItemHome>,
        val reports: ArrayList<ReportResponse>
)

data class ItemUserProfile(
        val id: Int,
        val user_id: Int,
        val category_id: Int,
        val name: String,
        val price: Double,
        val item_type: Int,
        val created_at: CreatedAt,
        var like_id: Int,
        var is_like: Boolean,
        var items_like_count: Int,
        val image_url: String,
        val username: String,
        val profile_pic: String,
        val is_sold: Int
)


@Parcelize
data class Report(
        val id: Int,
        val name: String,
        val description: String,
        val notes: String,
        val type: Int,
        val created_at: String,
        val updated_at: String
) : Parcelable

data class BlockUserResponse(
        val message: String,
        val data: DataBlock?
)

data class DataBlock(
        val user_block_id: String,
        val user_id: String,
        val updated_at: String,
        val created_at: String,
        val id: Int
)

data class SearchItemResponse(
        val message: String,
        val data: ArrayList<DataSearch>
)

data class DataSearch(
        val id: Int,
        val category_id: Int,
        val name: String,
        val category_name: String,
        val category: CategorySearch,
        val search: String
)

data class CategorySearch(
        val id: Int,
        val name: String,
        val parent_id: Int
)

data class SearchByUserResponse(
        val message: String,
        val data: ArrayList<DataUser>
)

data class DataUser(
        val id: Int,
        val username: String,
        val profile_pic: String,
        var follower_id: Int,
        var isClicked: Boolean
)

data class CarBrandResponse(
        val data: ArrayList<DataCarBrand>,
        val message: String
)

data class DataCarBrand(
        val id: Int,
        val name: String,
        val image: String? = null,
        val created_at: String? = null,
        val updated_at: String? = null,
        var isSelected: Boolean = false,
        val car_models: ArrayList<CarModelList>? = null
)

@Parcelize
data class CarModelList(
        var brand_id: Int,
        var created_at: String,
        var id: Int,
        var is_active: Int,
        var name: String,
        var updated_at: String,
        var isSelected: Boolean
) : Parcelable

data class BuyCarResponse(
        val data: DataCarResponse?,
        val message: String
)

data class DataCarResponse(
        val items: ArrayList<ItemHome>,
        val totalPage: Int,
        val brands: ArrayList<Brand>,
        val car_types: ArrayList<CarType>
)

data class CarType(
        val id: Int,
        val name: String,
        val image: String,
        val created_at: String? = null,
        val updated_at: String? = null
)

@Parcelize
data class Brand(
        val id: Int,
        val name: String,
        val image: String,
        val created_at: String? = null,
        val updated_at: String? = null
) : Parcelable


data class CoinPlanResponse(
        var data: ArrayList<CoinPlan>,
        var message: String,
        var coins: Int
)

data class CoinPlan(
        var id: Int,
        var amount: Double,
        var coin: Int,
        var currency: String,
        var is_active: Int,
        var created_at: String,
        var updated_at: String
)

data class AdvertPlanActivity(
        var data: ArrayList<DataAdvert>,
        var message: String
)

data class DataAdvert(
        var id: Int,
        var name: String,
        var day: Int,
        var coin: Int,
        var is_active: Int,
        var created_at: String,
        var updated_at: String
)

data class UpdateWalletResponse(
        var data: List<DataWalletAdd>,
        var amount: Int,
        var message: String
)

data class DataWalletAdd(
        var id: Int,
        var user_id: Int,
        var token: String,
        var amount: Int,
        var status: Int,
        var created_at: String,
        var updated_at: String
)

data class WalletHistoryResponse(
        var data: ArrayList<DataHistory>?,
        var amount: Float,
        var message: String
)

data class DataHistory(
        var id: Int,
        var type: Int,
        var user_id: Int,
        var token: String,
        var currency: String,
        var userName: String,
        var profilePic: String,
        var amount: Float,
        var status: Int,
        var coin_plan_id: Int,
        var is_active: Int,
        var coin: Int,
        var created_at: String,
        var updated_at: String
)

data class ViewAllBannerResponse(
        var data: ArrayList<DataBanner>
)

data class DataBanner(
        var id: Int,
        var image: String,
        var status: String,
        var updated_at: String,
        var day: Int,
        var advertisement_plan_id: Int
)

data class UserInfoResponse(
        var data: DataInfoUser?,
        var message: String
)

data class DataInfoUser(
        var id: Int,
        var username: String,
        var first_name: String,
        var last_name: String,
        var profile_pic: String
)

data class ChatImageResponse(
        var data: Datamage,
        var message: String
)

data class Datamage(
        var image: String
)

data class BannerDetailResponse(
        var data: DataAdd?,
        var message: String
)

data class DataAdd(
        var banner: ArrayList<Banner>?,
        var items: ArrayList<ItemHome>,
        var totalPage: Int
)


data class Banner(
        var category_id: Int,
        var id: Int,
        var image: String,
        var user_id: Int
)

data class VerifyNumberResponse(
        var message: String,
        var otp: Int = 0
)

data class MyRatingResponse(
        var data: ArrayList<DataMyRating>?
)

data class DataMyRating(
        var name: String,
        var profile_pic: String,
        var rating: Double,
        var url: String,
        var userName: String,
        var created_at: String,
        var user_id: Int,
        var item_id: Int
)

data class SearchMultipleKeywordResponse(
        val `data`: SearchMultipleKeywordData,
        val message: String
)

data class SearchMultipleKeywordData(
//    val items: List<SearchMultipleKeywordDataList>,
        val items: List<ItemHome>,
        val totalPage: Int
)

data class SearchMultipleKeywordDataList(
        val category_id: Int,
        val created_at: CreatedAt,
        val id: Int,
        val image_url: String,
        val is_like: Boolean,
        val is_sold: Int,
        val item_type: Int,
        val items_like_count: Any,
        val like_id: Int,
        val name: String,
        val price: Int,
        val profile_pic: String,
        val type: Int,
        val user_id: Int,
        val username: String
)

//data class SearchMultipleKeywordResponse(
//    val `data`: List<SearchMultipleKeywordData>,
//    val message: String
//)

//data class SearchMultipleKeywordData(
//    val category: SearchMultipleKeywordCategory,
//    val category_id: Int,
//    val category_name: String,
//    val id: Int,
//    val name: String
//)

data class SearchMultipleKeywordCategory(
        val id: Int,
        val name: String,
        val parent_id: Int
)

data class ActivityListResponseModel(
        val data: ActivityListData,
        val message: String
)

data class ActivityListData(
        val activities: ArrayList<ActivityData>
)

data class ActivityData(
        val comment_id: Int,
        val created_at: CreatedAt,
        val description: String,
        val image: String,
        val item_id: Int,
        val user_id: Int,
        val message: String,
        val type: Int
)



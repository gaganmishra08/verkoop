package com.verkoopapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class SignUpRequest(
        val email: String,
        val username: String,
        val password: String,
        val login_type: String,
        val country: String,
        val country_code: String,
//        val device_id: String,
        val device_type: String
//        val currency: String,
//        val currency_symbol: String
)

data class LoginRequest(
        val email: String,
        val password: String,
        val login_type: String,
//        val device_id: String,
        val device_type: String
//        val currency: String,
//        val currency_symbol: String
)

@Parcelize
data class labelText(
        val text: String
) : Parcelable

data class LoginSocialRequest(
        val username: String,
//        val FirstName: String,
//        val lastName: String,
        val email: String,
        val social_id: String,
        val login_type: String,
//        val device_id: String,
        val device_type: String
)

data class LoginSocialGmailRequest(
        val username: String,
        val email: String,
        val social_id: String,
        val login_type: String
)

data class UpdateDeviceInfoRequest(
        val user_id:String,
       val device_id : String,
       val device_type : String
)

data class UpdateCountryRequest(
        val user_id:String,
        val country : String,
        val country_code : String
)

data class MakePaymentRequest(
        val user_id:String,
        val currency: String,
        val amount: String,
        val ex_year:String,
        val ex_mon:String,
        val cvv:String,
        val card_num:String,
        val name:String
)

data class DeactivateAccountRequest(
        val access_token:String
)

@Parcelize
data class AddItemRequest(
        val imageList: ArrayList<String>,
        val categoriesId: String,
        val categoryName: String,
        val name: String,
        val price: String,
        val country_code: String,
        val item_type: String,
        val description: String,
        val user_id: String,
        val label: String,
        val Latitude: String? = null,
        val Longitude: String? = null,
        val Address: String? = null,
        val meet_up: String? = null,
        val type: Int = 0,
        val additional_info: AdditionalInfo? = null,
        val zone_id: Int = 0,
        val brand_id: Int = 0,
        val car_type_id: Int = 0

) : Parcelable

@Parcelize
data class AdditionalInfo(
        val car_brand_id: Int = 0,
        val brand_name: String? = null,
        val car_type: String? = null,
        val car_type_id: Int = 0,
        val registration_year: String? = null,
        val direct_owner: Int = 0,
        val location: String? = null,
        val zone_id: Int = 0,
        val street_name: String? = null,
        val postal_code: Int = 0,
        val city: String? = null,
        val bedroom: Int = 0,
        val bathroom: Int = 0,
        val min_price: String? = null,
        val max_price: String? = null,
        val property_type: String? = null,
        val parking_type: Int = 0,
        val from_year: Int? = 0,
        val to_year: Int? = 0,
        val furnished: Int? = 0,
        val transmission_type: Int = 0,
        val model_id: Int = 0,
        val model_name: String? = null
) : Parcelable

@Parcelize
data class EditItemRequest(
        val imageList: ArrayList<String>,
        val deleteImageList: String,
        val categoriesId: String,
        val categoryName: String,
        val name: String,
        val price: String,
        val country_code: String,
        val item_type: String,
        val description: String,
        val user_id: String,
        val Latitude: String,
        val Longitude: String,
        val Address: String,
        val meet_up: String,
        val item_id: Int,
        val type: Int = 0,
        val additional_info: AdditionalInfo? = null,
        val zone_id: Int = 0,
        val brand_id: Int = 0,
        val car_type_id: Int = 0
) : Parcelable


data class PlaceSearchRequest(
        val loc: String,
        val radius: String,
        val keyword: String,
        val key: String
)

@Parcelize
data class SelectedImage(
        val imageUrl: String,
        val adapterPosition: Int,
        val isEditable: Boolean,
        val imageId: Int
) : Parcelable

data class LickedRequest(
        val user_id: String,
        val item_id: Int
)

data class CategoryPostRequest(
        val category_id: String,
        val type: Int,
        val userId: String,
        val name: String
)

data class UpdateCategoryRequest(
        val user_id: String,
        val category_id: String
)

@Parcelize
data class FilterRequest(
        val category_id: String,
        val type: Int,
        val userId: String,
        val sort_no: String,
        val latitude: String,
        val longitude: String,
        val item_type: String,
        val meet_up: String,
        val min_price: String,
        val max_price: String,
        val search: String,
        val country_code: String,
        val item_id: Int = 0
) : Parcelable

@Parcelize
data class FilterModal(
        val FilterType: String,
        val FilterName: String,
        val isClicked: Boolean,
        val type: Int
) : Parcelable


data class ProfileUpdateRequest(
        val user_id: String,
        val username: String,
        val first_name: String,
        val last_name: String,
        val city: String,
//        val cityname: String,
        val state: String,
        val country: String,
        val country_code: String,
        val City_id: String,
        val State_id: String,
        val country_id: String,
        val website: String,
        val bio: String,
        val profile_pic: String,
        val mobile_no: String,
        val gender: String,
        val DOB: String
)

data class UpdatePasswordRequest(
        val user_id: String,
        val current_password: String,
        val new_password: String
)


data class PostCommentRequest(
        val user_id: String,
        val item_id: Int,
        val comment: String
)

data class ReportUserRequest(
        val user_id: String,
        val item_id: Int,
        val report_id: Int,
        val type: Int


)

data class FollowRequest(
        val user_id: String,
        val follower_id: Int
)

data class BlockUserRequest(
        val user_id: String,
        val user_block_id: Int
)

data class MarkAsSoldRequest(
        val user_id: String
)

class MessageEvent(
        val updateUi: String
)

class MessageEventOnLikeCategory(
        val position: Int,
        val comingFrom: String
)

class MessageEventOnLikeBuyCars(
        val position: Int,
        val comingFrom: String
)

class MessageEventOnLikeBuyProperty(
        val position: Int,
        val comingFrom: String
)

class MessageEventOnLikeUserProfile(
        val position: Int,
        val comingFrom: String
)

class MessageEventOnLikeAdvertisementAdapter(
        val position: Int,
        val comingFrom: String
)

class MessageEventOnLike(
        val position: Int,
        val comingFrom: String
)

data class SearchItemRequest(
        val name: String,
        val user_id: Int
)

data class RenewBannerRequest(
        val banner_id: Int,
        val advertisement_plan_id: Int
)

data class SearchUserRequest(
        val username: String
)

data class SearchKeywordMultipleDataRequest(
        val name: String,
        val user_id: Int
)

data class ForgotPasswordRequest(
        val email: String
)

data class LogOutRequest(
        val user_id : Int
)

data class SendMoneyRequest(
        val qrCodeId: String,
        val user_id: Int,
        val amount: String
)

data class HomeRequest(
        val type: Int,
        val country_code: String,
        val page: Int
)

data class HomeRequestID(
        val type: Int
)

data class CarsFilterRequest(
        val type: Int,
        val brand_id: Int = 0,
        val car_type_id: Int = 0,
        val price_no: Int = 0,
        val zone_id: Int = 0
)

data class PropertyTypeRequest(
        val name: String,
        var isSelected: Boolean
)

data class AddMoneyRequest(
        val user_id: Int,
        val amount: Double,
        val token: String,
        val currency: String
)

data class PurchaseCoinRequest(
        val user_id: Int,
        val coin_plan_id: Int
)

data class UploadBannerRequest(
        val user_id: Int,
        val category_id: Int,
        val advertisement_plan_id: Int,
        val banner: String
)

data class SendQrCodeRequest(
        val user_id: Int,
        val coin: Int,
        val qrCodeId: String
)

data class RateUserRequest(
        val user_id: Int,
        val rated_user_id: Int,
        val item_id: Int,
        val rating: Float
)

data class VerifyNumberRequest(
        var mobile_no: String
)

data class VerifyOtpRequest(
        var otp: Int,
        var user_id: Int,
        var mobile_no: String
)

data class StateRequest(
        val country_id: String
)
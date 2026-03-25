package com.simats.savorshelf.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<VerifyOtpResponse>

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    @Multipart
    @POST("scan-product-info")
    suspend fun scanProductInfo(@Part image: MultipartBody.Part): Response<ScanProductResponse>

    @Multipart
    @POST("add-labeled-product")
    suspend fun addLabeledProduct(
        @Part front_image: MultipartBody.Part,
        @Part expiry_image: MultipartBody.Part,
        @Part("user_id") userId: RequestBody,
        @Part("item_name") itemName: RequestBody,
        @Part("category") category: RequestBody,
        @Part("storage_type") storageType: RequestBody,
        @Part("expiry_date") expiryDate: RequestBody,
        @Part("mfg_date") mfgDate: RequestBody,
        @Part("lot_number") lotNumber: RequestBody,
        @Part("quantity") quantity: RequestBody
    ): Response<AddProductResponse>

    @POST("add-unlabeled-product")
    suspend fun addUnlabeledProduct(@Body request: AddUnlabeledProductRequest): Response<AddUnlabeledProductResponse>

    @GET("get-pantry-items")
    suspend fun getPantryItems(@Query("user_id") userId: String): Response<GetPantryItemsResponse>

    @GET("get-product-details")
    suspend fun getProductDetails(@Query("id") id: String): Response<GetProductDetailsResponse>

    @POST("delete-product")
    suspend fun deleteProduct(@Body request: DeleteProductRequest): Response<DeleteProductResponse>

    @PUT("update-profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UpdateProfileResponse>

    @POST("change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    @GET("get-freshness-report")
    suspend fun getFreshnessReport(@Query("user_id") userId: String): Response<GetFreshnessReportResponse>

    @POST("delete-account")
    suspend fun deleteAccount(@Body request: DeleteAccountRequest): Response<DeleteAccountResponse>

    @GET("get-dashboard")
    suspend fun getDashboard(@Query("user_id") userId: String): Response<GetDashboardResponse>

    @POST("update-item-status")
    suspend fun updateItemStatus(@Body request: UpdateItemStatusRequest): Response<UpdateItemStatusResponse>

    @POST("save-alert-settings")
    suspend fun saveAlertSettings(@Body request: SaveAlertSettingsRequest): Response<SaveAlertSettingsResponse>

    @GET("get-alert-settings")
    suspend fun getAlertSettings(@Query("user_id") userId: String): Response<GetAlertSettingsResponse>

    @GET("get-notifications")
    suspend fun getNotifications(@Query("user_id") userId: String): Response<GetNotificationsResponse>

    @POST("mark-notification-read")
    suspend fun markNotificationRead(@Body request: MarkNotificationReadRequest): Response<MarkNotificationReadResponse>

    @retrofit2.http.DELETE("delete-notification/{id}")
    suspend fun deleteNotification(@retrofit2.http.Path("id") notificationId: Int): Response<DeleteNotificationResponse>

    @POST("mark-all-notifications-read")
    suspend fun markAllNotificationsRead(@Body request: MarkAllNotificationsReadRequest): Response<MarkAllNotificationsReadResponse>

    @POST("delete-all-notifications")
    suspend fun deleteAllNotifications(@Body request: DeleteAllNotificationsRequest): Response<DeleteAllNotificationsResponse>

    @GET("trigger-scheduler")
    suspend fun triggerScheduler(): Response<TriggerSchedulerResponse>
}

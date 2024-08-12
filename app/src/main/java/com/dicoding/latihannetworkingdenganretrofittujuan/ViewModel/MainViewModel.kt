package com.dicoding.latihannetworkingdenganretrofittujuan.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.CustomerReviewsItem
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.PostReviewResponse
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.Restaurant
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.RestaurantResponse
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    init {
        findRestaurant()
    }

    private fun findRestaurant() {
        // Menggunakan _isLoading untuk menggantikan showLoading
        _isLoading.value = true  // Menampilkan loading
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                // Menggunakan _isLoading untuk menggantikan showLoading
                _isLoading.value = false  // Menyembunyikan loading
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        // Mengganti setRestaurantData dengan mengupdate _restaurant
                        _restaurant.value = responseBody.restaurant
                        // Mengganti setReviewData dengan mengupdate _listReview
                        _listReview.value = responseBody.restaurant.customerReviews
                    }
                } else {
                    // Mengganti MainActivity.TAG dengan TAG yang ada di companion object MainViewModel
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                // Menggunakan _isLoading untuk menggantikan showLoading
                _isLoading.value = false  // Menyembunyikan loading
                // Mengganti MainActivity.TAG dengan TAG yang ada di companion object MainViewModel
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun postReview(review: String) {
        // Menggunakan _isLoading untuk menggantikan showLoading
        _isLoading.value = true  // Menampilkan loading
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "MATEO", review)
        client.enqueue(object : Callback<PostReviewResponse> {
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                // Menggunakan _isLoading untuk menggantikan showLoading
                _isLoading.value = false  // Menyembunyikan loading
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // Mengganti setReviewData dengan mengupdate _listReview
                    _listReview.value = responseBody.customerReviews
                } else {
                    // Mengganti MainActivity.TAG dengan TAG yang ada di companion object MainViewModel
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                // Menggunakan _isLoading untuk menggantikan showLoading
                _isLoading.value = false  // Menyembunyikan loading
                // Mengganti MainActivity.TAG dengan TAG yang ada di companion object MainViewModel
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }
}

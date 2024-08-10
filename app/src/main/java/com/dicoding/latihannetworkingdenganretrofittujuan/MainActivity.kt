package com.dicoding.latihannetworkingdenganretrofittujuan

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.CustomerReviewsItem
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.PostReviewResponse
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.Restaurant
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.RestaurantResponse
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.retrofit.ApiConfig
import com.dicoding.latihannetworkingdenganretrofittujuan.databinding.ActivityMainBinding
import com.dicoding.latihannetworkingdenganretrofittujuan.ui.ReviewAdapater
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // View binding untuk mengakses tampilan di layout
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"  // ID restoran yang akan diambil datanya
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)  // Inisialisasi view binding
        setContentView(binding.root)

        supportActionBar?.hide()  // Menyembunyikan ActionBar

        // Mengatur RecyclerView untuk menampilkan ulasan restoran
        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)

        findRestaurant()  // Memulai proses pencarian data restoran

        binding.btnSend.setOnClickListener{ view ->
            postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        }
    }

    private fun postReview(review: String){
        showLoading(true)
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID,"MATEO ",review)
        client.enqueue(object :Callback<PostReviewResponse>{
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    setReviewData(responseBody.customerReviews)
                } else {
                    Log.e(TAG,"onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                showLoading(false)
                Log.e(TAG,"onFailure: ${t.message}")
            }
        })
    }

    // Fungsi untuk mengambil data restoran dari API
    private fun findRestaurant() {
        showLoading(true)  // Menampilkan loading saat data sedang diambil
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse> {  // Callback untuk menangani respons dari API
            override fun onResponse(
                call: Call<RestaurantResponse>,  // Panggilan API
                response: Response<RestaurantResponse>  // Respons dari API
            ) {
                showLoading(false)  // Menyembunyikan loading setelah respons diterima
                if (response.isSuccessful) {  // Memeriksa apakah respons berhasil
                    val responseBody = response.body()  // Mendapatkan body dari respons
                    if (responseBody != null) {  // Memeriksa apakah body tidak null
                        setRestaurantData(responseBody.restaurant)  // Mengatur data restoran ke UI
                        setReviewData(responseBody.restaurant.customerReviews)  // Mengatur data ulasan ke UI
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")  // Log pesan kesalahan jika respons gagal
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {  // Jika panggilan API gagal
                showLoading(false)  // Menyembunyikan loading
                Log.e(TAG, "onFailure: ${t.message}")  // Log pesan kesalahan
            }
        })
    }

    // Fungsi untuk mengatur data restoran ke tampilan UI
    private fun setRestaurantData(restaurant: Restaurant) {
        binding.tvTitle.text = restaurant.name  // Menampilkan nama restoran
        binding.tvDescription.text = restaurant.description  // Menampilkan deskripsi restoran
        Glide.with(this@MainActivity)  // Menggunakan Glide untuk memuat gambar restoran
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    // Fungsi untuk mengatur data ulasan ke tampilan RecyclerView
    private fun setReviewData(consumerReviews: List<CustomerReviewsItem>) {
        val adapter = ReviewAdapater()  // Inisialisasi adapter untuk RecyclerView
        adapter.submitList(consumerReviews)  // Mengirim data ulasan ke adapter
        binding.rvReview.adapter = adapter  // Mengatur adapter ke RecyclerView
        binding.edReview.setText("")  // Mengosongkan input ulasan setelah data diperbarui
    }

    // Fungsi untuk menampilkan atau menyembunyikan loading indicator
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE  // Menampilkan loading indicator
        } else {
            binding.progressBar.visibility = View.GONE  // Menyembunyikan loading indicator
        }
    }
}

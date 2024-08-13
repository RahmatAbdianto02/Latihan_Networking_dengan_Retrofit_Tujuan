package com.dicoding.latihannetworkingdenganretrofittujuan

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.latihannetworkingdenganretrofittujuan.ViewModel.MainViewModel
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.CustomerReviewsItem
import com.dicoding.latihannetworkingdenganretrofittujuan.data.response.Restaurant
import com.dicoding.latihannetworkingdenganretrofittujuan.databinding.ActivityMainBinding
import com.dicoding.latihannetworkingdenganretrofittujuan.ui.ReviewAdapater
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    // View binding untuk mengakses tampilan di layout
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
        const val RESTAURANT_ID =
            "uewq1zg2zlskfw1e867"  // ID restoran yang akan diambil datanya
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)  // Inisialisasi view binding
        setContentView(binding.root)

        supportActionBar?.hide()  // Menyembunyikan ActionBar



        val mainViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(MainViewModel::class.java)

        mainViewModel.restaurant.observe(this) { restaurant ->
            setRestaurantData(restaurant)

            // Mengatur RecyclerView untuk menampilkan ulasan restoran
            val layoutManager = LinearLayoutManager(this)
            binding.rvReview.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
            binding.rvReview.addItemDecoration(itemDecoration)

            mainViewModel.listReview.observe(this) { consumerReviews ->
                setReviewData(consumerReviews)
            }
            mainViewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }
           mainViewModel.snackbarText.observe(this,{
               it.getContentIfNotHandled()?.let { snackBarText ->
                   Snackbar.make(
                       window.decorView.rootView,
                       snackBarText,
                       Snackbar.LENGTH_SHORT
                   ).show()
               }
           })

            // Memulai proses pencarian data restoran
            binding.btnSend.setOnClickListener { view ->
                mainViewModel.postReview(binding.edReview.text.toString())
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
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

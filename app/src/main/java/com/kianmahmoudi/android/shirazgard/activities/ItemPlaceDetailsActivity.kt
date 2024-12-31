package com.kianmahmoudi.android.shirazgard.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.appbar.MaterialToolbar
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ActivityMainBinding
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceDetailsBinding
import java.util.Locale

class ItemPlaceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ItemPlaceDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemPlaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageList = ArrayList<SlideModel>()

        if (intent.extras != null){
            when (Locale.getDefault().language) {
                "en" -> {
                    binding.placeTitle.setText(intent.extras?.getString("enName"))
                    supportActionBar?.title = intent.extras?.getString("enName")
                }

                "fa" -> {
                    binding.placeTitle.setText(intent.extras?.getString("faName"))
                    supportActionBar?.title = intent.extras?.getString("faName")
                }
            }
            binding.placeAddress.text = intent.extras?.getString("address")
            binding.placeDescription.text = intent.extras?.getString("description")

            val imageUrls = intent.extras?.getStringArrayList("images")

            if (imageUrls!= null) {
                for (url in imageUrls) {
                    imageList.add(SlideModel(url,ScaleTypes.FIT))
                }
            }

        }

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)

        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
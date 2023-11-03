package com.learning.orderfoodappsch3.presentation.ui.orderfooddetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.learning.orderfoodappsch3.R
import com.learning.orderfoodappsch3.databinding.ActivityDetailOrderFoodBinding
import com.learning.orderfoodappsch3.model.OrderFood
import com.learning.orderfoodappsch3.utils.AssetWrapper
import com.learning.orderfoodappsch3.utils.proceedWhen
import com.learning.orderfoodappsch3.utils.toCurrencyFormat
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailOrderFoodActivity : AppCompatActivity() {
    private val binding: ActivityDetailOrderFoodBinding by lazy {
        ActivityDetailOrderFoodBinding.inflate(layoutInflater)
    }
    private val viewModel: DetailOrderFoodViewModel by viewModel {
        parametersOf(intent?.extras)
    }
    private val assetWrapper: AssetWrapper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindOrderFood(viewModel.orderFood)
        observeData()
        setClickListener()
    }

    private fun bindOrderFood(orderFood: OrderFood?) {
        orderFood?.let {
                item ->
            binding.tvOrderFoodName.text = item.foodName
            binding.tvDescMenu.text = item.desc
            binding.ivOrderFood.load(item.imgFood) { crossfade(true) }
            binding.tvPriceFood.text = item.foodPrice.toCurrencyFormat()
        }
    }

    private fun observeData() {
        viewModel.priceLiveData.observe(this) {
            binding.tvTotalPrice.text = it.toCurrencyFormat()
        }
        viewModel.countOrderLiveData.observe(this) {
            binding.tvTotalOrder.text = it.toString()
        }
        viewModel.resultToCart.observe(this) {
            it.proceedWhen(doOnSuccess = {
                Toast.makeText(
                    this,
                    assetWrapper.getString(R.string.your_food_has_added_to_cart),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }, doOnError = {
                    Toast.makeText(
                        this,
                        assetWrapper.getString(R.string.your_food_failed_to_add_to_cart),
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }

    private fun setClickListener() {
        binding.btnAdd.setOnClickListener {
            viewModel.plus()
        }
        binding.btnMinus.setOnClickListener {
            viewModel.minus()
        }
        binding.cvBtnAddToCart.setOnClickListener {
            viewModel.toCart()
        }
        binding.ivBackButton.setOnClickListener {
            onBackPressed()
        }
        binding.tvMaps.setOnClickListener {
            navigateToGoogleMaps(viewModel.orderFood)
        }
    }

    private fun navigateToGoogleMaps(orderFood: OrderFood?) {
        orderFood?.let {
            val mapUrl = "https://maps.app.goo.gl/h4wQKqaBuXzftGK77"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl))
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_ORDER_FOOD = "extra_order_food"
        fun startActivity(context: Context, orderfood: OrderFood) {
            val intent = Intent(context, DetailOrderFoodActivity::class.java)
            intent.putExtra(EXTRA_ORDER_FOOD, orderfood)
            context.startActivity(intent)
        }
    }
}

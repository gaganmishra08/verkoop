package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.verkoopapp.R
import com.verkoopapp.adapter.CategoryDialogAdapter
import com.verkoopapp.adapter.SubCategoryDialogAdapter
import kotlinx.android.synthetic.main.select_category_dialog_activity.*
import java.util.ArrayList
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.github.florent37.viewanimator.ViewAnimator
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.models.*
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import retrofit2.Response


class SelectCategoryDialogActivity : AppCompatActivity(), SubCategoryDialogAdapter.SelectedCategory {

    override fun subCategoryName(name: String, subCategoryId: Int,categoryId:Int) {
        ViewAnimator
                .animate(flParent)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParent)
                .alpha(1f,0f)
                .duration(400)
                .onStop {
                    llParent.visibility= View.GONE
                    var screnType:Int
                    if(categoryId==85){
                        screnType=1
                    }else if(categoryId==24 &&subCategoryId!=103){
                        screnType=2
                    }else if(categoryId==24 &&subCategoryId==103){
                        screnType=3
                    }else{
                        screnType=0
                    }
                    val returnIntent = Intent()
                    returnIntent.putExtra(AppConstants.CATEGORY_NAME, name)
                    returnIntent.putExtra(AppConstants.SUB_CATEGORY_ID, subCategoryId)
                    returnIntent.putExtra(AppConstants.CATEGORY_ID, categoryId)
                    returnIntent.putExtra(AppConstants.SCREEN_TYPE, screnType)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                    overridePendingTransition(0,0)
                }
                .start()
    }

    private val categoryList= ArrayList<DataCategory>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_category_dialog_activity)
        setData()
        setAnimation()
        if (Utils.isOnline(this)) {
            callCategoriesApi()
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }
    private fun setAnimation() {
        ViewAnimator
                .animate(flParent)
                .translationY(1000f, 0f)
                .duration(400)
                .start()
    }
    private fun setAdapter() {
        val mManager = LinearLayoutManager(this)
        rvCategoryDialogList.layoutManager = mManager
        val categoryDialogAdapter = CategoryDialogAdapter(this,categoryList)
        rvCategoryDialogList.adapter = categoryDialogAdapter
    }

    private fun setData() {
        pbProgress.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY)
        ivFinish.setOnClickListener {
            onBackPressed()
        }
    }
  /*  private fun setListData() {
        val nameList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        val subList = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles")
        val subList2 = arrayOf("Women's", "men's", "Footwear", "Desktop's", "Mobiles", "Desktop's", "Mobiles", "Furniture", "Pets", "Car", "Books")
        for(j in subList.indices){
            val subCatogry=SubModal(subList[j],0)
            newList.add(subCatogry)
        }
        for(j in subList2.indices){
            val subCatogry=SubModal(subList2[j],0)
            newList1.add(subCatogry)
        }

        for (i in nameList.indices) {
            if(i%2==0) {
                val categoryModal = QuestionsDataDialogModel(newList, nameList[i],false)
                categoryList.add(categoryModal)
            }else{
                val categoryModal = QuestionsDataDialogModel(newList1, nameList[i],false)
                categoryList.add(categoryModal)
            }

        }
        setAdapter()
    }*/

    override fun onBackPressed() {
        ViewAnimator
                .animate(flParent)
                .translationY(0f, 2000f)
                .duration(700)
                .andAnimate(llParent)
                .alpha(1f,0f)
                .duration(600)
                .onStop {
                    llParent.visibility= View.GONE
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_CANCELED, returnIntent)
                    finish()
                    overridePendingTransition(0,0)
                }
                .start()

    }
    private fun callCategoriesApi() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pbProgress.visibility=View.VISIBLE
        ServiceHelper().categoriesService(
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgress.visibility=View.GONE
                        VerkoopApplication.instance.loader.hide(this@SelectCategoryDialogActivity)
                        val categoriesResponse = response.body() as CategoriesResponse
                        if(categoriesResponse.data!=null) {
                            categoryList.addAll(categoriesResponse.data)
                            setAdapter()
                        }
                    }

                    override fun onFailure(msg: String?) {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProgress.visibility=View.GONE
                        Utils.showSimpleMessage(this@SelectCategoryDialogActivity, msg!!).show()
                    }
                })
    }
}
package com.verkoopapp

interface LikeDisLikeListener{
    fun getLikeDisLikeClick(type:Boolean,position:Int,lickedId:Int,itemId:Int)
    fun getItemDetailsClick(itemId:Int,userId:Int)
}

interface LoadingListener{
    fun getLoadingCallBack()
}
package com.ad4th.devote.control

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.GlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.GrayscaleTransformation

import java.io.File
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.graphics.drawable.BitmapDrawable
import android.R.attr.bitmap
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.support.annotation.DrawableRes
import com.ad4th.devote.R
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.target.ImageViewTarget
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import jp.wasabeef.glide.transformations.CropCircleTransformation
import org.spongycastle.math.ec.ECCurve


class GlideManager : GlideModule {

    /**
     * Uri 이미지 파일 로드
     */
    fun uriLoad(context: Context, url: String, target: ImageView) {
        Glide.with(context).load(url).into(target)
    }

    /**
     * Uri 이미지 파일 로드 - Cicle
     */
    fun uriLoadCicle(context: Context, url: String, target: ImageView) {
        Glide.with(context).load(url).apply(RequestOptions().circleCrop()).into(target)
    }

    /**
     * 리소스 이미지 파일 로드
     */
    fun resouceLoad(context: Context, resourceId: Int, target: ImageView) {
        Glide.with(context).load(resourceId).into(target)
    }

    /**
     * 로컬 이미지 파일 로드
     */
    fun localLoad(context: Context, file: File, target: ImageView) {
        // 로컬 파일
        Glide.with(context).load(file).into(target)
    }

    /**
     * 백그라운트 이미지 처리
     */
    fun uriBackgroundLoad(context: Context, url: String, target: ImageView) {

        //ColorFilterTransformation(R.color.block)
        val multi = MultiTransformation( GrayscaleTransformation(), BlurTransformation(10))

        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        try {
                            val original = (resource as BitmapDrawable).bitmap
                            val resizeBitmap = Bitmap.createBitmap(original
                                    , original.width / 4 //X 시작위치 (원본의 4/1지점)
                                    , original.height / 4 //Y 시작위치 (원본의 4/1지점)
                                    , (original.width / 2).toInt() // 넓이 (원본의 절반 크기)
                                    , (original.height / 2).toInt()) // 높이 (원본의 절반 크기)

                            val d = BitmapDrawable(context.resources, resizeBitmap)
                            target.setBackgroundDrawable(d)
                        } catch (e: Exception) {
                        }

                    }
                })
    }


    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache(InternalCacheDiskCacheFactory(context))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

    }

    companion object {
        private val TAG = GlideManager::class.java.name
        //int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 16);
        @Volatile
        private var manager: GlideManager? = null

        /**
         * @return SingleTon pattern
         */
        val instance: GlideManager?
            get() {
                synchronized(GlideManager::class.java) {
                    if (manager == null) {
                        manager = GlideManager()
                    }
                }
                return manager
            }
    }

}

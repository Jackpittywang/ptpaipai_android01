package com.putao.camera.camera.filter;

import android.content.Context;
import android.graphics.PointF;

import com.putao.camera.camera.gpuimage.GPUImageBrightnessFilter;
import com.putao.camera.camera.gpuimage.GPUImageContrastFilter;
import com.putao.camera.camera.gpuimage.GPUImageFilter;
import com.putao.camera.camera.gpuimage.GPUImageFilterGroup;
import com.putao.camera.camera.gpuimage.GPUImageMagicBeautyFilter;
import com.putao.camera.camera.gpuimage.GPUImageRGBFilter;
import com.putao.camera.camera.gpuimage.GPUImageSaturationFilter;
import com.putao.camera.camera.gpuimage.GPUImageSepiaFilter;
import com.putao.camera.camera.gpuimage.GPUImageVignetteFilter;
import com.putao.camera.camera.gpuimage.GPUImageWhiteBalanceFilter;
import com.putao.camera.camera.gpuimage.MagicSketchFilter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 使用GPUImage自定义滤镜
 */
public class CustomerFilter {
    public enum FilterType implements Serializable {
        NONE, BLCX, MSHK, BBNN, QRSY, ZJLN, YMYG, WLHA, SLDC,SM,TEST1,TEST2
    }

    private final String[] filterNmae = {"原图", "白亮晨曦", "陌上花开", "白白嫩嫩", "秋日私语", "指尖流年", "一米阳光", "蔚蓝海岸", "闪亮登场","素描","样式1","样式2"};
    private Map<String, FilterType> filterTypeMap = new HashMap<>();

    public CustomerFilter() {
        initFilterTypeMap();
    }

    private void initFilterTypeMap() {
        filterTypeMap.put(filterNmae[0], FilterType.NONE);
        filterTypeMap.put(filterNmae[1], FilterType.BLCX);
        filterTypeMap.put(filterNmae[2], FilterType.MSHK);
        filterTypeMap.put(filterNmae[3], FilterType.BBNN);
        filterTypeMap.put(filterNmae[4], FilterType.QRSY);
        filterTypeMap.put(filterNmae[5], FilterType.ZJLN);
        filterTypeMap.put(filterNmae[6], FilterType.YMYG);
        filterTypeMap.put(filterNmae[7], FilterType.WLHA);
        filterTypeMap.put(filterNmae[8], FilterType.SLDC);
        filterTypeMap.put(filterNmae[9], FilterType.SM);
        filterTypeMap.put(filterNmae[10], FilterType.TEST1);
        filterTypeMap.put(filterNmae[11], FilterType.TEST2);

    }

    public Map<String, FilterType> getFilterTypeMap() {
        return filterTypeMap;
    }

    public GPUImageFilter getFilterByType(FilterType type, Context mContext) {
        switch (type) {
            case NONE:
                return getYuTu();
            case BLCX:
                return getBaiLiangChenXi();
            case MSHK:
                return getMoShangKiaHua();
            case BBNN:
                return getBaiBaiNenNen();
            case QRSY:
                return getQiuRiSiYu();
            case ZJLN:
                return getZhiJianLiuNian();
            case YMYG:
                return getYiMiYangGuang();
            case WLHA:
                return getWeiLanHaiAn();
            case SLDC:
                return getShanLiangDengChang();
            case SM:
//                return new MagicSketchFilter(mContext);
            return getMagicSketchFilter(mContext);
            case TEST1:
                return getMagicSketchFilter(mContext);
            case TEST2:
                return getMagicSketchFilter(mContext);
            default:
                return new GPUImageFilter();
        }
    }

    /**
     * 素描
     *
     * @return
     */
    private GPUImageFilterGroup getMagicSketchFilter(Context mContext) {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new MagicSketchFilter(mContext));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }


    /**
     * 白亮晨曦
     */
    private GPUImageFilter getBaiLiangChenXi() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //亮度 0.0f, 2.0f
        filters.add(new GPUImageBrightnessFilter(0.25f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }


    /**
     * 陌上花开
     *
     * @return
     */
    private GPUImageFilter getMoShangKiaHua() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //0.0f, 2.0f
        filters.add(new GPUImageSepiaFilter(0.5f));
        //0.0f, 2.0f
        filters.add(new GPUImageContrastFilter(1.0f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 白白嫩嫩
     *
     * @return
     */
    private GPUImageFilter getBaiBaiNenNen() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //亮度 0.0f, 2.0f
        filters.add(new GPUImageBrightnessFilter(0.20f));
        //饱和度 0.0f, 2.0f
        filters.add(new GPUImageSaturationFilter(1.2f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 秋日私语
     *
     * @return
     */
    private GPUImageFilterGroup getQiuRiSiYu() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //饱和度 0.0f, 2.0f
        filters.add(new GPUImageSaturationFilter(1.1f));
        //亮度 0.0f, 2.0f
        filters.add(new GPUImageBrightnessFilter(0.10f));
        //怀旧 0.0f, 2.0f
        filters.add(new GPUImageSepiaFilter(0.1f));
        //对比度 0.0f, 2.0f
        filters.add(new GPUImageContrastFilter(1.1f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 指尖流年
     *
     * @return
     */
    private GPUImageFilterGroup getZhiJianLiuNian() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //0.0f, 2.0f
        filters.add(new GPUImageSepiaFilter(1.0f));
        //0.0f, 2.0f
        filters.add(new GPUImageContrastFilter(1.25f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 一米阳光
     *
     * @return
     */
    private GPUImageFilterGroup getYiMiYangGuang() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //白平衡 2000.0f, 8000.0f
        filters.add(new GPUImageWhiteBalanceFilter(5000.0f, 0.0f));
        //对比度 0.0f, 2.0f
        filters.add(new GPUImageContrastFilter(1.3f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 蔚蓝海岸
     *
     * @return
     */
    private GPUImageFilter getWeiLanHaiAn() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //饱和度 0.0f, 2.0f
        filters.add(new GPUImageSaturationFilter(1.2f));
        //rgb
        filters.add(new GPUImageRGBFilter(0.75f, 0.75f, 1.0f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    /**
     * 闪亮登场
     *
     * @return
     */
    private GPUImageFilterGroup getShanLiangDengChang() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //0.0f, 1.0f
        PointF centerPoint = new PointF();
        centerPoint.x = 0.5f;
        centerPoint.y = 0.5f;
        filters.add(new GPUImageVignetteFilter(centerPoint, new float[]{0.0f, 0.0f, 0.0f}, 0.2f, 0.75f));
        //0.0f, 2.0f
        filters.add(new GPUImageContrastFilter(1.0f));
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }

    private float progress = 8.0f;

    private GPUImageFilterGroup getYuTu() {
        List<GPUImageFilter> filters = new LinkedList<GPUImageFilter>();
        //高斯模糊0.0f, 15.0f
//        filters.add(new GPUImageBilateralFilter(progress));
        filters.add(new GPUImageMagicBeautyFilter());
        return new GPUImageFilterGroup(filters);
    }
}

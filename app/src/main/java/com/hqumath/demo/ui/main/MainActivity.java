package com.hqumath.demo.ui.main;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.hqumath.demo.app.AppExecutors;
import com.hqumath.demo.app.Constant;
import com.hqumath.demo.base.BaseActivity;
import com.hqumath.demo.databinding.ActivityMainBinding;
import com.hqumath.demo.utils.CommonUtil;
import com.hqumath.demo.utils.LogUtil;
import com.hqumath.demo.utils.SPUtil;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ****************************************************************
 * 作    者: Created by gyd
 * 创建时间: 2023/10/25 9:35
 * 文件描述: 主界面
 * 注意事项:
 * ****************************************************************
 */
public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;
    private Sensor linearAccelerationSensor;
    private ScheduledFuture scheduledFuture;//定时任务
    private SPUtil sp = SPUtil.getInstance();

    private final float[] accelerometerData = new float[3];//加速度计数据
    private final float[] magnetometerData = new float[3];//磁力计数据
    private final float[] linearAccelerationData = new float[3];//线性加速度计数据

    private final float[] rotationMatrix = new float[9];//屏幕的旋转矩阵
    private final float[] orientationAngles = new float[3];//屏幕的三个方向角

    private boolean timerWorking;//普通定时器工作状态

    @Override
    protected View initContentView(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initListener() {
        binding.edtPitchPercent.addTextChangedListener(new MyTextWatcher(Constant.PitchPercent));
        binding.edtRollPercent.addTextChangedListener(new MyTextWatcher(Constant.RollPercent));
        binding.edtYawPercent.addTextChangedListener(new MyTextWatcher(Constant.YawPercent));
        binding.edtSwayPercent.addTextChangedListener(new MyTextWatcher(Constant.SwayPercent));
        binding.edtSurgePercent.addTextChangedListener(new MyTextWatcher(Constant.SurgePercent));
        binding.edtHeavePercent.addTextChangedListener(new MyTextWatcher(Constant.HeavePercent));
        binding.edtPitchMax.addTextChangedListener(new MyTextWatcher(Constant.PitchMax));
        binding.edtRollMax.addTextChangedListener(new MyTextWatcher(Constant.RollMax));
        binding.edtYawMax.addTextChangedListener(new MyTextWatcher(Constant.YawMax));
        binding.edtSwayMax.addTextChangedListener(new MyTextWatcher(Constant.SwayMax));
        binding.edtSurgeMax.addTextChangedListener(new MyTextWatcher(Constant.SurgeMax));
        binding.edtHeaveMax.addTextChangedListener(new MyTextWatcher(Constant.HeaveMax));
    }

    @Override
    protected void initData() {
        //传感器服务
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager == null) {
            return;
        }
        //加速度计
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            LogUtil.d("加速度计存在");
        } else {
            LogUtil.d("加速度计不存在");
        }
        //磁力计
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticSensor != null) {
            LogUtil.d("磁力计存在");
        } else {
            LogUtil.d("磁力计不存在");
        }
        //线性加速度计
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (linearAccelerationSensor != null) {
            LogUtil.d("线性加速度计存在");
        } else {
            LogUtil.d("线性加速度计不存在");
        }

        initTimer();//定时器
//        rotationVectorSensor.getMaximumRange();//最大取值范围
//        rotationVectorSensor.getName();//设备名称
//        rotationVectorSensor.getPower();//功率
//        rotationVectorSensor.getResolution();//精度
//        rotationVectorSensor.getType();//传感器类型
//        rotationVectorSensor.getVendor();//设备供应商
//        rotationVectorSensor.getVersion();//设备版本号

        //读取数据
        binding.edtPitchPercent.setText(sp.getInt(Constant.PitchPercent, 100) + "");
        binding.edtRollPercent.setText(sp.getInt(Constant.RollPercent, 100) + "");
        binding.edtYawPercent.setText(sp.getInt(Constant.YawPercent, 100) + "");
        binding.edtSwayPercent.setText(sp.getInt(Constant.SwayPercent, 100) + "");
        binding.edtSurgePercent.setText(sp.getInt(Constant.SurgePercent, 100) + "");
        binding.edtHeavePercent.setText(sp.getInt(Constant.HeavePercent, 100) + "");
        binding.edtPitchMax.setText(sp.getInt(Constant.PitchMax, 180) + "");
        binding.edtRollMax.setText(sp.getInt(Constant.RollMax, 180) + "");
        binding.edtYawMax.setText(sp.getInt(Constant.YawMax, 180) + "");
        binding.edtSwayMax.setText(sp.getInt(Constant.SwayMax, 10) + "");
        binding.edtSurgeMax.setText(sp.getInt(Constant.SurgeMax, 10) + "");
        binding.edtHeaveMax.setText(sp.getInt(Constant.HeaveMax, 10) + "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometerSensor != null)
            sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (magneticSensor != null)
            sensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (linearAccelerationSensor != null)
            sensorManager.registerListener(sensorEventListener, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        //传感器的采样率 samplingPeriodUs
        //SENSOR_DELAY_FASTEST (延迟 0 微秒)
        //SENSOR_DELAY_GAME（延迟 20,000 微秒）实时性较高的游戏
        //SENSOR_DELAY_UI（延迟 60,000 微秒）适合普通用户界面 UI 变化的频率
        //SENSOR_DELAY_NORMAL 200,000 微秒 益智类或 EASY 级别的游戏
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometerSensor != null || magneticSensor != null || linearAccelerationSensor != null)
            sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //定时任务
        timerWorking = false;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);//取消定时任务
            scheduledFuture = null;
        }
    }

    //定时器
    private void initTimer() {
        if (timerWorking)
            return;
        timerWorking = true;
        //主要任务
        scheduledFuture = AppExecutors.getInstance().scheduledWork().scheduleAtFixedRate(() -> {
            if (timerWorking) {
                updateOrientationAngles();//计算屏幕的方向角
                dealData();//处理数据
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        //当传感器感应的值发生变化时回调
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {//加速度计
                //沿 xyz 轴的加速力（包括重力）。米/秒2
                System.arraycopy(event.values, 0, accelerometerData, 0, accelerometerData.length);
                //LogUtil.d("accelerometer data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {//磁力计
                //沿 xyz 轴的地磁场强度 微特斯拉
                System.arraycopy(event.values, 0, magnetometerData, 0, magnetometerData.length);
                //LogUtil.d("magnetic data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {//线性加速度计
                //沿 xyz 轴的加速力（不包括重力）。米/秒2
                System.arraycopy(event.values, 0, linearAccelerationData, 0, linearAccelerationData.length);
                //LogUtil.d("accelerometer data[x:" + event.values[0] + ", y:" + event.values[1] + ", z:" + event.values[2] + "]");
            }
        }

        //当传感器精度发生变化时回调
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 使用地磁场传感器和加速度计来计算屏幕的方向角
     */
    private void updateOrientationAngles() {
        //基于加速度计和磁力计的当前读数的旋转矩阵
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);
        //将更新的旋转矩阵表示为三个方向角
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
    }

    /**
     * 显示六轴数据
     */
    private void dealData() {
        //欧拉角，单位rad=>°
        //Azimuth yaw偏航角，绕-z轴旋转的角度。值的范围是 -π 到 π。
        //当朝北时，这个角度为0，当朝南时，这个角度为π，当面向东时，该角度为 π/2，当面向西时，该角度为 -π/2。
        float yaw = (float) Math.toDegrees(orientationAngles[0]);
        //pitch俯仰角，绕x轴旋转的角度。值的范围是 -π/2 到 π/2。
        //当与地面平行0 当设备下边缘靠近您，顶部边缘向地面倾斜时为正
        float pitch = (float) Math.toDegrees(orientationAngles[1]);
        //roll横滚角，绕y轴旋转的角度。值的范围是 -π 到 π。
        //当与地面平行0 当设备下边缘靠近您，右边边缘向地面倾斜时为正
        float roll = (float) Math.toDegrees(orientationAngles[2]);

        //线性加速度，单位米/秒2，范围[-170,170], +-17g
        //横移
        float sway = linearAccelerationData[0];
        //纵移
        float surge = linearAccelerationData[1];
        //升降
        float heave = linearAccelerationData[2];

        //处理数据
        int PitchPercent = sp.getInt(Constant.PitchPercent);
        int RollPercent = sp.getInt(Constant.RollPercent);
        int YawPercent = sp.getInt(Constant.YawPercent);
        int SwayPercent = sp.getInt(Constant.SwayPercent);
        int SurgePercent = sp.getInt(Constant.SurgePercent);
        int HeavePercent = sp.getInt(Constant.HeavePercent);
        int PitchMax = sp.getInt(Constant.PitchMax);
        int RollMax = sp.getInt(Constant.RollMax);
        int YawMax = sp.getInt(Constant.YawMax);
        int SwayMax = sp.getInt(Constant.SwayMax);
        int SurgeMax = sp.getInt(Constant.SurgeMax);
        int HeaveMax = sp.getInt(Constant.HeaveMax);

        //百分比和最值，是否反向
        float pitch2 = Math.min(PitchMax, (Math.max(pitch * Math.abs(PitchPercent) / 100, -PitchMax))) * (PitchPercent < 0 ? -1 : 1);
        float roll2 = Math.min(RollMax, (Math.max(roll * Math.abs(RollPercent) / 100, -RollMax))) * (RollPercent < 0 ? -1 : 1);
        float yaw2 = Math.min(YawMax, (Math.max(yaw * Math.abs(YawPercent) / 100, -YawMax))) * (YawPercent < 0 ? -1 : 1);
        float sway2 = Math.min(SwayMax, (Math.max(sway * Math.abs(SwayPercent) / 100, -SwayMax))) * (SwayPercent < 0 ? -1 : 1);
        float surge2 = Math.min(SurgeMax, (Math.max(surge * Math.abs(SurgePercent) / 100, -SurgeMax))) * (SurgePercent < 0 ? -1 : 1);
        float heave2 = Math.min(HeaveMax, (Math.max(heave * Math.abs(HeavePercent) / 100, -HeaveMax))) * (HeavePercent < 0 ? -1 : 1);

        //LogUtil.d("X轴加速度:" + String.format("%.2f", sway) + " Y轴加速度:" + String.format("%.2f", surge) + " Z轴加速度:" + String.format("%.2f", heave));
        binding.getRoot().post(() -> {
            binding.tvPitch1.setText(String.format("%.2f", pitch) + "°");
            binding.tvRoll1.setText(String.format("%.2f", roll) + "°");
            binding.tvYaw1.setText(String.format("%.2f", yaw) + "°");
            binding.tvSway1.setText(String.format("%.2f", sway));
            binding.tvSurge1.setText(String.format("%.2f", surge));
            binding.tvHeave1.setText(String.format("%.2f", heave));
            binding.tvPitch2.setText(String.format("%.2f", pitch2) + "°");
            binding.tvRoll2.setText(String.format("%.2f", roll2) + "°");
            binding.tvYaw2.setText(String.format("%.2f", yaw2) + "°");
            binding.tvSway2.setText(String.format("%.2f", sway2));
            binding.tvSurge2.setText(String.format("%.2f", surge2));
            binding.tvHeave2.setText(String.format("%.2f", heave2));
        });
    }

    private class MyTextWatcher implements TextWatcher {

        private String SPKey = "";

        public MyTextWatcher(String SPKey) {
            this.SPKey = SPKey;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int num = -1;
            try {
                num = Integer.parseInt(s.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (num < -1000 || num > 1000) {
                CommonUtil.toast("请输入合法的值");
            } else {
                sp.put(SPKey, num);
            }
        }
    }
}

package com.hqumath.demo.media.attitude;

import com.hqumath.demo.BuildConfig;
import com.hqumath.demo.media.struct.JavaStruct;
import com.hqumath.demo.media.structBean.ControlStructSeat;
import com.hqumath.demo.utils.ByteUtil;
import com.hqumath.demo.utils.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteOrder;

/**
 * ****************************************************************
 * 作    者: Created by gyd
 * 创建时间: 2022/3/7 16:33
 * 文件描述: 姿态座椅信息发送
 * 注意事项: 六轴运动姿态陀螺仪传感器(三轴加速度和三轴角速度) => 六轴座椅
 * ****************************************************************
 */
public class SeatUDPClient {
    public static final String TAG = "SeatUDPClient";
    /*报文结构
        S:%lf:%lf:%lf:%lf:%lf:%lf:E
        roll, pitch, yaw, surge, sway, heave*/
    //pi≈3.14=180°
    private String ipOrDomain;
    private int dstPort;
    private ControlStructSeat controlStructSeat;//报文

    private DatagramSocket socket;
    private InetAddress host;

    private float rollScale = 1.0f;//比例和方向 -0.8f
    private float pitchScale = 1.0f;//0.95f
    private float yawScale = 1.0f;//0.2f
    private float surgeScale = 1.0f;
    private float swayScale = 1.0f;
    private float heaveScale = 1.0f;//0.4f

    private float rollMax = 15;//pitch,roll, yaw 取值范围（±16）
    private float pitchMax = 15;
    private float yawMax = 15;
    private float surgeMax = 2;//surge, sway, heave 取值范围（±170）
    private float swayMax = 2;
    private float heaveMax = 40;

    public SeatUDPClient() {
        /*if (BuildConfig.DEBUG) {//TODO 测试使用伪地址
            this.ipOrDomain = "192.168.2.101";
            this.dstPort = 7400;
        } else {
            //第一个直播设备 192.168.1.101
            //第二个直播设备 192.168.1.102   TODO
            this.ipOrDomain = "192.168.1.101";
            this.dstPort = 7408;
        }*/
        this.ipOrDomain = "192.168.1.101";
        this.dstPort = 7408;

        //报文
        controlStructSeat = new ControlStructSeat();//小端
        controlStructSeat.start = ByteUtil.hexToBytes("11ed");
        controlStructSeat.cmd = ByteUtil.hexToByte("02");
        controlStructSeat.time = 100;//运行时间 设置为100 ，一般取100~300左右, 动作太猛可以适当加大time的值（数值越小 速度越快）
    }

    /**
     * 发送六轴数据
     *
     * @param roll  [-180,180]
     * @param pitch [-90,90]
     * @param yaw   [-180,180]
     * @param surge [-170,170]
     * @param sway  [-170,170]
     * @param heave [-170,170]
     */
    public void sendUdp(float roll, float pitch, float yaw, float surge, float sway, float heave) {
        try {

            //六轴座椅
            //attitude[6] 为姿态数据数组 依次为：roll, pitch, yaw, surge, sway, heave
            //              ##20240126 顺序为：roll, pitch, yaw, sway, surge, heave
            //roll, pitch, yaw 取值范围（±16）
            //surge, sway, heave 取值范围（±170）
            controlStructSeat.attitude[0] = roll * 16 / 180;
            controlStructSeat.attitude[1] = pitch * 16 / 90;
            controlStructSeat.attitude[2] = yaw * 16 / 180;
            controlStructSeat.attitude[3] = sway;
            controlStructSeat.attitude[4] = surge;
            controlStructSeat.attitude[5] = heave;
            //小端
            byte[] data2 = JavaStruct.pack(controlStructSeat, ByteOrder.LITTLE_ENDIAN);
            //LogUtil.d(TAG, ByteUtil.bytesToHexWithSpace(data2), false);
            //发送
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
            }
            if (host == null) {
                host = InetAddress.getByName(ipOrDomain);
            }
            if (!socket.isClosed()) {
                DatagramPacket request = new DatagramPacket(data2, data2.length, host, dstPort);
                socket.send(request);
            }
        } catch (Exception e) {
            try {
                if (socket != null) {
                    LogUtil.e(TAG, socket + "==sendUdp===出错=");
                    socket.close();
                    socket = null;
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            host = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRollScale(float rollScale) {
        this.rollScale = rollScale;
    }

    public void setPitchScale(float pitchScale) {
        this.pitchScale = pitchScale;
    }

    public void setYawScale(float yawScale) {
        this.yawScale = yawScale;
    }

    public void setSurgeScale(float surgeScale) {
        this.surgeScale = surgeScale;
    }

    public void setSwayScale(float swayScale) {
        this.swayScale = swayScale;
    }

    public void setHeaveScale(float heaveScale) {
        this.heaveScale = heaveScale;
    }

    public void setRollMax(float rollMax) {
        this.rollMax = rollMax;
    }

    public void setPitchMax(float pitchMax) {
        this.pitchMax = pitchMax;
    }

    public void setYawMax(float yawMax) {
        this.yawMax = yawMax;
    }

    public void setSurgeMax(float surgeMax) {
        this.surgeMax = surgeMax;
    }

    public void setSwayMax(float swayMax) {
        this.swayMax = swayMax;
    }

    public void setHeaveMax(float heaveMax) {
        this.heaveMax = heaveMax;
    }
}

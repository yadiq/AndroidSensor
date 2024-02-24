package com.hqumath.demo.media.structBean;

import com.hqumath.demo.media.struct.StructClass;
import com.hqumath.demo.media.struct.StructField;

/**
 * 姿态座椅控制报文
 * 小端
 */
@StructClass
public class ControlStructSeat {
    /*struct UdpDef {
        uint8_t start1;
        uint8_t start2;
        uint8_t profile;
        uint8_t cmd;
        float attitude[6];  //pitch roll yaw surge sway heave
        float rpm[6];
        float pos_m[6];
        int  time;
        int  input;
        int  output;
    };*/

    @StructField(order = 0)
    public byte[] start = new byte[2];//包头 0x11ed

    @StructField(order = 1)
    public byte profile;

    @StructField(order = 2)
    public byte cmd;//0x02 更新姿态数据

    @StructField(order = 3)
    public float[] attitude = new float[6];//pitch roll yaw surge sway heave
    //pitch,roll, yaw 取值范围（±16） //surge, sway, heave 取值范围（±170）

    @StructField(order = 4)
    public float[] rpm = new float[6];

    @StructField(order = 5)
    public float[] pos_m = new float[6];

    @StructField(order = 6)
    public int time;//运行时间 设置为100 ，一般取100~300左右, 动作太猛可以适当加大time的值（数值越小 速度越快）

    @StructField(order = 7)
    public int input;

    @StructField(order = 8)
    public int output;
}

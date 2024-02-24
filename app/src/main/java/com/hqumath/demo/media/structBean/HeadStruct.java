package com.hqumath.demo.media.structBean;

import com.hqumath.demo.media.struct.StructClass;
import com.hqumath.demo.media.struct.StructField;
import com.hqumath.demo.utils.ByteUtil;

/**
 * ****************************************************************
 * 作    者: Created by gyd
 * 创建时间: 2022/3/22 10:57
 * 文件描述: 心跳报文，后面可补充有效负载
 * 注意事项:
 * ****************************************************************
 */
@StructClass
public class HeadStruct {
    @StructField(order = 0)
    public byte[] magic = new byte[6];

    @StructField(order = 1)
    public byte[] uuid = new byte[16];

    @StructField(order = 2)
    public byte type = 1;//1设备（不校验flashid）, 2手机（校验flashid）

    @StructField(order = 3)
    public byte[] random = new byte[2];

    @StructField(order = 4)
    public byte[] key = new byte[2];

    @StructField(order = 5)
    public byte[] flashid = new byte[8];//type=1时不校验；type=1时校验

    @Override
    public String toString() {
        return " magic:" + ByteUtil.bytesToHex(magic) +
                ", uuid：" + ByteUtil.bytesToHex(uuid) +
                ", type：" +type +
                ", random：" + ByteUtil.bytesToHex(random)+
                ", key：" + ByteUtil.bytesToHex(key)+
                ", flashid：" + ByteUtil.bytesToHex(flashid)
                ;
    }
}

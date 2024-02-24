package com.hqumath.demo.media.structBean;


import com.hqumath.demo.media.struct.StructClass;
import com.hqumath.demo.media.struct.StructField;
import com.hqumath.demo.utils.ByteUtil;

@StructClass
public class VideoStruct {
    /*

struct {
uint8_t cmd;
uint8_t devmac[6];
uint32_t ots;
uint32_t nts;
}
cmd: 1
ots: 同步前时间戳。
nts: 同步后时间戳。

     */


    @StructField(order = 0)
    public byte[] magic = new byte[6];

    @StructField(order = 1)
    public byte[] uuid = new byte[16];

    @StructField(order = 2)
    public byte type = 2;

    @StructField(order = 3)
    public byte[] random = new byte[2];

    @StructField(order = 4)
    public byte[] key = new byte[2];

    @StructField(order = 5)
    public byte[] flashid = new byte[8];

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

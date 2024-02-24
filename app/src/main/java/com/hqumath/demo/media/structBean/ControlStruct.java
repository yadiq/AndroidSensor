package com.hqumath.demo.media.structBean;

import com.hqumath.demo.media.struct.StructClass;
import com.hqumath.demo.media.struct.StructField;
import com.hqumath.demo.utils.ByteUtil;

@StructClass
public class ControlStruct {

    @StructField(order = 0)
    public byte[] magic = new byte[2];//车型 2xuint8_t 0x1A1B

    @StructField(order = 1)
    public byte[] channel = new byte[1];//通道 1xuint8_t 0x01

    @StructField(order = 2)
    public byte[] period = new byte[2];//周期 1xuint16_t 0x2710 (10000)

    @StructField(order = 3)
    public byte[] value = new byte[2];//通道值 1xuint16_t 0x05DC (1500)

    @StructField(order = 4)
    public byte crc;//累加校验取最后8位 1xuint8_t 0x00

    @Override
    public String toString() {
        return "ControlStruct{" +
                "magic=" + ByteUtil.bytesToHex(magic) +
                ", channel=" + ByteUtil.bytesToHex(channel) +
                ", period=" + ByteUtil.bytesToHex(period) +
                ", value=" + ByteUtil.bytesToHex(value) +
                ", crc=" + ByteUtil.byteToHex(crc) +
                '}';
    }
}

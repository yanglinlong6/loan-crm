package com.crm.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *  生成二维码工具类
 * @author zhangqiuping
 */
public class QRCodeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeUtil.class);

    /**生成图片类型为QRCode */
    private static BarcodeFormat FORMAT = BarcodeFormat.QR_CODE;

    /** 二维码基本参数设置 */
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

    /**
     * 初始化二维码配置参数
     */
    static {
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");// 设置编码字符集utf-8
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);// 设置纠错等级L/M/Q/H,纠错等级越高越不易识别，当前设置等级为最高等级H
        hints.put(EncodeHintType.MARGIN, 0);// 可设置范围为0-10，但仅四个变化0 1(2) 3(4 5 6) 7(8 9 10)
    }

    public static byte[] createQRCode(int width, int height, String content) {
        ByteArrayOutputStream os = null;
        byte[] bytes = null;
        try{
            // 创建位矩阵对象
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, FORMAT, width, height, hints);
            os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", os);
            bytes = os.toByteArray();
        }catch (Exception e){
            LOG.error("生成二维码异常:{}",e.getMessage(),e);
        }finally {
            try{
                if(null != os){
                    os.close();
                }
            }catch (Exception e1){
                LOG.error("生成二维码关闭流异常:{}",e1.getMessage(),e1);
            }
        }
        return bytes;

    }

//    public static void main(String[] args) throws WriterException, IOException {
//
//        byte[] b = createQRCode(200, 200, "http://crms.fxsk100.com/mobile/company?orgId=1");
//
//        OutputStream os = new FileOutputStream("C:\\dev\\bestme.png");
//        os.write(b);
//        os.close();
//
//    }

}



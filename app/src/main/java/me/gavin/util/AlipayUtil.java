package me.gavin.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 支付宝付款
 *
 * @author gavin.xiong 2017/11/22
 * @link {http://blog.csdn.net/u010399316/article/details/54892081}
 */
public class AlipayUtil {

    public static final String ALIPAY_CODE = "HTTPS://QR.ALIPAY.COM/FKX04883XNPJ5FKASHP538"; // 个人(收钱)

    /**
     * 支付宝付款
     *
     * @param context Context
     * @param qrCode  收款码（商家 | 个人（收钱） | 个人（收款码））
     * @return 是否成功
     */
    public static boolean alipay(Context context, String qrCode) {
        final String format = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=%s?_s=web-other&_t=%s";
        try {
            final String url = String.format(format, qrCode, System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.eg.android.AlipayGphone");
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}

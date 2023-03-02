package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;

import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000122613303";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCp1awi/ErpZnvxg9PZcBbbDMYxVmO/iYHlufY/QgUYV55+8DfEE1MWfLM9Ofv+Hr+QJNo0Nwam6dNzxEp/T7kZ7iOhvzT+SKr3kuvRsDa6OceKQMbBOaCYbqRUz+3/TabTUkDUMnr064G7iapBBUhBjZb8c/YH9+dhlf8xCRoIhu6fhCC/8ZR3LguG0iVfOIALkWGamjkQjolTDfiVKM0BHGepTCgZpIoBaXobVysu1ytq995snSImYC8ILHX60bDpBqIGqRnLx6xyd0tjS/TFP+rWXyF+89cMZLdCjPseRqVt5s9ZslESDWnnPPbr2fohdRJFdVb2ZWJiifAmyx95AgMBAAECggEBAJGVg6i94q1u16JxogGLj1wY+DBE4N7IzhwSLB+PxXRXb5ta6kLfWeM15CQA952f2BCFNrN50+7zE/rx8fZubbrno428rX2o5z2wqQun5VH/w6gk3Pu4HY1tktrNHzQx6WbO5JnB6BrZYu1TFioEkCr5Z8ti0C2GecbyYL41EibpsM1YUfz8hWHg00vouvkv0rzHn9uqBaZizPlnVyldm99TLbkrAPomJsi4f2RGUaZ0Ll0JNKTdobd2y+F+nGQgEEydMcZJjL/A0mbD16+zBLgUC0gd2f9LPNmz/VRhxTV29oh74Rs88QIw4Qv9nO0Iqpu0i1fg5u0dPJEJGfWU/u0CgYEA+KGtSjqwyXgBSnP4/4FQ6/UfeR2TtbdCIvax8Oh0VOurSgsNvgOWnBj+zverpa4NO10uX75ABv/aEInv5nvgqzFC3ZoEyWdIvDBCuCxggCH+voKi9ZdhzIBN62P1Dp4WqzAMnjknWSQguZGKIQfxveM3vs/av8LpUFRj02ReN4sCgYEArt4tdTY9RNFw8mTNpHKeZnBXUg0lWRKsk0pfQ0VHZRmknyeNbNjPQSmPb/Sx0vBa8nDPR2pHQf9PTGlKfcss/RyDw63uXOZXT7MOEnLjsFY0I43s8pjH/5RBc/hxrzuOQ3kRHh2H0Y0HJ2t7X9LUdKMdN9fGmzOz4BVeU2lPxYsCgYEAl+X2v/3hWgTWDQ2TV05DCRVFuDIItyER59h91+e8gtFEYcUlDSyWDCH9Mqb/mAYhEV6ZYWcTKPOQ3z/WcCLInO5IoqhhOka+mjKF4I1EG6QpRWz8cDyfRFgywpfb9KVCxi1h2Byk3IIE+DphgFKhL1itCwdj5noHHsuf/Vs5cIcCgYEAjELLpYOe+08fUF3IkMobAcHEWJ0QwvR2liIGbTfpf09lOv+HsbHp87N5SOy1oaOG34XazMaTZ314xqqBxIquiEtR6Sfe1Icxq0zM0cFzniJW6dnSH/iqGvd41KOH1G1pu/ck4sn/AfcjsNvGduLt5KxdKEicFisPcYLSSQdko+cCgYBnKHFqwRMCnP/X01JVlti9hdw2GMuiV2bN7ErcdKWDtuit4np/TwAYzL4I7XwgCoE8HbFFAsxIrVyXs4NII1ZqQkiIOTRSRBjra4uVlopcRj5xWZ7xdOfNXNRfkDxSdJboUiQxwAhxLv60CtgXuWMwh1cyDylNQAQbX1XabU/Ilw==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5PQxdalKdWbVagojAwq6UcrrWdlSw1dTrIxMEGgqSMttTx9qKH+KmEd7GD/NjyvdiIdL0SuUK0H1BgftsxUTEgKFj3iGhPQSejHg5fTMVPm+Rl/kF6JFCdeJjVOdP0Msmmp/x9krY+10zgb3YmeYFZOqMBCZ+w4AMTYB9hzddX0b4V4duFq2u8mlHdoWaq+/jJ2c4Le7rDLoqw2DXdHaB69kn8BWtcDYWcgEGCCf0itSZLBwQRW3KiYmLYxRt34sgyQofgF3L/yQADf7D5TqEZCMTObzdQ33XpDd7vWPivfqjRfrXQGqZKKutLeOJvsY59WCYiFHyjFARVPQQNoeQIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout= "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"time_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}

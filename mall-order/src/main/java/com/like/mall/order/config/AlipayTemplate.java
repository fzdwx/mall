package com.like.mall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.like.mall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000117603524";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDJ+SEaKC4mDLwlzvVi4IfzlmPn0tKzPkVWRT3b7OyrdhSLsq9Qu1TtdcLD2p0dZS1iC6DvLDFFwcsxiiwc57X+Cn67KCeeMeh1YwThLONSCBTg4yJLSOY9tMpC9nfmWlb4B6UEUSc8vdZAf0INNkzb6Caz2Y2y6H098CyPCH5fbIek826xND5bHcdRJVSaW6p0+IpBOEn1zjzVRKN9VexNraRLs6EVJiHb2h8mrVOL/yHt4VGa249O+udDpH3PTj2KTiAuLV3xuKODpNZgVm0FS0179eG0HG0JoUzb0x8y8E0X9TRdXP3qlWTMs8vHv7ldCkzyeLD+gY7dkOGGSYjAgMBAAECggEALnJtOfr3mAfGWm+kb+6U6YChNPybpGTMfSPsCeiUAd4Q8qHBiJ7v/yoxeqg98fpF7LfKiTrEeze5gcC3Xi3D/gXTkBpGk3iAci53Cd5/27PERu8aUOYcaipXA30/7jCdID1RduxamR5uPCY5HrPgi5enj8uNDjC1LVSj+DrVaU35grj7o4+0E+T7E3uEq3RRcxI3KH4bD1wbWNhVMfHXbPZ1+YQ4/MWf0LF0Nqt1yvi32k9R5OT9O5sYJKywTil7aSIgUWcekFzsXPb3PITZxC0p1SsIP8oCqS4e2U6AaWa17dzwAOiKGRWtbNxOL2dc8h92Xlo0uaYBZP9ApS7roQKBgQC3Tq5AuBngdhG5WPAkHBXnHrsUkkcFD6UZ6XHB+UjtDDmc/pB7P7rambiXslxbkVfg+qQGyG9IZpzweKNad7fsb6uuK6A/q/j8t8QGEuIbFFGIoQA2RqCqPeUtNIaePgWLm3A7TQTK9tZ4Cmm8N03sM5BH23xMjZkteKXn4aKBswKBgQC3Ks1bi4QElNvWz7et5pP64cPQFbq+PdyyD4zLHLsMsP2kHMIwn0XqutFa8/aghlaam5Fj/sZfyNRkXKcT8w2riGG0yCWTTr/e0ra734WS/mpJgHGEV7BrYTN01cMrhv+pfEUYpGN6oBheS4QAoMV0UV8SOMiPyI20wiUbYv4x0QKBgEetya/fDadbJAUhRQ4puVreBnO9222cN4hRY71hGGYxYJoGZUL8obl3YNpY0Yqw4/dCqz9Hw1RBY98YnW/z3oTjCfQadRhQzBUuWsWxPp8uK8/MfUO8DHllqKcrofRg9lQoHRPjQxuGuEjKcZw62AqkAyhGKG3a9On2ApIRBjXHAoGABdgUw0X5SVgih4ELID5KHGgVok7/LVC1+OxsFpLtGOOQxCqyu3Bpf6rEQT2lccu3ealbAzC+zZGPzT/ziLxphD6Ot4DnSgLqNkbM08O29FCvvXaeqJVPqK1qQeEMIBhnkdXIMpgUIHZ5MJECPi7i/eoWuXMcWxIQC2NiYbRP3fECgYAza259KSfqSei0RtJxa5dYg+cfT0UzqO8mNQRjHDh7GrlhQQ6pET5AKjt9ansG5KNPfdz0dSKGWaC7vFO9l/K9GDepaMqUxFxVXv3aqTxl/IAewdZQobir9t41Q4og0WJjnbqHchPaCjwpW4D/X1gnEmqby/llsATyhrnZxs+vrQ==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxOcV9cBYCuZ2nEoJHVxtuV/n64QtvL3gMXZLEjkhe/k2ro7/wVdIQXkbHqLBWFOr4PtFc+RH1qvm2q+15/Adjvv68obmBOg7CGxVksN+vUQSDsvWIRaOmDuPPLraxNGdobTQ7iUEMZvoRl5PPCe/oPjyOBOQVU7SfhqvrfZ+XmpGjwGQg2RdVx4jOAZWOKQ4iophwX8+Fvdt6AKHUIhMk1ViffnBu2pKMLRmIiaFQwC8AAehsX9Qj/ZRMIP13+eaUbWQzn1Dgm/YvCt0dhJkc+efQggGTF/BUel6+W0RdeJg51wGGlGwlP+a9nxaiREQsh9lluVUJU7AtouhNFXNqQIDAQAB";

    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url;

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url;

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

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

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}

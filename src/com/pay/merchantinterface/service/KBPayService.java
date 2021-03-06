package com.pay.merchantinterface.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import util.JWebConstant;

import com.PayConstant;
import com.pay.order.dao.PayOrder;
import com.pay.order.dao.PayProductOrder;
import com.third.kb.MerchantUtil;
import com.third.kb.Utils;
/**
 * 酷宝服务类
 * @author xk
 *
 */
public class KBPayService {
	private static final Log log = LogFactory.getLog(KBPayService.class);
	private static String pubKeyPath = JWebConstant.APP_PATH+JWebConstant.PATH_DIV+"config"+JWebConstant.PATH_DIV+PayConstant.PAY_CONFIG.get("KB_PUB_KEY");//公钥
	private static String priKeyPaht = JWebConstant.APP_PATH+JWebConstant.PATH_DIV+"config"+JWebConstant.PATH_DIV+PayConstant.PAY_CONFIG.get("KB_PRV_KEY");//商户私钥
	/**
	 * 微信/支付宝扫码下单
	 * @param request
	 * @param payRequest
	 * @param prdOrder
	 * @param payOrder
	 * @param productType
	 * @return
	 * @throws Exception 
	 */
	public String scanPay(HttpServletRequest request,PayRequest payRequest,PayProductOrder prdOrder,PayOrder payOrder,String productType) throws Exception{
		Map<String, String> beanMap = new HashMap<String, String>();
		String ProductName = PayConstant.PAY_CONFIG.get("COMMON_PRODUCT_DESC")==null||PayConstant.PAY_CONFIG.get("COMMON_PRODUCT_DESC").length()==0?
				"_":PayConstant.PAY_CONFIG.get("COMMON_PRODUCT_DESC");//商品名称
		beanMap.put("charset", "00");//字符集  00-GBK
		beanMap.put("version", "1.2");//接口版本
		beanMap.put("signType", "RSA");//签名方式
		beanMap.put("service", "newGatePayment");//业务类型
		beanMap.put("merchantId", PayConstant.PAY_CONFIG.get("KB_MERCHANTID"));//合作商户编号
		beanMap.put("subtype", productType);//业务子类型  12：微信扫码支付  22：支付宝扫码支付
		beanMap.put("offlineNotifyUrl", PayConstant.PAY_CONFIG.get("KB_OFFLINENOTIFYURL"));//后台通知 url
		beanMap.put("clientIP", PayConstant.PAY_CONFIG.get("KB_CLIENTIP"));//客户端ip
		beanMap.put("requestId", payOrder.payordno);//请求号
		beanMap.put("orderId", payOrder.payordno);//订单号
		beanMap.put("orderTime", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));//订单时间
		beanMap.put("totalAmount", String.valueOf(payOrder.txamt));//订单总金额，以分为单位
		beanMap.put("currency", "CNY");//交易币种
		beanMap.put("validUnit", "01");//订单有效期单位 00-分 01-小时 02-日 03-月
		beanMap.put("validNum", "1");//订单有效期数量
		beanMap.put("isRaw", "1");//是否为原生态  Subtype=12,22 时，0-返回二维码，1-返回支付链接
		beanMap.put("showUrl", PayConstant.PAY_CONFIG.get("KB_SHOWURL"));//前端跳转 url
		beanMap.put("productName", ProductName);//商品名称
		beanMap.put("productDesc", ProductName);//商品描述
		
		Map<String, String> configLoadMap = new HashMap<String, String>();
		configLoadMap.put("certPath", priKeyPaht);
		configLoadMap.put("rootCertPath", pubKeyPath);
		//签名
		Map<String, String> signMap = Utils.sign(beanMap, configLoadMap, log);
		log.info("酷宝微信/支付宝扫码请求数据："+signMap);
		/**
		 * {offlineNotifyUrl=127.0.0.1, requestId=20170704141530, signType=RSA, isRaw=1, charset=00, orderTime=20170704141530, version=1.2, currency=CNY, showUrl=127.0.0.1, 
		 * merchantSign=119B33A1D83AC07E4FFCE7B932E3D18EBC9DE67C6D81D8C7111325E1046ACC3C7E248A4A4D5A8E7925B7E32D28546978414050977545CF4DACC0043EA02FFE240D35DE081FE5FF57072217139ED5A3D03E2DFFEA20B8D17EF05025142C735E24AED00ECBD06C91232BFA9827F5B48629F4A21635598446A9D068C5F1FAEF14F5, 
		 * validNum=1, productDesc=scanPay, subtype=12, merchantCert=3082036F30820257A00302010202081BFDEE33F7D9814F300D06092A864886F70D0101050500305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F744341301E170D3137303330313037313030385A170D3138303330313037313030385A3064310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C0249543118301606035504030C0F38383830313030353831313030323730819F300D06092A864886F70D010101050003818D0030818902818100888E077BA7E7C64CE038845D19C8A5F1FE9CFF2FFF4DC37D2646423AFD4BD7207D6322D2E559EEF727A534905734CE29588CDA50393D2E548D179F5221F09092655FE4F5A7B1EB33704EB1E3A5BBE01E6CD875CFD1F0E41B6118C8922522E086383F8B0D683E87B70A5A106086E4F54F0225D276EDACC89DF6FA9E9D911D374F0203010001A381B13081AE301D0603551D0E041604145887E1DE7D3DCCCC254028510B26B3655A88DC0230818C0603551D23048184308181801407C69254961EB26785D105D969445BF72CF3CCA7A15FA45D305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F74434182081BF6C2A9D76A7CF2300D06092A864886F70D0101050500038201010019E90BA95981E2BB767FD50CF5667B9DABF7003688F55BDCD0FE31AAE08821EA1E77EB10F9E2050C948285598D9F41BA0B4BF6C615B811A3D686A40D7C0CA860CB9D498CEA6FE72D378B9F52B3AE23DB8CC0DE940496E8777ED546333891145659064A6FA4CE79FBB853B6AF9EE3FFBB22545A78EE00B4EEC89383D46DEECBCB7824E6AD456CCD37EAE63156CEB0D433AB6FE2AEFAC00CDB1FBEB60694F23112B074BB19BE4F4107F4F10AD8674CB499DD652C681D9F7A929110AB46400405A7D716E148B4C269AD94F2C520EAA827EFE58ECC81CD3AB121BFA62C151A1761F5B2B40D5896B54725D966D7AE03D8D5118A8AB0906A0ADA52B99F7730DE28A3EC, 
		 * totalAmount=1, service=newGatePayment, merchantId=888010058110027, validUnit=01, clientIP=124.202.181.158, productName=scanPay, orderId=20170704141530}
		 */
		String res = MerchantUtil.sendAndRecv(PayConstant.PAY_CONFIG.get("KB_SCANREQUESTURL"), signMap, PayConstant.PAY_CONFIG.get("KB_CHARSET"));
		/**
		 * version=1.2&charset=00&signType=RSA&status=SUCCESS&returnCode=000000&returnMessage=SUCCESS&merchantId=888010058110027&orderId=20170704141415&
		 * codeUrl=weixin://wxpay/bizpayurl?pr=lfiLBxo&
		 * serverCert=308203653082024DA00302010202081BF6C2A9D997293C300D06092A864886F70D0101050500305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F744341301E170D3135303131393130303530365A170D3435303131393130303530365A305A310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310E300C06035504030C05506179434130819F300D06092A864886F70D010101050003818D0030818902818100CEA9A82C9F5E6BF6AE9426EAF879CF0B8614B1C59818B841DBEE3BD44A09F03DEBD59871D975BFE42F19177342D00988A13912B153F0CC72C16EBD23C8596610F45978797414C5D211FB3DE1DA3944A8D75E7A0749EFE2E36DC46FCC42A373FFCD8F4B1C69E6D9789EEB51BF01AABE43E940652DE8F890004A965B07E49A614D0203010001A381B13081AE301D0603551D0E0416041439E6E108F96F61A1FBEA466C406666E545742BD530818C0603551D23048184308181801407C69254961EB26785D105D969445BF72CF3CCA7A15FA45D305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F74434182081BF6C2A9D76A7CF2300D06092A864886F70D010105050003820101003498678C1547E2D86CCBEE81971E54221EFBDFFF85F3BAA9BB5C6EC217E02DDD48641B132BB4AAF87BE91461304F7457554465F767D29E61266DC0212F8D1DAE06743B6D62125BE989DF80B889FCA6DC25D67F28581A8C44B400D754F3B0542F56603432131E690F8FC4D632717F914A199E080FABECCD4D931EF4288330B4E9CF684409E3CA2D026EDDD4DFC473C7ADEE759E15CD2937CA95DC2F347FAE4E67257CE9941E361EBAAEC1445CB01DBE74E3BDFF62E4FF6D378C0559E4D75AD42C10B78A1999280EA9A5C3FCE22808A722D3D9C9E8929CF02D0CDB7938E30478FBE21D5F53E7A1646F3510A6A4E062748453B23620273927D0F8A3A8622714DAEB&serverSign=4C00B39EBC477E67CAB0E98F17D8A54CD49F2B5D31B20A8B06B1730E97F0F57622E8A5162B3DEA7011465847CA63166D75B9684A145B9A53D04C46ED88245186624E7353C8AED6061045DDE5D561858857C383809395ECF60E6A99F56DB393CDBADCD9C44DD2974C0E318E3E87163DAC6BD141F354EB21C7D787BE477B4F86AB&
		 * requestId=20170704141415&ReqSignTyp=RSA
		 */
		log.info("酷宝微信/支付宝扫码响应数据："+res);
		if(!"".equals(res) && res != null){
			Map<String,String> resultMap = Utils.analysis(res, configLoadMap,beanMap.get("service"));
			//验签
			if(Utils.verifySign(res, configLoadMap, resultMap, PayConstant.PAY_CONFIG.get("KB_PRV_PASSWROD"), "GBK")){
				if("000000".equals(resultMap.get("returnCode")) && "SUCCESS".equals(resultMap.get("status"))){
					return "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
					"<message merchantId=\""+payRequest.merchantId+"\" merchantOrderId=\""+payRequest.merchantOrderId+"\" " +
					"codeUrl=\""+ resultMap.get("codeUrl") +"\" respCode=\"000\" respDesc=\"处理成功\"/>";
				}else{//业务处理错误。
					return "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>" +
		       		   "<message merchantId=\""+payRequest.merchantId+"\" merchantOrderId=\""+payRequest.merchantOrderId+"\" " +
		       			"codeUrl=\"\" respCode=\"-1\" respDesc=\""+resultMap.get("returnMessage")+"\"/>";  
				}
			}else{
				throw new Exception("酷宝微信/支付宝下单验签失败");
			}
		}else throw new Exception("(KB)渠道错误！");
	}
	/**
	 * 扫码查单
	 * @param payOrder
	 * @throws Exception
	 */
	public void channelQuery(PayOrder payOrder) throws Exception{
		Map<String, String> beanMap = new HashMap<String, String>();
		beanMap.put("charset", "00");//字符集  00-GBK
		beanMap.put("version", "1.2");//接口版本
		beanMap.put("signType", "RSA");//签名方式
		beanMap.put("service", "OrderSearch");//业务类型
		beanMap.put("requestId", payOrder.payordno);//请求号
		beanMap.put("merchantId", PayConstant.PAY_CONFIG.get("KB_MERCHANTID"));//合作商户编号
		beanMap.put("orderId", payOrder.payordno);//订单号
		
		Map<String, String> configLoadMap = new HashMap<String, String>();
		configLoadMap.put("certPath", priKeyPaht);
		configLoadMap.put("rootCertPath", pubKeyPath);
		//签名
		Map<String, String> signMap = Utils.sign(beanMap, configLoadMap, log);
		log.info("酷宝微信/支付宝扫码查单请求数据："+signMap);
		/**
		 * {merchantSign=62C83D361443CDD7D63D6724E625CA998F1607BF3F4E812091DC9E650AB0CD41A00BB415F09CEFE6DD363164B7EC77ED502FF6FE8E5DC34ECA88B50B868BC80B39636711996CA5ED23F2E862949E20B330C2B4C9322312DF8D6897CCAC1B4B6EECB9B48AB18C3DEB164468F8753E4D966CDEBD8BDB4BC272ACCC724E9AFB9784, requestId=20170704141530, signType=RSA, merchantCert=3082036F30820257A00302010202081BFDEE33F7D9814F300D06092A864886F70D0101050500305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F744341301E170D3137303330313037313030385A170D3138303330313037313030385A3064310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C0249543118301606035504030C0F38383830313030353831313030323730819F300D06092A864886F70D010101050003818D0030818902818100888E077BA7E7C64CE038845D19C8A5F1FE9CFF2FFF4DC37D2646423AFD4BD7207D6322D2E559EEF727A534905734CE29588CDA50393D2E548D179F5221F09092655FE4F5A7B1EB33704EB1E3A5BBE01E6CD875CFD1F0E41B6118C8922522E086383F8B0D683E87B70A5A106086E4F54F0225D276EDACC89DF6FA9E9D911D374F0203010001A381B13081AE301D0603551D0E041604145887E1DE7D3DCCCC254028510B26B3655A88DC0230818C0603551D23048184308181801407C69254961EB26785D105D969445BF72CF3CCA7A15FA45D305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F74434182081BF6C2A9D76A7CF2300D06092A864886F70D0101050500038201010019E90BA95981E2BB767FD50CF5667B9DABF7003688F55BDCD0FE31AAE08821EA1E77EB10F9E2050C948285598D9F41BA0B4BF6C615B811A3D686A40D7C0CA860CB9D498CEA6FE72D378B9F52B3AE23DB8CC0DE940496E8777ED546333891145659064A6FA4CE79FBB853B6AF9EE3FFBB22545A78EE00B4EEC89383D46DEECBCB7824E6AD456CCD37EAE63156CEB0D433AB6FE2AEFAC00CDB1FBEB60694F23112B074BB19BE4F4107F4F10AD8674CB499DD652C681D9F7A929110AB46400405A7D716E148B4C269AD94F2C520EAA827EFE58ECC81CD3AB121BFA62C151A1761F5B2B40D5896B54725D966D7AE03D8D5118A8AB0906A0ADA52B99F7730DE28A3EC, 
		 * service=OrderSearch, charset=00, merchantId=888010058110027, orderId=20170704141530, version=1.2}
		 */
		String res = MerchantUtil.sendAndRecv(PayConstant.PAY_CONFIG.get("KB_SCANREQUESTURL"), signMap, PayConstant.PAY_CONFIG.get("KB_CHARSET"));
		log.info("酷宝微信/支付宝扫码查单响应数据："+res);
		/**
		 * charset=00&merchantId=888010058110027&requestId=20170704141530&returnCode=000000&returnMessage=SUCCESS&signType=RSA&service=OrderSearch&version=1.2&totalAmount=1&bankAbbr=WFT&purchaserId=&orderId=20170704141530&payTime=&backParam=&status=WP&fee=0&
		 * serverCert=308203653082024DA00302010202081BF6C2A9D997293C300D06092A864886F70D0101050500305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F744341301E170D3135303131393130303530365A170D3435303131393130303530365A305A310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310E300C06035504030C05506179434130819F300D06092A864886F70D010101050003818D0030818902818100CEA9A82C9F5E6BF6AE9426EAF879CF0B8614B1C59818B841DBEE3BD44A09F03DEBD59871D975BFE42F19177342D00988A13912B153F0CC72C16EBD23C8596610F45978797414C5D211FB3DE1DA3944A8D75E7A0749EFE2E36DC46FCC42A373FFCD8F4B1C69E6D9789EEB51BF01AABE43E940652DE8F890004A965B07E49A614D0203010001A381B13081AE301D0603551D0E0416041439E6E108F96F61A1FBEA466C406666E545742BD530818C0603551D23048184308181801407C69254961EB26785D105D969445BF72CF3CCA7A15FA45D305B310B300906035504061302636E310B300906035504080C02626A3110300E06035504070C076265696A696E67310F300D060355040A0C064D75726F6E67310B3009060355040B0C024954310F300D06035504030C06726F6F74434182081BF6C2A9D76A7CF2300D06092A864886F70D010105050003820101003498678C1547E2D86CCBEE81971E54221EFBDFFF85F3BAA9BB5C6EC217E02DDD48641B132BB4AAF87BE91461304F7457554465F767D29E61266DC0212F8D1DAE06743B6D62125BE989DF80B889FCA6DC25D67F28581A8C44B400D754F3B0542F56603432131E690F8FC4D632717F914A199E080FABECCD4D931EF4288330B4E9CF684409E3CA2D026EDDD4DFC473C7ADEE759E15CD2937CA95DC2F347FAE4E67257CE9941E361EBAAEC1445CB01DBE74E3BDFF62E4FF6D378C0559E4D75AD42C10B78A1999280EA9A5C3FCE22808A722D3D9C9E8929CF02D0CDB7938E30478FBE21D5F53E7A1646F3510A6A4E062748453B23620273927D0F8A3A8622714DAEB&serverSign=6E9616FCA32E4F52BCFCE28D1E2AC982CAA800E69FB8BDEDF569EF4BB97C73A8E2D86BD1DD3251BDB4467A129E2FD7D784B83901579B8BF5F4FEA0C369EAE0F76957E18DCFEB309ED65EB548FEC0DCCE1620BD8257605D698B598E8ADE2883FB6F8CFF755EEECA285757F59EF30D976FB0B44205E61F7E77B2FF0DD2827F73C5
		 * &ReqSignTyp=RSA
		 */
		Map<String,String> resultMap = Utils.analysis(res, configLoadMap,beanMap.get("service"));
		log.info("解析后的map数据为："+resultMap);
		//验签
		if(Utils.verifySign(res, configLoadMap, resultMap, PayConstant.PAY_CONFIG.get("KB_PRV_PASSWROD"), "GBK")){
			if("000000".equals(resultMap.get("returnCode"))){//查询成功
				if("PD".equals(resultMap.get("status"))){
					payOrder.ordstatus="01";//支付成功
		        	new NotifyInterface().notifyMer(payOrder);//支付成功
				}else{
					payOrder.ordstatus="02";//支付失败。
		        	new NotifyInterface().notifyMer(payOrder);//支付失败。
				}
			}else throw new Exception("酷宝扫码查询请求失败");
		}else{
			throw new Exception("酷宝查单验签失败");
		}
	}
}
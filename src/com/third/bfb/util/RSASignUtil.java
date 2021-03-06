package com.third.bfb.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

/**
 * 
 * @author HisunTech R&D Center
 * @version version1.0
 * 
 * */
public class RSASignUtil {
	private String certFilePath = null;
	private String password = null;
	private String bfbServerCertPath = null;
	/**
	 * 
	 * HiRSASignUtil构造函数<br/>
	 * 
	 * @param CertFilePath
	 *            证书路径
	 * @param password
	 *            证书密钥
	 * 
	 * */
	public RSASignUtil(String certFilePath, String password) {
		this.certFilePath = certFilePath;
		this.password = password;

	}
	public RSASignUtil(String bfbServerCertPath) {
		this.bfbServerCertPath = bfbServerCertPath;

	}
	public RSASignUtil() {
	}
	public String getCerInfo(){
		try {
			CAP12CertTool c = new CAP12CertTool(certFilePath, password);
		     X509Certificate cert = c.getCert();
		     byte[] cr = cert.getEncoded();
		     return HexStringByte.byteToHex(cr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * 对一串原文进行RSA算法进行数字签名.<br/>
	 * 
	 * @param signData
	 *            String 签名原文
	 * @return String 签名结果
	 * @throws UnsupportedEncodingException
	 * 
	 * */
	public String sign(String indata, String encoding) {
		String singData = null;

		if (isEmpty(encoding)) {
			encoding = "GBK";
		}

		try {
			CAP12CertTool c = new CAP12CertTool(certFilePath, password);
			X509Certificate cert = c.getCert();

			byte[] si = null;
			try {
				si = c.getSignData(indata.getBytes(encoding));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] cr = cert.getEncoded();
			singData = HexStringByte.byteToHex(si);
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return singData;
	}  
	/**
	 * 交易类验签方法（快捷，网银）
	 * @param oridata
	 * @param signData
	 * @param svrCert
	 * @param encoding
	 * @return
	 */
	 public boolean verify(String oridata, String signData, String svrCert, String encoding)
	  {
	    boolean res = false;

	    if (isEmpty(encoding))
	    {
	      encoding = "GBK";
	    }
	    try
	    {
	      byte[] signDataBytes = HexStringByte.hexToByte(signData.getBytes());
	      byte[] inDataBytes = oridata.getBytes(encoding);

	      byte[] signaturepem = checkPEM(signDataBytes);
	      if (signaturepem != null) {
	        signDataBytes = Base64.decode(signaturepem);
	      }
	      X509Certificate cert = getCertFromHexString(svrCert);
	      if (cert != null) {
	        PublicKey pubKey = cert.getPublicKey();
	        Signature signet = Signature.getInstance("SHA1WITHRSA");
	        signet.initVerify(pubKey);
	        signet.update(inDataBytes);
	        res = signet.verify(signDataBytes);
	      }
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return res;
	  }

	/**
	 * 
	 * 对接收到的帮付宝平台返回报文做RSA的验签.<br/>
	 * 
	 * @param oridata
	 *            String 帮付宝平台返回报文
	 * @param signData
	 *            String 帮付宝平台返回的数据签名值
	 * @param serverCertPath
	 *            帮付宝平台服务器公钥证书
	 * @return true -验签正确，false -验签错误
	 * 
	 * */

	public boolean verifyBySoft(String strSignData, String stOriData,String encoding) {
		boolean res = false;
		try {
			byte[] signData = SecureUtil.base64Decode(strSignData.getBytes(encoding));
			byte[] oridata = SecureUtil.sha1X16(stOriData, encoding);
			X509Certificate serverCert = getCertfromPath(this.bfbServerCertPath);
			PublicKey publicKey = serverCert.getPublicKey();
			Signature st = Signature.getInstance("SHA1withRSA");
			st.initVerify(publicKey);
			st.update(oridata);
			res = st.verify(signData);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	

	public X509Certificate getCertfromPath(String crt_path)
			throws SecurityException {
		X509Certificate cert = null;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(new File(crt_path));
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cert;
	}

	public static PublicKey getPublicKeyfromPath(String svrCertpath)
			throws SecurityException {
		X509Certificate cert = null;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(new File(svrCertpath));
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(inStream);

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return cert.getPublicKey();
	}

	public boolean verifyCert(X509Certificate userCert, X509Certificate rootCert)
			throws SecurityException {
		boolean res = false;
		try {
			PublicKey rootKey = rootCert.getPublicKey();
			userCert.checkValidity();
			userCert.verify(rootKey);
			res = true;
			if (!userCert.getIssuerDN().equals(rootCert.getSubjectDN()))
				res = false;
		} catch (Exception e) {
			e.printStackTrace();
		} 

		return res;
	}

	private X509Certificate getCertFromHexString(String hexCert)
			throws SecurityException {
		ByteArrayInputStream bIn = null;
		X509Certificate certobj = null;
		try {
			byte[] cert = HexStringByte.hexToByte(hexCert.getBytes());
			CertificateFactory fact = CertificateFactory.getInstance("X.509");
			bIn = new ByteArrayInputStream(cert);
			certobj = (X509Certificate) fact.generateCertificate(bIn);
			bIn.close();
			bIn = null;
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bIn != null)
					bIn.close();
			} catch (IOException localIOException1) {
			}
		}
		return certobj;
	}

	public static byte[] checkPEM(byte[] paramArrayOfByte) {
		String str1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/+= \r\n-";
		for (int i = 0; i < paramArrayOfByte.length; i++)
			if (str1.indexOf(paramArrayOfByte[i]) == -1)
				return null;
		StringBuffer localStringBuffer = new StringBuffer(
				paramArrayOfByte.length);
		String str2 = new String(paramArrayOfByte);
		for (int j = 0; j < str2.length(); j++)
			if ((str2.charAt(j) != ' ') && (str2.charAt(j) != '\r')
					&& (str2.charAt(j) != '\n'))
				localStringBuffer.append(str2.charAt(j));
		return localStringBuffer.toString().getBytes();
	}

	public String getFormValue(String respMsg, String name) {
		String[] resArr = StringUtils.split(respMsg, "&");

		Map resMap = new HashMap();
		for (int i = 0; i < resArr.length; i++) {
			String data = resArr[i];
			int index = StringUtils.indexOf(data, '=');
			String nm = StringUtils.substring(data, 0, index);
			String val = StringUtils.substring(data, index + 1);
			resMap.put(nm, val);
		}

		return (String) resMap.get(name) == null ? "" : (String) resMap
				.get(name);
	}

	public String getValue(String respMsg, String name) {
		String[] resArr = StringUtils.split(respMsg, "&");

		Map resMap = new HashMap();
		for (int i = 0; i < resArr.length; i++) {
			String data = resArr[i];
			int index = StringUtils.indexOf(data, '=');
			String nm = StringUtils.substring(data, 0, index);
			String val = StringUtils.substring(data, index + 1);
			resMap.put(nm, val);
		}
		return (String) resMap.get(name) == null ? "" : (String) resMap
				.get(name);
	}

	public Map coverString2Map(String respMsg) {
		String[] resArr = StringUtils.split(respMsg, "&");

		Map resMap = new HashMap();
		for (int i = 0; i < resArr.length; i++) {
			String data = resArr[i];
			int index = StringUtils.indexOf(data, '=');
			String nm = StringUtils.substring(data, 0, index);
			String val = StringUtils.substring(data, index + 1);
			resMap.put(nm, val);
		}
		return resMap;
	}

	public  String coverMap2String(Map<String, String> data) {
		TreeMap tree = new TreeMap();
		Iterator it = data.entrySet().iterator();  //Map.entrySet  获得key_value集合
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			String value = "";
			if (!"signature".equals(((String) en.getKey()).trim())) {
				tree.put(en.getKey(), en.getValue());
			}
		}
		it = tree.entrySet().iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Map.Entry en = (Map.Entry) it.next();
			sf.append(new StringBuilder().append((String) en.getKey())
					.append("=").append((String) en.getValue()).append("&")
					.toString());
		}
		return sf.substring(0, sf.length() - 1);                                 //去掉最后的&
	}

	public static String encryptData(String dataString, String encoding,
			String svrCertPath) {
		byte[] data = null;
		try {
			data = encryptedPin(getPublicKeyfromPath(svrCertPath),
					dataString.getBytes(encoding));
			return new String(base64Encode(data), encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] encryptedPin(PublicKey publicKey, byte[] plainPin)
			throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding",
					new BouncyCastleProvider());

			cipher.init(1, publicKey);
			int blockSize = cipher.getBlockSize();
			int outputSize = cipher.getOutputSize(plainPin.length);
			int leavedSize = plainPin.length % blockSize;
			int blocksSize = leavedSize != 0 ? plainPin.length / blockSize + 1
					: plainPin.length / blockSize;

			byte[] raw = new byte[outputSize * blocksSize];
			int i = 0;
			while (plainPin.length - i * blockSize > 0) {
				if (plainPin.length - i * blockSize > blockSize) {
					cipher.doFinal(plainPin, i * blockSize, blockSize, raw, i
							* outputSize);
				} else {
					cipher.doFinal(plainPin, i * blockSize, plainPin.length - i
							* blockSize, raw, i * outputSize);
				}

				i++;
			}
			return raw;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static byte[] base64Encode(byte[] inputByte) throws IOException {
		return Base64.encode(inputByte);
	}

	public static boolean isEmpty(String s) {
		return (null == s) || ("".equals(s.trim()));
	}

	public String capSign(String stringData, String encoding) {

		if (isEmpty(encoding)) {
			encoding = "GBK";
		}
		byte[] byteSign = null;
		String stringSign = null;
		try {
			byte[] signDigest = SecureUtil.sha1X16(stringData, encoding);                //数据转16进制

			CAP12CertTool c = new CAP12CertTool(this.certFilePath,                       //数据价签
					this.password);
			byte[] signRes = SecureUtil.signBySoft(c.getPrivateKey(),                    //数据加签
					signDigest);
			byteSign = SecureUtil.base64Encode(signRes);   
			stringSign = new String(byteSign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringSign;
	}
	
	 public static Map<String,String> getValue(String respMsg)
	    {
	        String resArr[] = StringUtils.split(respMsg, "&");
	        Map<String,String> resMap = new HashMap<String,String>();
	        for(int i = 0; i < resArr.length; i++)
	        {
	            String data = resArr[i];
	            int index = StringUtils.indexOf(data, '=');
	            String nm = StringUtils.substring(data, 0, index);
	            String val = StringUtils.substring(data, index + 1);
	            resMap.put(nm, val);
	        }
	        return resMap;
	    }
}

package com.gp.common.base.utils;

import com.gp.common.base.param.GoogleBindParam;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.commons.codec.binary.Base32;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class GoogleAuthUtil {
	/**
	 * 随机生成一个密钥
	 */
	public static GoogleBindParam getSecretKey(String username, String host) {
		SecureRandom random = new SecureRandom();
		byte[]       bytes  = new byte[ 20 ];
		random.nextBytes( bytes );
		Base32 base32    = new Base32();
		String secretKey = base32.encodeToString( bytes ).toLowerCase();

        GoogleBindParam googleBindParam = new GoogleBindParam();
        googleBindParam.setGoogleKey(secretKey);
        String googleAuthenticatorBarCode = getGoogleAuthenticatorBarCode(secretKey, username, host);
        googleBindParam.setQrBarcodeBase(googleAuthenticatorBarCode);
		return googleBindParam;
	}
	/**
	 * 随机生成一个密钥
	 */
	public static String createSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[]       bytes  = new byte[ 20 ];
		random.nextBytes( bytes );
		Base32 base32    = new Base32();
		String secretKey = base32.encodeToString( bytes );
		return secretKey.toLowerCase();
	}
    public static String getGoogleAuthenticatorBarCode(String secretKey,
                                                       String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8")
                    .replace("+", "%20")
                    + "?secret="
                    + URLEncoder.encode(secretKey, "UTF-8").replace(
                    "+", "%20") + "&issuer="
                    + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }


    /**
	 * 验证验证码
	 */
	public static boolean verifyCode( String secretKey, int verificationCode ) {
		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		return gAuth.authorize( secretKey, verificationCode );
	}

//	public static String getQRBarcodeURL( String user, String host, String secret ) {
//		String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s";
//		return String.format( format, user, host, secret );
//	}

	public static String tranUrlToBase64String( String url ) {
		try {
			URL               urlImg            = new URL( url );
			HttpURLConnection httpURLConnection = ( HttpURLConnection ) urlImg.openConnection();
			httpURLConnection.addRequestProperty( "User-Agent", "Mozilla / 4.76" );
			InputStream is = httpURLConnection.getInputStream();
			//定义字节数组大小；
			byte[]                buffer                = new byte[ 1024 ];
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int                   rc                    = 0;
			while ( ( rc = is.read( buffer, 0, 100 ) ) > 0 ) {
				byteArrayOutputStream.write( buffer, 0, rc );
			}
			buffer = byteArrayOutputStream.toByteArray();
			return Base64Utils.encodeToString( buffer );
		} catch ( IOException e ) {
			e.printStackTrace();
		}


		return null;
	}
}

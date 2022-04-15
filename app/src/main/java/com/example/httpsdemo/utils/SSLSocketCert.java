package com.example.httpsdemo.utils;

import android.net.http.SslCertificate;
import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

public class SSLSocketCert {

	/**
	 * SSL证书错误，手动校验https证书
	 *
	 * @param cert https证书
	 * @return true通过，false失败
	 */
	public static String getSSLCertFromServer(SslCertificate cert) {
		Bundle bundle = SslCertificate.saveState(cert);
		if (bundle != null) {
			byte[] bytes = bundle.getByteArray("x509-certificate");
			if (bytes != null) {
				try {
					CertificateFactory cf = CertificateFactory.getInstance("X.509");
					Certificate ca = cf.generateCertificate(new ByteArrayInputStream(bytes));
					MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
					byte[] key = sha256.digest(ca.getEncoded());
					return bytesToHex(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String getSSLCertSHA256FromCert(InputStream cerIn) {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate ca = cf.generateCertificate(cerIn);
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] key = sha256.digest(ca.getEncoded());
			return bytesToHex(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字节数组转十六进制字符串
	 */
	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
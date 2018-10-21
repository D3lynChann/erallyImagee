package yao;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner.*;
import java.lang.Math;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import yao.gate.Gate;

public class Utils 
{
	public final static int sssize = 5;
	public static void swap(byte[] a,byte[] b)
	{
		byte[] h;
		h=a;
		a=b;
		b=h;
	}
	
	public static void printLut(Gate gate,String title)
	{
		System.out.println(title);
		for(int i=0;i<4;i++)
		{
			System.out.println(getHex(gate.getLutEntry(i)));
		}
		System.out.println();
	}
	
	public static String getHex(byte[] b)
	{
		String result="";
		for(int j=0;j<b.length;j++)
		{
			String hex="0"+Integer.toHexString(b[j]&255);
			result+=hex.substring(hex.length()-2);
		}
		return result;
	}
	
	public static boolean arraysAreEqual(byte[] a,byte[] b)
	{
		if(a.length!=b.length)
			return false;
		
		for(int i=0;i<a.length;i++)
			if(a[i]!=b[i])
				return false;
		
		return true;
	}
	
	public static byte[] genAESkey(int size) throws Exception
	{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(size); 
		
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		
		return raw;
	}
	public static byte[] AESdecrypt(byte[] encrypted,byte[] key) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] plain=cipher.doFinal(encrypted);
		return plain;
	}
	
	public static byte[] AESencrypt(byte[] plain,byte[] key) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted =cipher.doFinal(plain);
		return encrypted;
	}
	
	public static KeyPair genRSAkeypair() throws Exception
	{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		
                kpg.initialize(512);
		KeyPair kp = kpg.genKeyPair();
		
		return kp;
	}
	
	public static byte[] RSAencrypt(byte[] data,PublicKey key) throws Exception
	{		  
	  Cipher cipher = Cipher.getInstance("RSA");
	  cipher.init(Cipher.ENCRYPT_MODE, key);
	  byte[] cipherData = cipher.doFinal(data);
	  return cipherData;
	}
	
	public static byte[] RSAdecrypt(byte[] data,PrivateKey key) throws Exception
	{		  
	  Cipher cipher = Cipher.getInstance("RSA");
	  cipher.init(Cipher.DECRYPT_MODE, key);
	  byte[] cipherData = cipher.doFinal(data);
	  return cipherData;
	}

	
	public void bench(Gate gate1,byte[] in_a1,byte[] in_a2) throws Exception
	{
		long start=System.currentTimeMillis();
		
		for(int i=0;i<10000;i++)
			gate1.operate(in_a2, in_a1);
		
		System.out.println("10000 operations: "+(System.currentTimeMillis()-start)+" ms");
		
		start=System.currentTimeMillis();
		
		for(int i=0;i<10000;i++)
		{
			for(int j=0;j<4;j++)
			{
				Utils.AESdecrypt(gate1.getLutEntry(0),in_a2);
				Utils.AESdecrypt(gate1.getLutEntry(0),in_a2);
			}
		}
		
		System.out.println("10000 *4 *2 decryptions: "+(System.currentTimeMillis()-start)+" ms");
		
		start=System.currentTimeMillis();
		
		for(int i=0;i<10000;i++)
		{
			for(int j=0;j<4;j++)
			{
				Utils.AESencrypt(gate1.getLutEntry(0),in_a2);
				Utils.AESencrypt(gate1.getLutEntry(0),in_a2);
			}
		}
		
		System.out.println("10000 *4 *2 encryptions: "+(System.currentTimeMillis()-start)+" ms");
		start=System.currentTimeMillis();
		
		MessageDigest md = MessageDigest.getInstance( "SHA" );
		
		for(int i=0;i<10000;i++)
		{
			for(int j=0;j<4;j++)
			{
				md.reset();
				md.update(in_a1);
				md.update(in_a2);
		        
		        md.digest();
		        
		        md.reset();
				md.update(in_a1);
				md.update(in_a2);
		        
		        md.digest();
			}
		}
		System.out.println("10000 *4 *2 hashes: "+(System.currentTimeMillis()-start)+" ms");
	}
	public static void printAddRes(int size, boolean[] input, String title, boolean flag) {
		System.out.println(title);
		int tt  = 0;
		if (flag == false && input[0] == false)
			tt = 1;
		for (int ctr = tt; ctr < size; ctr++) {
			if (input[ctr]) System.out.print(1);
			else System.out.print(0);
		}
		System.out.println(' ');
	}

	public static boolean[] byteToBoolean(byte[] inputByte, int size) {
		boolean[] res = new boolean[size];
		for (int ctr = 0; ctr < size; ctr++)
			res[ctr] = inputByte[ctr] == 0 ? false : true;
		return  res;
	}

	public static byte[] BooleanToByte(boolean[] inputByte, int size) {
		byte[] res = new byte[size];
		for (int ctr = 0; ctr < size; ctr++)
			res[ctr] = (inputByte[ctr] == false ? (byte)0 : (byte)1);
		return  res;
	}


	public static long binaryToDecimal(int n){
		return Long.parseLong( Long.toBinaryString(n));
	}
	public static int DecFromBin(long n) {
		return Integer.parseInt(String.valueOf(n), 10);
	}
	public static int getWid(long input) {
		String s = String.valueOf(input);
		return s.length();
	}
	public static byte[] intTo2Byte(int input) {
		Long temp = binaryToDecimal(input);
		int size = getWid(temp);
		byte[] res = new byte[size];
		String temptemp = String.valueOf(temp);
		for (int ctr = 0; ctr < size; ctr++)
			if (temptemp.charAt(ctr) == '0') res[ctr] = 0;
			else res[ctr] = 1;
		return res;
	}
	public static long maxx(long input_a, long input_b) {
		if (input_a >= input_b) return input_a;
		return input_b;
	}
	public  static void biToBoo(int size, boolean[] booo, long input) {
		int tt = size - getWid(input);
		if (tt > 0)
			for (int ctr = 0; ctr < tt; ctr++)
				booo[ctr] = false;
		//System.out.println(input + ": " + tt);
		String temp = String.valueOf(input);
		for (int ctr = 0; ctr < size - tt; ctr++) {
			if (temp.charAt(ctr) == '0') booo[ctr + tt] = false;
			else booo[ctr + tt] = true;
		}
		//System.out.println("Asdasd");
	}

	public static byte[][] 	Wiid(byte[] input_a, byte[] input_b) {
		byte[][] res = new byte[2][];
		if (input_a.length > input_b.length) {
			res[0] = input_a;
			byte[] temp = new byte[input_a.length];
			for  (int ctr = 0; ctr < input_a.length - input_b.length; ctr++)
				temp[ctr] = 0;
			int cc = 0;
			for (int ctr = input_a.length - input_b.length; ctr < input_a.length; ctr++)
				temp[ctr] = input_b[cc++];
			res[1] = temp;
		}
		else {
			res[1] = input_b;
			byte[] temp = new byte[input_b.length];
			for  (int ctr = 0; ctr < input_b.length - input_a.length; ctr++)
				temp[ctr] = 0;
			int cc = 0;
			for (int ctr = input_b.length - input_a.length; ctr < input_b.length; ctr++)
				temp[ctr] = input_a[cc++];
			res[0] = temp;
		}
		return res;
	}
	public static int booToInt(boolean[] input, int size) {
		int res = 0, temp = 1;
		for (int ctr = size - 1; ctr >= 0; ctr--) {
			if (input[ctr]) res += temp;
			temp *= 2;
		}
		return res;
	}
	public static int bYTEToInt(byte[] input) {
		int res = 0, temp = 1;
		for (int ctr = input.length - 1; ctr >= 0; ctr--) {
			if (input[ctr] == 1) res += temp;
			temp *= 2;
		}
		return res;
	}
	public static void print_byte(byte[] input) {
		for (int ctr = 0; ctr < input.length; ctr++)
			System.out.print(input[ctr] + " ");
		System.out.println("");
	}
	public static String byte_to_string(byte[] input) {
		return String.valueOf(bYTEToInt(input));
	}
}

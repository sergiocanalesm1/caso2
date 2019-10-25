import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Cliente{

    private Socket connection;
    private final static String padding = "AES/ECB/PKCS5Padding";//algoritmos y formato
    private FileOutputStream archivoDeSalida;
    private ObjectOutputStream oos;
    private FileInputStream archivoDeEntrada;
    private ObjectInputStream ois;
    private static KeyGenerator keyGen;
    private static SecretKey KS;
    private PrintWriter pw;



    public Cliente(){
        try {
            connection = new Socket("localhost" , 6789);
            pw = new PrintWriter(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static byte[] cifrar(SecretKey KS, String m){
        try {

            Cipher cifrador = Cipher.getInstance(padding);
            cifrador.init(Cipher.ENCRYPT_MODE,KS);

            return cifrador.doFinal(m.getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();return null;
        }
    }

    private static byte[] descifrar(byte[] mCifrado, SecretKey KS){
        try {
            Cipher descifrador = Cipher.getInstance(padding);
            descifrador.init(Cipher.DECRYPT_MODE,KS);
            return descifrador.doFinal(mCifrado);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();return null;
        }
    }

    private static void imprimirBytes(byte[] bytes){
        String s = "";
        for(int i = 0; i < bytes.length; i++){
            s += bytes[i];
        }
        System.out.println(s);
    }

    private static void generate(){//genera llaves con algoritmo AES
        try {
            keyGen = KeyGenerator.getInstance("AES");
            KS = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        generate();
        String s = "esto lo quiero cifrar";
        byte[] mCifrado = cifrar(KS,s);
        imprimirBytes(mCifrado);
        byte[] mDescifrado = descifrar(mCifrado,KS);
        imprimirBytes(mDescifrado);
        System.out.println(new String(mDescifrado));

    }


}

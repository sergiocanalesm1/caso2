import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class Cliente{

    private static Socket connection;
    private final static String padding = "AES/ECB/PKCS5Padding";//algoritmos y formato
    //private FileOutputStream archivoDeSalida;
    //private ObjectOutputStream oos;
    //private FileInputStream archivoDeEntrada;
    //private ObjectInputStream ois;
    private static KeyGenerator keyGen;
    private static SecretKey KS;
    private static PrintWriter pw;
    private static BufferedReader bf;
    private static InputStreamReader in;



    public Cliente(){

    }


    private static byte[] cifrarSimetrico(SecretKey KS, String m){
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

    private static byte[] descifrarSimetrico(byte[] mCifrado, SecretKey KS){
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

    //genera llaves con algoritmo AES
    private static void generateSimetricKey(){
        try {
            keyGen = KeyGenerator.getInstance("AES");
            KS = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){

        //String s = "esto lo quiero cifrar";
        //byte[] mCifrado = cifrar(KS,s);
        //imprimirBytes(mCifrado);
        //byte[] mDescifrado = descifrar(mCifrado,KS);
       // imprimirBytes(mDescifrado);
        //System.out.println(new String(mDescifrado));

        //1: establecer conexión
        alistarConexion();
        pw.println("HOLA");
        pw.flush();

        //leer OK
        try {
            System.out.println(bf.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }

        //2: mandar algoritmos en orden- simétrico, asimétrico y hmac
        String algoritmos = "ALGORITMOS:AES:RSA:HMACSHA256";
        pw.println(algoritmos);
        pw.flush();

        try {

            //leer OK
            System.out.println(bf.readLine());

            //leer certificado digital
            String CD = bf.readLine();
            System.out.println(CD);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //obtener llave de sesión a través del CD usando el estándar X509





        //Cerrar
        pw.close();
        try {
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static void alistarConexion() {
        try {

            connection = new Socket("localhost", 6789);
            pw = new PrintWriter(connection.getOutputStream());
            in = new InputStreamReader(connection.getInputStream());
            bf = new BufferedReader(in);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

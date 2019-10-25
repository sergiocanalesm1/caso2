import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

    //misma vaina que el simétrico pero mandando algoritmo al cifrador
    public static byte[] cifrarAsimetrico(Key pk, String algoritmo, String m){
        try {
            Cipher cifrador = Cipher.getInstance(algoritmo);
            cifrador.init(Cipher.ENCRYPT_MODE, pk);

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
            System.out.println(CD.length());



            try {

                //establecer estándar
                CertificateFactory f = CertificateFactory.getInstance("X.509");

                //extraer K+
                byte[] bytesCD = DatatypeConverter.parseBase64Binary(CD);

                InputStream input = new ByteArrayInputStream(bytesCD);
                X509Certificate certificate = (X509Certificate)f.generateCertificate(input);
                PublicKey pk = certificate.getPublicKey();
                imprimirBytes(pk.getEncoded());

                //generar llave de sesión
                generateSimetricKey();

                //cifrar KS con la llave publica pk: cifrado asimétrico



                String algoritmoAsimetrico = "RSA";

                String mensajeACifrar = DatatypeConverter.printBase64Binary(KS.getEncoded());
                byte[] bytesMensajeCifrado = cifrarAsimetrico(pk,algoritmoAsimetrico,mensajeACifrar);

                imprimirBytes(bytesMensajeCifrado);

                //se envia la llave de sesión cifrada asimétricamente
                pw.println(new String(bytesMensajeCifrado));
                pw.flush();

                //verificar que funciona el canal
                pw.println("reto");
                pw.flush();
                String prueba = bf.readLine();

                System.out.println("esto debería ser igual a prueba");
                imprimirBytes(descifrarSimetrico(prueba.getBytes(),KS));





            } catch (CertificateException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }













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

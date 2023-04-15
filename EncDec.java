import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

class EncDec {
    private static final String algTyp = "AES";
    private static final String keyDerFun = "PBKDF2WithHmacSHA256";
    private static final String algMod = "AES/GCM/NoPadding";
    private static final int autTagLen = 128;
    private static final int iniVecLen = 12;
    private static final int salLen = 16;
    private static final int iteCou = 65536;
    private static final int keyLen = 256;

    private static byte[] getSecRanByt(int len) {
        byte[] secRanByt = new byte[len];
        new SecureRandom().nextBytes(secRanByt);
        return secRanByt;
    }

    private static SecretKey getKey(char[] pas, byte[] sal) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory selFac = SecretKeyFactory.getInstance(keyDerFun);
        KeySpec spe = new PBEKeySpec(pas, sal, iteCou, keyLen);
        SecretKey key = new SecretKeySpec(selFac.generateSecret(spe).getEncoded(), algTyp);
        return key;
    }

    private static byte[] encByt(byte[] filByt, String pas) throws Exception {
        byte[] sal = getSecRanByt(salLen);
        byte[] iniVec = getSecRanByt(iniVecLen);
        SecretKey key = getKey(pas.toCharArray(), sal);
        Cipher cip = Cipher.getInstance(algMod);
        cip.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(autTagLen, iniVec));
        byte[] encByt = cip.doFinal(filByt);
        byte[] encBytIniVecSal = ByteBuffer.allocate(iniVec.length + sal.length + encByt.length).put(iniVec).put(sal)
                .put(encByt).array();
        return encBytIniVecSal;
    }

    private static byte[] decByt(byte[] encBytIniVecSal, String pas) throws Exception {
        ByteBuffer bytBuf = ByteBuffer.wrap(encBytIniVecSal);
        byte[] iniVec = new byte[iniVecLen];
        bytBuf.get(iniVec);
        byte[] sal = new byte[salLen];
        bytBuf.get(sal);
        byte[] encByt = new byte[bytBuf.remaining()];
        bytBuf.get(encByt);
        SecretKey key = getKey(pas.toCharArray(), sal);
        Cipher cip = Cipher.getInstance(algMod);
        cip.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(autTagLen, iniVec));
        byte[] decByt = cip.doFinal(encByt);
        return decByt;
    }

    static void encFil(File fil, String pas) throws Exception {
        byte[] filByt = Files.readAllBytes(fil.toPath());
        byte[] encByt = encByt(filByt, pas);
        Files.write(fil.toPath(), encByt);
    }

    static void decFil(File fil, String pas) throws Exception {
        byte[] filByt = Files.readAllBytes(fil.toPath());
        byte[] decByt = decByt(filByt, pas);
        Files.write(fil.toPath(), decByt);
    }
}

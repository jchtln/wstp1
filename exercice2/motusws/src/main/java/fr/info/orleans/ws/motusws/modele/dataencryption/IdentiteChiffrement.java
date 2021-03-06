package fr.info.orleans.ws.motusws.modele.dataencryption;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.info.orleans.ws.motusws.modele.exceptions.IdentiteInvalide;
import fr.info.orleans.ws.motusws.modele.exceptions.TicketInvalideException;
import fr.info.orleans.ws.motusws.modele.modele.Joueur;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

public class IdentiteChiffrement {

    private Cipher cipher; // Chiffrement utilisé
    private SecretKeySpec secretKey; // Clé secrète
    private ObjectMapper objectMapper; // Permet de transformer un objet en chaîne de caractère JSON et réciproquement

    public IdentiteChiffrement(String secret) {
        byte[] key;
        try {
            cipher = getInstance("AES/ECB/PKCS5Padding");
            key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key,"AES");
            objectMapper = new ObjectMapper();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public String chiffrement(Joueur joueur) {
        try {
            cipher.init(ENCRYPT_MODE,
                    secretKey);
            String strToEncrypt = this.objectMapper.writeValueAsString(joueur);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (InvalidKeyException | JsonProcessingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Encryption problem!");
    }

    /**
     * Permet de déchiffrer un token
     * @param token
     * @return l'invitation extraite du token
     * @throws TicketInvalideException : Le token n'est pas déchiffrable
     */
    public Joueur dechiffrement(String token) throws IdentiteInvalide {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String jsonString = new String(cipher.doFinal(Base64.getDecoder().decode(token)));
            return objectMapper.readerFor(Joueur.class).readValue(jsonString);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IdentiteInvalide();
    }

}

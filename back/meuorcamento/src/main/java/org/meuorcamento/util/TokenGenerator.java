package org.meuorcamento.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.meuorcamento.model.Usuario;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenGenerator {

	 protected static SecureRandom random = new SecureRandom();
	    
	    public static  synchronized String generateToken( String word ) {
	            long longToken = Math.abs( random.nextLong() ) + word.length();
	            String random = Long.toString( longToken, 16 );
	            return  random;
	    }
	    
		public static synchronized String digester(String original) throws UnsupportedEncodingException, NoSuchAlgorithmException {
			MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
			byte messageDigest[] = algorithm.digest(original.getBytes("UTF-8"));
			 
			StringBuilder hexString = new StringBuilder();
			for (byte b : messageDigest) {
			  hexString.append(String.format("%02X", 0xFF & b));
			}
			return hexString.toString();
		}
		
		public static synchronized String generateHash(Usuario usuario) {
			String token = "";
			long tokenTempoExpirar = 1;
			Date data = Date.from(LocalDateTime.now().plusMinutes(tokenTempoExpirar).atZone(ZoneId.systemDefault()).toInstant());
			try {
			    Algorithm algorithm = Algorithm.HMAC256("SECRET");
			    token = JWT.create()
			        .withClaim("login", usuario.getLogin())
			        .withExpiresAt(data)
			        .withSubject(usuario.getSenha())
			        .sign(algorithm);
			} catch (Exception exception){}
			
			return token;
		}
				
		public static synchronized String DecodeHash(String word) {
			String payload ="";
			try {
			    DecodedJWT jwt = JWT.decode(word);
			    payload = jwt.getPayload();
			} catch (JWTDecodeException exception){
			    //Invalid token
			}
			return payload;
		}
		
		public static synchronized DecodedJWT ValidateHash(String token) throws IllegalArgumentException, UnsupportedEncodingException {
		    Algorithm algorithm = Algorithm.HMAC256("SECRET");
		    JWTVerifier verifier = JWT.require(algorithm).build();
		    DecodedJWT jwt = verifier.verify(token);
			   
			return jwt;
		}
	
}

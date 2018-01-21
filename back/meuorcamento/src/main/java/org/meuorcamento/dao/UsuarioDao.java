package org.meuorcamento.dao;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meuorcamento.model.Usuario;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Stateful
public class UsuarioDao {

	@PersistenceContext
	private EntityManager em;

	public void inserir(Usuario usuario) {
		Usuario usuarioReady = prepararUsuario(usuario);
		usuarioReady.setEstado(true);
		em.persist(usuarioReady);
	}
	
	public String loga(Usuario usuario) {
		
		String generateHash = "";
		Usuario usuarioReady = prepararUsuario(usuario);
		Query q = em.createQuery("select u from Usuario u where u.nome = :param1 and u.senha = :param2");
		q.setParameter("param1", usuarioReady.getNome());
		q.setParameter("param2", usuarioReady.getSenha());
		Usuario singleResult = (Usuario) q.getSingleResult();
		singleResult.setUltimoAcesso(LocalDate.now());
		if(singleResult.isEstado()) {
			em.merge(em.find(Usuario.class, singleResult.getId()));
			generateHash = generateHash(singleResult);
		}else {
			generateHash = null;
		}
		
		return generateHash;
	}
	
	public boolean valida(String token) {
		boolean isValid = false;
		DecodedJWT hashIsValid;
		try {
			hashIsValid = ValidateHash(token);
			Query q = em.createQuery("select u from Usuario u where u.nome = :param1 and u.senha = :param2");
			Claim claim = hashIsValid.getClaim("nome");
			String nome = claim.isNull() ? null : claim.asString();
			String subject = hashIsValid.getSubject();
			if(nome != null && subject != null) {
				q.setParameter("param1", nome);
				q.setParameter("param2", subject);
				Usuario singleResult = (Usuario) q.getSingleResult();
				isValid = singleResult.getNome() != null ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("DAO: " + isValid);
		return isValid;
	}
	

	private Usuario prepararUsuario(Usuario usuario) {
		String hash = "";
		try {
			hash = digester(usuario.getSenha());
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		usuario.setSenha(hash);
		return usuario;
	}
	
	
	private String digester(String original) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
		byte messageDigest[] = algorithm.digest(original.getBytes("UTF-8"));
		 
		StringBuilder hexString = new StringBuilder();
		for (byte b : messageDigest) {
		  hexString.append(String.format("%02X", 0xFF & b));
		}
		return hexString.toString();
	}

	private String generateHash(Usuario usuario) {
		String token = "";
		long tokenTempoExpirar = 1;
		Date data = Date.from(LocalDateTime.now().plusMinutes(tokenTempoExpirar).atZone(ZoneId.systemDefault()).toInstant());
		try {
		    Algorithm algorithm = Algorithm.HMAC256("SECRET");
		    token = JWT.create()
		        .withClaim("nome", usuario.getNome())
		        .withExpiresAt(data)
		        .withSubject(usuario.getSenha())
		        .sign(algorithm);
		} catch (Exception exception){
		    //UTF-8 encoding not supported
		}
		return token;
	}
	
	
	private String DecodeHash(String word) {
		String payload ="";
		try {
		    DecodedJWT jwt = JWT.decode(word);
		    payload = jwt.getPayload();
		} catch (JWTDecodeException exception){
		    //Invalid token
		}
		return payload;
	}
	
	private DecodedJWT ValidateHash(String token) throws IllegalArgumentException, UnsupportedEncodingException {
	    Algorithm algorithm = Algorithm.HMAC256("SECRET");
	    JWTVerifier verifier = JWT.require(algorithm).build();
	    DecodedJWT jwt = verifier.verify(token);
		   
		return jwt;
	}
}

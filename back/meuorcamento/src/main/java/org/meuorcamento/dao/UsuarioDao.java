package org.meuorcamento.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meuorcamento.model.Usuario;
import org.meuorcamento.util.TokenGenerator;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Stateful
public class UsuarioDao {

	@PersistenceContext
	private EntityManager em;

	public boolean inserir(Usuario usuario) {
		boolean isOk = false;
		try {
			Usuario usuarioReady = prepararUsuario(usuario);
			usuarioReady.setEstado(true);
			em.persist(usuarioReady);
			isOk = true;
		} catch (Exception e) {
			isOk = false;
		}
		return isOk;
	}
	
	public String loga(Usuario usuario) {
		
		String generateHash = "";
		Usuario usuarioReady = prepararUsuario(usuario);
		Query query = em.createQuery("select u from Usuario u where u.login = :param1 and u.senha = :param2");
		query.setParameter("param1", usuarioReady.getLogin());
		query.setParameter("param2", usuarioReady.getSenha());
		Usuario singleResult = (Usuario) query.getSingleResult();
		singleResult.setUltimoAcesso(LocalDate.now());
		if(singleResult.isEstado()) {
			em.merge(em.find(Usuario.class, singleResult.getId()));
			generateHash = TokenGenerator.generateHash(singleResult);
		}else {
			generateHash = null;
		}
		return generateHash;
	}
	
	public Usuario valida(String token) {
		Usuario singleResult = null;
		try {
			DecodedJWT hashIsValid = TokenGenerator.ValidateHash(token);
			Claim claim = hashIsValid.getClaim("login");
			String login = claim.isNull() ? null : claim.asString();
			String subject = hashIsValid.getSubject();
			if(login != null && subject != null) {
				Query query = em.createQuery("select u from Usuario u where u.login = :param1 and u.senha = :param2");
				query.setParameter("param1", login);
				query.setParameter("param2", subject);
				
				singleResult = (Usuario) query.getSingleResult();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return singleResult;
	}
	

	private Usuario prepararUsuario(Usuario usuario) {
		try {
			String hash = TokenGenerator.digester(usuario.getSenha());
			usuario.setSenha(hash);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return usuario;
	}
	


}

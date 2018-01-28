package org.meuorcamento.ws.resource;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.JSONObject;
import org.meuorcamento.dao.UsuarioDao;
import org.meuorcamento.model.Usuario;

@Path("usuario")
@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
public class UsuarioResource {

	@Inject
	private UsuarioDao dao;
	
	@POST
	@Path("/gerar")
	public Response geraSenha(@Valid Usuario usuario) {
		System.out.println("Gerando usuario: " + usuario.getId() + " - " + usuario.getLogin() + " - " + usuario.getNome() + " - " + usuario.getSenha() + " - " + usuario.isEstado());
		boolean validar = dao.inserir(usuario);
		if(validar) {
			return Response.ok().build();
		}else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	@PUT
	@Path("/login")
	public Response getUsuario(@Valid Usuario usuario) {
		String usuarioValido = dao.loga(usuario);
		boolean validar = usuarioValido != null ? true : false;
		
		System.out.println("dmedeiros :: getUsuario - token : " + usuarioValido);
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("authtoken", usuarioValido);
		if(validar) {
			return Response.ok(jsonObject.toString()).build();
		}else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}
	
	@GET
	@Path("/verificar")
	public Response verificarUsuario(@QueryParam("XTOKEN")String token) {
		String usuarioValido = dao.valida(token);
		System.out.println("dmedeiros :: verificarUsuario - token : " + usuarioValido);
		boolean validar = usuarioValido != null ? true : false;
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("authtoken", usuarioValido);
		if(validar) {
			return Response.ok(jsonObject.toString()).build();
		}else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}


	
}

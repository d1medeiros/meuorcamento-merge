package org.meuorcamento.ws.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.meuorcamento.dao.ContaDao;
import org.meuorcamento.dao.UsuarioDao;
import org.meuorcamento.model.Conta;
import org.meuorcamento.model.Usuario;

@Path("conta")
@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
public class ContaResource {
	
	@Inject
	private ContaDao dao;
	@Inject
	private UsuarioDao usuarioDao;
	private static Status STATUS_CODE;

	
	@GET
	@Path("/atual") 
	public List<Conta> getContasAtual(@HeaderParam("XTOKEN")String token) {
		Usuario validaUsuario = validaUsuario(token);
		return preparaLista(dao.listaMesAtual(validaUsuario));
	}

	@GET
	@Path("{id}")
	public Conta getConta(@PathParam("id") int id, @HeaderParam("XTOKEN")String token) {
		if(validaUsuario(token).getNome() != null) {
			return dao.getContaById(id);
		}else {
			return new Conta();
		}
	}
	
	@GET
	@Path("/all")
	public List<Conta> getContas(@QueryParam("ids") String ids, @HeaderParam("XTOKEN")String token) {
			Optional<String> o = Optional.of(ids);
			if(o.isPresent() && validaUsuario(token).getNome() != null) {
				String[] split = ids.split("-");
				List<Integer> collect = Stream.of(split).map(s -> Integer.valueOf(s)).collect(Collectors.toList());
				System.out.println(collect);
				return dao.getContaByIds( collect );
			}else {
				return Arrays.asList(new Conta());
			}
	}

	@POST
	@Path("/remove/{id}")
	public Response removeConta(@PathParam("id") int id, @HeaderParam("XTOKEN")String token) {
		STATUS_CODE = Status.FORBIDDEN;
		Optional.of(validaUsuario(token))
				.ifPresent(u -> {
					dao.remove(id);
					STATUS_CODE = Status.ACCEPTED;
				});
		return  Response.status(STATUS_CODE).build();
	}
	
	@POST
	@Path("/remove/todos/{id}")
	public Response removeAllConta(@PathParam("id") int id, @HeaderParam("XTOKEN")String token) {
		STATUS_CODE = Status.FORBIDDEN;
		Optional.of(validaUsuario(token))
				.ifPresent(u -> {
					dao.removeAll(id);
					STATUS_CODE = Status.ACCEPTED;
				});
		return  Response.status(STATUS_CODE).build();
	}
	
	@GET
	@Path("/mesano/{mesAno}")
	public List<Conta> getContasPorNumero(@PathParam("mesAno") String mesAno, @HeaderParam("XTOKEN")String token) {
		int mes = Integer.valueOf(mesAno.split("-")[0]);
		int ano = Integer.valueOf(mesAno.split("-")[1]);
		Usuario validaUsuario = validaUsuario(token);
		return preparaLista(dao.listaMesPorNumero(mes, ano, validaUsuario));
	}
	
	@GET
	@Path("/seisMeses")
	public List<Conta> getContasAll(@HeaderParam("XTOKEN")String token) {
		Usuario validaUsuario = validaUsuario(token);
		return preparaLista(dao.listaTodos(validaUsuario));
	}
	
	@POST
	@Path("/salva")
	public Response salva(@Valid Conta conta, @HeaderParam("XTOKEN")String token) {
		dao.inserir(inserirUsuario(conta, token));
		return Response.noContent().build();
	}
	
	@POST
	@Path("/altera")
	public Response altera(@Valid Conta conta, @HeaderParam("XTOKEN")String token) {
		dao.alterar(inserirUsuario(conta, token));
		return Response.noContent().build();
	}
	
	@POST
	@Path("/altera/todos")
	public Response alteraTodos(@Valid Conta conta, @HeaderParam("XTOKEN")String token) {
		dao.alterarAll(inserirUsuario(conta, token));
		return Response.noContent().build();
	}

	private Conta inserirUsuario(Conta conta, String token) {
		Usuario usuarioValido = usuarioDao.valida(token);
		conta.setUsuario(usuarioValido);
		return conta;
	}
	
	private Usuario validaUsuario(String token) {
		Usuario usuarioValido = usuarioDao.valida(token);
		return usuarioValido;
	}
	
	private Conta removeUsuarioDaConta(Conta conta) {
		conta.setUsuario(new Usuario());
		return conta;
	}
	
	private List<Conta> preparaLista(List<Conta> lista){
		return lista.stream().map(conta -> removeUsuarioDaConta(conta)).collect(Collectors.toList());
	}
}

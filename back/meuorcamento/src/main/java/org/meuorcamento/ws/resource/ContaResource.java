package org.meuorcamento.ws.resource;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.meuorcamento.dao.ContaDao;
import org.meuorcamento.model.Conta;
import org.meuorcamento.model.TipoConta;
import org.meuorcamento.util.TokenGenerator;

@Path("conta")
@Produces({MediaType.APPLICATION_JSON })
@Consumes({MediaType.APPLICATION_JSON })
public class ContaResource {
	
	@Inject
	private ContaDao dao;


	@GET
	@Path("/atual")
	public List<Conta> getContasAtual() {
		return dao.listaMesAtual();
	}

	@GET
	@Path("{id}")
	public Conta getConta(@PathParam("id") int id) {
		return dao.getContaById(id);
	}

	@POST
	@Path("/remove/{id}")
	public Response removeConta(@PathParam("id") int id) {
		dao.remove(id);
		return Response.noContent().build();
	}
	
	@POST
	@Path("/remove/todos/{id}")
	public Response removeAllConta(@PathParam("id") int id) {
		dao.removeAll(id);
		return Response.noContent().build();
	}
	
	@GET
	@Path("/mesano/{mesAno}")
	public List<Conta> getContasPorNumero(@PathParam("mesAno") String mesAno) {
		int mes = Integer.valueOf(mesAno.split("-")[0]);
		int ano = Integer.valueOf(mesAno.split("-")[1]);
		return dao.listaMesPorNumero(mes, ano);
	}
	
	@GET
	@Path("/seisMeses")
	public List<Conta> getContasAll() {
		return dao.listaTodos();
	}
	
	@POST
	@Path("/salva")
	@Produces({MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON })
	public Response salva(@Valid Conta conta) {
		
		dao.inserir(conta);
		
		return Response.noContent().build();
	}
	
	@POST
	@Path("/altera")
	@Produces({MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON })
	public Response altera(@Valid Conta conta) {
		dao.alterar(conta);
		return Response.noContent().build();
	}
	
	@POST
	@Path("/altera/todos")
	@Produces({MediaType.APPLICATION_JSON })
	@Consumes({MediaType.APPLICATION_JSON })
	public Response alteraTodos(@Valid Conta conta) {
		dao.alterarAll(conta);
		return Response.noContent().build();
	}


}

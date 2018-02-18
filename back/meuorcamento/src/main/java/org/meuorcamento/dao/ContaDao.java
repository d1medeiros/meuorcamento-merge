package org.meuorcamento.dao;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meuorcamento.model.Carteira;
import org.meuorcamento.model.Conta;
import org.meuorcamento.model.TipoConta;
import org.meuorcamento.model.Usuario;
import org.meuorcamento.util.TokenGenerator;

@Stateful
public class ContaDao {

	@PersistenceContext
	private EntityManager em;
	
	private Usuario getUsuario(Usuario usuario) {
		Usuario find = em.find(Usuario.class, usuario.getId());
		return find;
	}
	private Carteira getCarteira(Usuario usuario) {
		Carteira carteira = getUsuario(usuario).getCarteira();
		return carteira;
	}
	private Conta geraContasParaDozeMeses(int plusMonth, Conta conta) {
		
		System.out.println("geraContasParaDozeMeses: " + plusMonth);
		
		Conta contaFutura = new Conta();
		contaFutura.setNome(conta.getNome());
		contaFutura.setValor(conta.getValor());
		contaFutura.setDataPagamento(conta.getDataPagamento().plusMonths(plusMonth));
		contaFutura.setEstado(conta.isEstado());
		contaFutura.setRepetir(conta.isRepetir());
		contaFutura.setTipoConta(conta.getTipoConta());
		contaFutura.setChaveGrupoContas(conta.getChaveGrupoContas()); 
		return contaFutura;
	}
	
	private Conta geraContaPelaAlteracao(Conta conta, Conta alteracao) {
		LocalDate withDayOfMonth = conta.getDataPagamento().withDayOfMonth(alteracao.getDataPagamento().getDayOfMonth());
		conta.setNome(alteracao.getNome());
		conta.setValor(alteracao.getValor());
		conta.setDataPagamento(withDayOfMonth);
		conta.setEstado(alteracao.isEstado());
		conta.setRepetir(alteracao.isRepetir());
		conta.setTipoConta(alteracao.getTipoConta());
		return conta;
	}
	
	public void inserir(Conta conta, Usuario usuario) {
		int anoVigente = conta.getDataPagamento().getYear();
		conta.setChaveGrupoContas(TokenGenerator.generateToken(conta.getNome())); 
		Carteira carteira = getCarteira(usuario);
		ArrayList<Conta> lista = new ArrayList<>();
		
		if(conta.isRepetir()) {
			for(int i=0;i<13;i++) {
				Conta geraContasParaDozeMeses = geraContasParaDozeMeses(i, conta);
				if(geraContasParaDozeMeses.getDataPagamento().getYear() > anoVigente) {
					break;
				}else {
					carteira.setConta(geraContasParaDozeMeses);
					em.persist(carteira);
				}
			}
		}else {
			carteira.setConta(conta);
			em.persist(carteira);
		}

	}
	
	public void alterar(Conta conta, Usuario usuario) {
		try {
			long count = usuario.getCarteira()
				.getContas()
				.stream()
				.filter(c -> c.getId() == conta.getId())
				.count();
			if(count == 1) {
				em.merge(conta);
			}
		}catch (Exception e) {		}
	}
	
	public void alterarAll(Conta conta, Usuario usuario) {
		List<Conta> mesesExistentes = usuario.getCarteira()
			   .getContas()
			   .stream()
			   .filter(c -> c.getChaveGrupoContas().equals(conta.getChaveGrupoContas()))
			   .collect(Collectors.toList());
			   
		mesesExistentes.forEach( c ->  em.merge(geraContaPelaAlteracao(c, conta)) );
	}
	
	public boolean remove(int id, Usuario usuario) {
		
		try {
			List<Conta> collect = usuario.getCarteira()
				.getContas()
				.stream()
				.filter(c -> c.getId() != id)
				.collect(Collectors.toList());
				
			usuario.getCarteira().setContas(collect);
			em.merge(usuario.getCarteira());
			return true;
		}catch (Exception e) {		
			return false;
		}
	}

	public boolean removeAll(int id, Usuario usuario) {
		try {
			Conta find = em.find(Conta.class, id);
			List<Conta> collect = usuario.getCarteira()
				.getContas()
				.stream()
				.filter(c -> !c.getChaveGrupoContas().equals(find.getChaveGrupoContas()))
				.collect(Collectors.toList());
				
			usuario.getCarteira().setContas(collect);
			em.merge(usuario.getCarteira());
			return true;
		}catch (Exception e) {		
			return false;
		}
	}
	
	public Conta getContaById(int id, Usuario usuario) {
		try {
			Conta conta = usuario.getCarteira()
			.getContas()
			.stream()
			.filter(c -> c.getId() == id )
			.findFirst()
			.get();
			
			return conta;
		}catch(Exception e) {
			e.printStackTrace();
			return new Conta();
		}
	}
	
	public List<Conta> getContaByIds(List<Integer> collect) {
		Query q = em.createQuery("select c from Conta c where c.id in :param1");
		q.setParameter("param1", collect);
		return q.getResultList();
	}
	
	public List<Conta> listaTodos(Usuario usuario) {
		LocalDate dataParaSeisMeses = LocalDate.now().plusMonths(12).with(TemporalAdjusters.lastDayOfMonth());
		Carteira carteira = getCarteira(usuario);
		return carteira.getContas().stream().filter(c -> c.getDataPagamento().isBefore(dataParaSeisMeses)).collect(Collectors.toList());
	}
	
	public List<Conta> listaMesAtual(Usuario usuario) {
		
		List<Conta> todos = listaTodos(usuario);
		return todos.stream()
					.filter(conta -> conta.getDataPagamento().getMonth() == LocalDate.now().getMonth())
					.collect(Collectors.toList());
		
		
	}
	
	public List<Conta> listaMesPorNumero(int mes, int ano, Usuario usuario) {
		List<Conta> todos = listaTodos(usuario);
		return todos.stream()
				.filter(conta -> conta.getDataPagamento().getMonth() == LocalDate.now().withMonth(mes).getMonth())
				.filter(conta -> conta.getDataPagamento().getYear() == LocalDate.now().withYear(ano).getYear())
				.collect(Collectors.toList());
	}

	
}

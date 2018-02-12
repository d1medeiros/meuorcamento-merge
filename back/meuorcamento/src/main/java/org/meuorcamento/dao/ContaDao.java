package org.meuorcamento.dao;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.meuorcamento.model.Conta;
import org.meuorcamento.model.TipoConta;
import org.meuorcamento.model.Usuario;
import org.meuorcamento.util.TokenGenerator;

@Stateful
public class ContaDao {

	@PersistenceContext
	private EntityManager em;
	
	
	private Conta geraContasParaDozeMeses(int plusMonth, Conta conta) {
		Conta contaFutura = new Conta();
		contaFutura.setNome(conta.getNome());
		contaFutura.setValor(conta.getValor());
		contaFutura.setDataPagamento(conta.getDataPagamento().plusMonths(plusMonth));
		contaFutura.setEstado(conta.isEstado());
		contaFutura.setRepetir(conta.isRepetir());
		contaFutura.setTipoConta(conta.getTipoConta());
		contaFutura.setChaveGrupoContas(conta.getChaveGrupoContas());
		contaFutura.setUsuario(conta.getUsuario());
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
		conta.setUsuario(alteracao.getUsuario());
		return conta;
	}
	
	public void inserir(Conta conta) {
		conta.setChaveGrupoContas(TokenGenerator.generateToken(conta.getNome()));
		int anoVigente = conta.getDataPagamento().getYear();
		
		if(conta.isRepetir()) {
			for(int i=0;i<13;i++) {
				Conta geraContasParaDozeMeses = geraContasParaDozeMeses(i, conta);
				if(geraContasParaDozeMeses.getDataPagamento().getYear() > anoVigente)
					break;
				else
					em.persist(geraContasParaDozeMeses);
			}
		}else {
			em.persist(conta);
		}

	}
	
	public void alterar(Conta conta) {
		em.merge(conta);
	}
	
	public void alterarAll(Conta conta) {
		List<Conta> mesesExistentes = mesesExistentes(conta);
		mesesExistentes.forEach( c ->  em.merge(geraContaPelaAlteracao(c, conta)) );
	}
	
	public void remove(int id) {
		Conta c = em.find(Conta.class, id);
		em.remove(c);
	}

	public void removeAll(int id) {
		List<Conta> mesesExistentes = mesesExistentes(em.find(Conta.class, id));
		mesesExistentes.forEach(conta -> em.remove(conta));
	}
	
	public Conta getContaById(int id) {
		Conta conta = new Conta();
		try {
			Query q = em.createQuery("select c from Conta c where c.id = :param1");
			q.setParameter("param1", id);
			conta = (Conta) q.getSingleResult();
			System.out.println("\n getContaById: " + conta.getId() + "\n");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return conta;
	}
	
	/**
	 * O limite e de 6 meses
	 * @return lista de contas
	 */
	public List<Conta> listaTodos(Usuario usuario) {
		List<Conta> contas = null;
		Query q = em.createQuery("select c from Conta c where c.dataPagamento < :param1 and c.usuario = :param2");
		q.setParameter("param1", dataParaSeisMeses());
		q.setParameter("param2", usuario);
		contas = q.getResultList();
		return contas;
	}
	
	/**
	 * O limite e de 6 meses
	 * @return lista de contas
	 */
	public List<Conta> listaPorTipoConta(TipoConta tipoConta) {
		List<Conta> contas = null;
		Query q = em.createQuery("select c from Conta c where c.dataPagamento < :param1 and c.tipoConta = :param2");
		q.setParameter("param1", dataParaSeisMeses());
		q.setParameter("param2", tipoConta);
		contas = q.getResultList();
		return contas;
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

	private LocalDate dataParaSeisMeses() {
		return LocalDate.now().plusMonths(12).with(TemporalAdjusters.lastDayOfMonth());
	}

	public List<Conta> mesesExistentes(Conta conta) {
		Conta c = em.find(Conta.class, conta.getId());
		Query q = em.createQuery("select c from Conta c where c.chaveGrupoContas = :param1 and c.dataPagamento >= :param2");
		q.setParameter("param1", c.getChaveGrupoContas());
		q.setParameter("param2", c.getDataPagamento());
		return q.getResultList();
	}

	public Conta getContaPorNomeEData(String nome, String data, Usuario usuario) {
		Query q = em.createQuery("select c from Conta c where c.dataPagamento = :param1 and c.usuario = :param2 and c.nome = :param3");
		q.setParameter("param1", dataParaSeisMeses());
		q.setParameter("param2", usuario);
		q.setParameter("param3", nome);
		Conta conta = (Conta) q.getSingleResult();
		return conta;
		
	}
	


	
}

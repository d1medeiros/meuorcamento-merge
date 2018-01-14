package org.meuorcamento.bean;

import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.meuorcamento.dao.ContaDao;
import org.meuorcamento.model.Conta;

@Model
public class ContaBean {
	
	@Inject
	private ContaDao dao;
	
	private Conta conta = new Conta();
	
	
	public Conta getConta() {
		return conta;
	}


	public void setConta(Conta conta) {
		this.conta = conta;
	}


	@Transactional
	public String salvar() {
		
		if(this.conta.isRepetir()) {
			for(int i=0;i<13;i++) {
				dao.inserir(geraContasParaDozeMeses(i));
			}
		}else {
			dao.inserir(conta);
		}
		
		System.out.println(this.conta.getNome() + " - " + this.conta.getValor() + " - " + this.conta.getDataPagamento() + " - " + this.conta.isEstado());
		this.conta = new Conta();
		return "/conta?faces-redirect=true";
	}
	
	private Conta geraContasParaDozeMeses(int plusMonth) {
		Conta contaFutura = new Conta();
		contaFutura.setNome(this.conta.getNome());
		contaFutura.setValor(this.conta.getValor());
		contaFutura.setDataPagamento(this.conta.getDataPagamento().plusMonths(plusMonth));
		contaFutura.setEstado(this.conta.isEstado());
		return contaFutura;
	}
	
	public List<Conta> getContas(){
		return dao.listaMesAtual();
	}
	

}

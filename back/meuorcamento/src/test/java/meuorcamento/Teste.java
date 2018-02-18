package meuorcamento;

import java.time.LocalDate;
import java.util.Optional;

import org.meuorcamento.model.Carteira;
import org.meuorcamento.model.Conta;
import org.meuorcamento.model.TipoConta;
import org.meuorcamento.model.Usuario;

public class Teste {

	public static void main(String[] args) {

		Usuario usuario = new Usuario();
		usuario.setNome("diego jose");
		usuario.setLogin("aian");
		usuario.setSenha("1234");
		
		Carteira carteira = new Carteira();
		usuario.setCarteira(carteira);
		
		
		Conta conta = new Conta();
		conta.setId(1);
		conta.setNome("vivo");
		conta.setDataPagamento(LocalDate.now());
		conta.setEstado(false);
		conta.setRepetir(false);
		conta.setTipoConta(TipoConta.GASTOS);
		conta.setValor(100.0);

		Conta conta2 = new Conta();
		conta2.setId(10);
		conta2.setNome("itau");
		conta2.setDataPagamento(LocalDate.now());
		conta2.setEstado(false);
		conta2.setRepetir(false);
		conta2.setTipoConta(TipoConta.GASTOS);
		conta2.setValor(2100.0);
		
		carteira.setConta(conta);
		carteira.setConta(conta2);
		
		

//		List<Conta> collect = usuario.getCarteira()
//			.getContas()
//			.stream()
//			.filter(c -> c.getId() == 10).collect(Collectors.toList());
//		
//		usuario.getCarteira().setContas(collect);
//			
//		usuario.getCarteira().getContas().stream().forEach(System.out::println);
		
		
//		System.out.println(count);
	}

}

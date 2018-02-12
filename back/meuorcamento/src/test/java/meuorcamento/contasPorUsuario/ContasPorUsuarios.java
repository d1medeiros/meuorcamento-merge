package meuorcamento.contasPorUsuario;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.meuorcamento.model.Conta;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@SuppressWarnings("deprecation")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContasPorUsuarios {

	private static String primeiraToken;
	private static int idConta;
	private static int idContaRepetir;
	private static String mesAnterior;
	private static String mesAtual;

	private void imp(String local, Object obj) {
		System.out.println("dmedeiros :: TESTE : " + local + " : " + obj + "\n");
	}

	private String getNomeById(int idConta) throws JsonParseException, JsonMappingException, IOException {
		// idConta = 2;
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		String response = target.path("/meuorcamento/api/conta/" + idConta).request().get(String.class);
		String nome = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			nome = (String) jsonObject.get("nome");
		} catch (Exception e) {}
		imp("******** getById: ", nome);
		return nome;
	}

	@Test
	public void aGeraUsuario() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("nome", "diego medeiros");
		jsonObject.putOpt("login", "diego");
		jsonObject.putOpt("senha", "12345");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/usuario/gerar").request().post(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		imp("aGeraUsuario: ", responseString);
		response.close();
		Assert.assertNotNull(responseString);
	}

	@Test
	public void bVerificaLoginRetornaToken() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("login", "diego");
		jsonObject.putOpt("senha", "12345");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/usuario/login").request().put(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		response.close();
		imp("verifica Login Retorna JSONToken : ", responseString);
		JSONObject jsonObjectResponse = new JSONObject(responseString);
		primeiraToken = (String) jsonObjectResponse.get("authtoken");
		imp("primeiro Token : ", primeiraToken);
		Assert.assertNotNull(primeiraToken);
	}

	@Test
	public void cGeraConta() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("nome", "cartao");
		jsonObject.putOpt("valor", 2110);
		jsonObject.putOpt("dataPagamento", "2018-02-20");
		jsonObject.putOpt("estado", false);
		jsonObject.putOpt("repetir", false);
		jsonObject.putOpt("tipoConta", "GASTOS");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		imp("cGeraConta: ", responseString);
		response.close();
		Assert.assertNotNull(responseString);
	}

	@Test
	public void dContasAtuais() {

		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		String response = target.path("/meuorcamento/api/conta/atual").request().header("XTOKEN", primeiraToken).get(String.class);
		imp("dContasAtuais: ", response);
		
		Assert.assertNotNull(response);
	}
	
	@Test
	public void eRemoveConta() throws JsonParseException, JsonMappingException, IOException {
		//so tem o 1 ate aqui
		idConta = 1;
//		idContaRepetir = (int) new JSONObject(jsonArray.opt(1).toString()).opt("id");
		
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/remove/" + idConta).request().header("XTOKEN", primeiraToken).post(null);
		String responseString = response.readEntity(String.class);
		imp("eRemoveConta: ", idConta);
		response.close();
		Assert.assertNull(getNomeById(idConta));
	}
	
	@Test
	public void fGeraContaRepetir() {
		int monthValue = LocalDate.now().minusMonths(1).getMonthValue();
		mesAnterior = monthValue < 10 ? "0" + monthValue : "" + monthValue;
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("nome", "seguro");
		jsonObject.putOpt("valor", 1110);
		jsonObject.putOpt("dataPagamento", "2018-" + mesAnterior + "-20");
		jsonObject.putOpt("estado", false);
		jsonObject.putOpt("repetir", true);
		jsonObject.putOpt("tipoConta", "GASTOS");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		imp("fGeraContaRepetir: ", responseString);
		response.close();
		Assert.assertNotNull(responseString);
	}


	@Test
	public void gRemoveAllConta() throws JsonParseException, JsonMappingException, IOException {
		//	ids 2 ao 13
		idContaRepetir = 2;
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/remove/todos/" + idContaRepetir).request().header("XTOKEN", primeiraToken).post(null);
		String responseString = response.readEntity(String.class);
		imp("gRemoveAllConta: ", idContaRepetir + " e todas pra frente");
		response.close();
		Assert.assertNull(getNomeById(idContaRepetir));
	}
	
	@Test
	public void hGeraContaRepetir() {
		int monthValue = LocalDate.now().minusMonths(1).getMonthValue();
		mesAnterior = monthValue < 10 ? "0" + monthValue : "" + monthValue;
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("nome", "seguro");
		jsonObject.putOpt("valor", 1110);
		jsonObject.putOpt("dataPagamento", "2018-" + mesAnterior + "-20");
		jsonObject.putOpt("estado", false);
		jsonObject.putOpt("repetir", true);
		jsonObject.putOpt("tipoConta", "GASTOS");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		imp("fGeraContaRepetir: ", responseString);
		response.close();
		Assert.assertNotNull(responseString);
	}

	
	@Test
	public void iContasMesAno() {
		LocalDate now = LocalDate.now();
		int monthValue = now.getMonthValue();
		int year = now.getYear();
		String mesAno = mesAnterior + "-" + year;
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		String response = target.path("/meuorcamento/api/conta/mesano/" + mesAno).request().header("XTOKEN", primeiraToken).get(String.class);
		imp("hContasMesAno: ", response);
		
		Assert.assertNotNull(response);
	}

	@Test
	public void jAlteraConta() throws JsonParseException, JsonMappingException, IOException {
		idConta = 14;
		Client newClient1 = ClientBuilder.newClient();
		WebTarget target1 = newClient1.target("http://127.0.0.1:8080");
		String response1 = target1.path("/meuorcamento/api/conta/" + idConta).request().get(String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JSR310Module());
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		Conta conta = objectMapper.readValue(response1, Conta.class);
		
		String nomeAterado = "itau";
		Optional.of(conta).ifPresent(c -> c.setNome(nomeAterado));
		
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JSR310Module());
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		String contaJson = objectMapper.writeValueAsString(conta);
		
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/altera").request().header("XTOKEN", primeiraToken).post(Entity.json(contaJson));
		String responseString = response.readEntity(String.class);
		response.close();
		
		String nomeNovo = getNomeById(idConta);
		imp("jAlteraConta: ", idConta + " - " + nomeAterado + " :: " + nomeNovo + "  " + nomeAterado.equalsIgnoreCase(nomeNovo));
		Assert.assertTrue(nomeAterado.equalsIgnoreCase(nomeNovo));
	}


//	@Test
//	public void iAlteraConta() throws JsonParseException, JsonMappingException, IOException {
//		String nome = "cartão de credito";
//		String data = "2018-02-20";
//		//iserindo contas
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.putOpt("nome", "cartão de credito");
//		jsonObject.putOpt("valor", 2110);
//		jsonObject.putOpt("dataPagamento", data);
//		jsonObject.putOpt("estado", false);
//		jsonObject.putOpt("repetir", true);
//		jsonObject.putOpt("tipoConta", "GASTOS");
//		Client newClient = ClientBuilder.newClient();
//		WebTarget target = newClient.target("http://127.0.0.1:8080");
//		Response response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
//		imp("iAlteraConta: ", response);
//		
//		//pegando id
//		idConta = 14;
//		
//		//alterando
//		String nomeAlterado = "carro";
//		jsonObject = new JSONObject();
//		jsonObject.putOpt("id", idConta);
//		jsonObject.putOpt("nome", nomeAlterado);
//		jsonObject.putOpt("valor", 2110);
//		jsonObject.putOpt("dataPagamento", data);
//		jsonObject.putOpt("estado", false);
//		jsonObject.putOpt("repetir", true);
//		jsonObject.putOpt("tipoConta", "GASTOS");
//		newClient = ClientBuilder.newClient();
//		target = newClient.target("http://127.0.0.1:8080");
//		response = target.path("/meuorcamento/api/conta/altera").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
//		
//		String nomeNovo = getNomeById(idConta);
//		
//		imp("iAlteraConta: ", idConta + " - " + nome + " :: " + nomeNovo + "  " + nomeAlterado.equalsIgnoreCase(nomeNovo));
//		Assert.assertTrue(nomeAlterado.equalsIgnoreCase(nomeNovo));
//	}

}
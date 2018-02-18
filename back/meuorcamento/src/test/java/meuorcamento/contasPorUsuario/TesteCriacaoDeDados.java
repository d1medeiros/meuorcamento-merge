package meuorcamento.contasPorUsuario;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

@SuppressWarnings("deprecation")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TesteCriacaoDeDados {

	private static String primeiraToken;
	private static int idConta;
	private static int idContaRepetir;
	private static String mesAnterior;
	private static String mesAtual;
	private static String collect;
	private static List<Conta> contas;
	private static int contasSize;
	private static long contagemMesAtual;
	

	
	
	private JSONObject jsonConta(String nome, int valor, String data, boolean estado, boolean repetir, String tipo) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("nome", nome);
		jsonObject.putOpt("valor", valor);
		jsonObject.putOpt("dataPagamento", data);
		jsonObject.putOpt("estado", estado);
		jsonObject.putOpt("repetir", repetir);
		jsonObject.putOpt("tipoConta", tipo);
		return jsonObject;
	}

	private void imp(String local, Object obj) {
		System.out.println("dmedeiros :: TESTE : " + local + " : " + obj + "\n");
	}

	private Conta changeNome(String nome, Conta conta) {
		conta.setNome(nome);
		return conta;
	}

	private String getNomeById(int idConta) throws JsonParseException, JsonMappingException, IOException {
		// idConta = 2;
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		String response = target.path("/meuorcamento/api/conta/" + idConta).request().header("XTOKEN", primeiraToken).get(String.class);
		String nome = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			nome = (String) jsonObject.get("nome");
		} catch (Exception e) {}
		imp("******** getById: ", nome);
		return nome;
	}
	
	private String getNomeByIds(String ids) throws JsonParseException, JsonMappingException, IOException {
		Client newClient2 = ClientBuilder.newClient();
		WebTarget target2 = newClient2.target("http://127.0.0.1:8080");
		String response1 = target2.path("/meuorcamento/api/conta/all").queryParam("ids", ids).request().get(String.class);
		
		return response1;
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
		
		int mesAtual = LocalDate.now().getMonthValue();
		String mesAtualString = (""+mesAtual).length() == 1 ? "0"+mesAtual : ""+mesAtual;
		int mesAnterior = LocalDate.now().minusMonths(1).getMonthValue();
		String mesAnteriorString = (""+mesAnterior).length() == 1 ? "0"+mesAnterior : ""+mesAnterior;
		int mesDepois = LocalDate.now().plusMonths(1).getMonthValue();
		String mesDepoisString = (""+mesDepois).length() == 1 ? "0"+mesDepois : ""+mesDepois;
		
		//// GASTOS 
		
		JSONObject jsonObject = jsonConta("cartao", 2110, "2018-" + mesAtualString + "-20", false, false, "GASTOS");
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		response.close();

		jsonObject = jsonConta("academia", 110, "2018-" + mesAtualString + "-22", false, false, "GASTOS");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		
		jsonObject = jsonConta("telefone", 210, "2018-" + mesAnteriorString + "-22", false, true, "GASTOS");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		
		jsonObject = jsonConta("internet", 80, "2018-" + mesDepoisString + "-22", false, false, "GASTOS");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		
		jsonObject = jsonConta("seguro", 480, "2018-" + mesDepoisString + "-22", false, true, "GASTOS");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		

		
		//// GANHO
		
		jsonObject = jsonConta("salario", 2000, "2018-" + mesAnteriorString + "-22", false, true, "GANHO");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		
		jsonObject = jsonConta("extra", 200, "2018-" + mesAtualString + "-22", false, false, "GANHO");
		target = newClient.target("http://127.0.0.1:8080");
		response = target.path("/meuorcamento/api/conta/salva").request().header("XTOKEN", primeiraToken).post(Entity.json(jsonObject.toString()));
		responseString = response.readEntity(String.class);
		response.close();
		
		
		
		
		imp("cGeraConta: ", responseString);
		Assert.assertNotNull(responseString);
	}



	
	

}

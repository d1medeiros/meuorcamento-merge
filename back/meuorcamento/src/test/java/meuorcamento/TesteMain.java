package meuorcamento;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TesteMain {
	
	private static JSONObject jsonObject;
	private static String primeiraToken;

	private void imp(String local,Object obj) {
		System.out.println("dmedeiros :: TESTE : " + local + " : " + obj);
	}
	
	@Before
	public void inicializa() {
		jsonObject = new JSONObject();
		jsonObject.putOpt("nome", "diego medeiros");
		jsonObject.putOpt("senha", "12345");
	}
	

	@Test
	public void aVerificaLoginRetornaToken() {
		imp("Meu objeto Usuario: ", jsonObject.toString());
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		Response response = target.path("/meuorcamento/api/usuario/login").request().put(Entity.json(jsonObject.toString()));
		String responseString = response.readEntity(String.class);
		response.close();
		imp("verifica Login Retorna JSONToken : ", responseString);
		
		JSONObject jsonObjectResponse = new JSONObject(responseString);
		primeiraToken = (String) jsonObjectResponse.get("authtoken");
		imp("primeiro Token : ", primeiraToken);
		
		Assert.assertTrue(primeiraToken != null && !primeiraToken.equals(""));
	}
	
	@Test
	public void bVerificaSeUsuarioConsegueFicarLogado(){
		
		Client newClient = ClientBuilder.newClient();
		WebTarget target = newClient.target("http://127.0.0.1:8080");
		String resTokenVerificar = target.path("/meuorcamento/api/usuario/verificar").queryParam("XTOKEN", primeiraToken).request().get(String.class);
		imp("verifica se esta logado Retorna JSONToken 2 : ", resTokenVerificar);
		
		JSONObject jsonObjectResponse = new JSONObject(resTokenVerificar);
		String segundoToken = (String) jsonObjectResponse.get("authtoken");
		imp("segundo Token : ", segundoToken);
		Assert.assertNotEquals(primeiraToken, segundoToken);
	}
	
//	@Test
//	public void cSelecionaConta() {
//		Client newClient = ClientBuilder.newClient();
//		WebTarget target = newClient.target("http://127.0.0.1:8080");
//		String response = target.path("/meuorcamento/api/conta/atual").request().get(String.class);
//		imp("selecionaConta", response);
//		Assert.assertTrue(response.contains("vivo"));
//	}

}

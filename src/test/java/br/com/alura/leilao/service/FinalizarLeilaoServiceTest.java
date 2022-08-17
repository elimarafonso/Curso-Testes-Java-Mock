package br.com.alura.leilao.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;

class FinalizarLeilaoServiceTest {

	@Autowired
	private FinalizarLeilaoService service;

	//falando pro Mockito que esta dependencia é um mock
	@Mock
	private LeilaoDao leilaoDao;
	
	@Mock
	private EnviadorDeEmails enviadorDeEmails;

	@BeforeEach
	public void BeforeEach() {
		MockitoAnnotations.initMocks(this);
		this.service = new FinalizarLeilaoService(leilaoDao,enviadorDeEmails);
	}

	@Test
	void deveriaFinalizarUmLeilao() {
		List<Leilao> leiloes = leiloes();
 
		/*when...thenReturn
		 * QUANDO o método "buscarLeiloesExpirados()" FOR CHAMADO...
		 * 
		 * entãoRetorne A LISTA "leiloes"
		 */
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
			.thenReturn(leiloes);

		service.finalizarLeiloesExpirados();

		Leilao leilao = leiloes.get(0);

		Assert.assertTrue(leilao.isFechado());
		Assert.assertEquals(new BigDecimal("3500"), leilao.getLanceVencedor().getValor());

		//Verifica que determinado comportamento aconteceu uma vez.
		Mockito.verify(leilaoDao).salvar(leilao);

	}

	@Test
	void deveriaEnviarEmailParaVencedorDoLeilao() {
		List<Leilao> leiloes = leiloes();
		
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
		.thenReturn(leiloes);
		
		service.finalizarLeiloesExpirados();
		
		Leilao leilao = leiloes.get(0);
		Lance lanceVencedor = leilao.getLanceVencedor();
		
		Mockito.verify(enviadorDeEmails)
		.enviarEmailVencedorLeilao(lanceVencedor);
	}
	
	@Test
	void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarOLeilao() {
		List<Leilao> leiloes = leiloes();
		
		Mockito.when(leilaoDao.buscarLeiloesExpirados())
		.thenReturn(leiloes);
		
		/*
		 * Quando o método "salvar" for chamado
		 * passando qualquer parametro, lança uma exception
		 */
		Mockito.when(leilaoDao.salvar(Mockito.any()))
		.thenThrow(RuntimeException.class);
		
		try {
			service.finalizarLeiloesExpirados();
			//verifica se o método NÃO OUVE INTERAÇÃO "NoInteractions"
			Mockito.verifyNoInteractions(enviadorDeEmails);
		} catch (Exception e) {}
	}
	
	
	private List<Leilao> leiloes() {
		List<Leilao> lista = new ArrayList<>();

		Leilao leilao = new Leilao("Monitor", new BigDecimal("3000"), new Usuario("Fulano"));

		Lance primeiroLance = new Lance(new Usuario("Elimar afonso"), new BigDecimal("3500"));
		Lance segundoLance = new Lance(new Usuario("Thayslane"), new BigDecimal("3000"));

		leilao.propoe(primeiroLance);
		leilao.propoe(segundoLance);

		lista.add(leilao);

		return lista;

	}

}

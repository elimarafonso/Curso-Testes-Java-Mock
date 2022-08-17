package br.com.alura.leilao.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;

class GeradorDePagamentoTest {

	private GeradorDePagamento gerador;

	@Mock
	private PagamentoDao pagamentos;

	/*
	 * O recurso de Argument Captor nos ajuda a capturar um objeto criado
	 * internamente na classe sendo testada, quando ele é passado como parâmetro
	 * para um método do mock
	 */
	@Captor
	private ArgumentCaptor<Pagamento> captor;

	@BeforeEach
	public void beforEach() {
		MockitoAnnotations.initMocks(this);
		this.gerador = new GeradorDePagamento(pagamentos);
	}

	@Test
	void deveriaCriaPagamentoParaVencedorDoLeilao() {

		Leilao leilao = leilao();
		Lance lanceVencedor = leilao.getLanceVencedor();

		gerador.gerarPagamento(lanceVencedor);

		// no momento que objeto é passado para o mock ele é capturado
		Mockito.verify(pagamentos).salvar(captor.capture());

		// objeto capturado pelo mock "captor.capture"
		Pagamento pagamento = captor.getValue();

		Assert.assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());

		Assert.assertEquals(lanceVencedor.getValor(), pagamento.getValor());

		Assert.assertFalse(pagamento.getPago());

		Assert.assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());

		Assert.assertEquals(leilao, pagamento.getLeilao());

	}

	//método auxiliar para simular um lance
	private Leilao leilao() {

		Leilao leilao = new Leilao("Monitor", new BigDecimal("3000"), new Usuario("Fulano"));

		Lance lanceVencedor = new Lance(new Usuario("Elimar afonso"), new BigDecimal("3500"));

		leilao.propoe(lanceVencedor);
		leilao.setLanceVencedor(lanceVencedor);

		return leilao;

	}
}

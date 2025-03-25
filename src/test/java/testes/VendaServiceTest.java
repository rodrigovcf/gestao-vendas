package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.rodrigo.gestaovendas.app.VendaService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.models.ItemVenda;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.models.Venda;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;
import com.rodrigo.gestaovendas.domain.repositories.VendaRepository;

public class VendaServiceTest {

    private VendaService vendaService;
    private VendaRepository vendaRepositoryMock;
    private ClienteRepository clienteRepositoryMock;
	private ProdutoRepository produtoRepositoryMock;
    
    @BeforeEach
    public void setup() {
        // Cria os mocks dos repositórios
        clienteRepositoryMock = Mockito.mock(ClienteRepository.class);
        vendaRepositoryMock = Mockito.mock(VendaRepository.class);
        produtoRepositoryMock = Mockito.mock(ProdutoRepository.class);

        // Cria a instância de VendaService com os mocks
        vendaService = new VendaService(vendaRepositoryMock, clienteRepositoryMock, produtoRepositoryMock);
    }


    @Test
    public void testRegistrarVenda() {
        // Simula os dados do cliente
        Cliente cliente = new Cliente(1, "Gabriel Araújo", 500.0, LocalDate.of(2025, 3, 30));
        when(clienteRepositoryMock.consultar(1)).thenReturn(cliente);

        // Simula os dados do produto
        Produto produto = new Produto(1, "Notebook", 200.0);
        when(produtoRepositoryMock.consultar(1)).thenReturn(produto);

        // Simula o mapa de produtos e suas quantidades
        Map<Integer, Integer> produtosQuantidade = Map.of(produto.getCodigo(), 2);

        // Simula a venda esperada que o método deve retornar
        Venda vendaEsperada = new Venda(
            1, // Código da venda
            cliente.getCodigo(),
            cliente,
            List.of(new ItemVenda(1, 1, produto, 2, produto.getPreco())),
            LocalDate.now(),
            400.0
        );
        when(vendaRepositoryMock.incluir(any(Venda.class))).thenReturn(vendaEsperada.getCodigo());

        // Executa o método registrarVenda
        Venda vendaObtida = vendaService.registrarVenda(cliente.getCodigo(), produtosQuantidade);

        // Valida os dados retornados
        assertNotNull(vendaObtida, "A venda não deve ser nula");
        assertEquals(vendaEsperada.getCodigo(), vendaObtida.getCodigo(), "O código da venda deve ser o esperado");
        assertEquals(vendaEsperada.getValorTotal(), vendaObtida.getValorTotal(), "O valor total deve ser o esperado");
    }






//    @Test
//    public void testConsultarVenda() {
//        // Simula os dados de uma venda existente
//        Cliente cliente = new Cliente(1, "Gabriel Araújo", 200.0, LocalDate.of(2025, 3, 30));
//        Produto produto = new Produto(1, "Notebook", 3000.0);
//        ItemVenda itemVenda = new ItemVenda(1, produto, 1, produto.getPreco());
//        Venda vendaEsperada = new Venda(1, cliente, List.of(itemVenda), LocalDate.now(), 3000.0);
//
//        // Ajusta o mock para retornar a venda esperada
//        when(vendaRepositoryMock.consultarPorId(1)).thenReturn(vendaEsperada);
//
//        // Executa o método de consulta
//        Venda vendaObtida = vendaService.consultarVenda(1);
//
//        // Valida os dados da venda
//        assertNotNull(vendaObtida, "A venda deve ser encontrada");
//        assertEquals(3000.0, vendaObtida.getValorTotal(), "O valor total da venda deve ser 3000.0");
//        assertEquals("Gabriel Araújo", vendaObtida.getCliente().getNome(), "O nome do cliente deve ser 'Gabriel Araújo'");
//
//        // Verifica se o repositório foi chamado corretamente
//        verify(vendaRepositoryMock, times(1)).consultarPorId(1);
//    }
}

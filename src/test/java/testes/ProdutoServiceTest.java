package testes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.rodrigo.gestaovendas.app.ProdutoService;
import com.rodrigo.gestaovendas.domain.models.Produto;
import com.rodrigo.gestaovendas.domain.repositories.ProdutoRepository;

public class ProdutoServiceTest {

    private ProdutoService produtoService;
    private ProdutoRepository produtoRepositoryMock;

    
    @BeforeEach
    public void setup() {
        produtoRepositoryMock = Mockito.mock(ProdutoRepository.class);
        produtoService = new ProdutoService(produtoRepositoryMock);
    }

    @Test
    public void testCadastrarProduto() {
        // Simula os dados do produto
        Produto produto = new Produto();
        produto.setDescricao("Sabonete");
        produto.setPreco(20.0);

        // Ajusta o mock para retornar true ao chamar incluir()
        Mockito.when(produtoRepositoryMock.incluir(Mockito.any(Produto.class))).thenReturn(produto);

        // Executa o método que deve ser testado
        produtoService.cadastrarProduto(produto.getDescricao(), produto.getPreco());

        // Verifica se o repositório foi chamado corretamente
        Mockito.verify(produtoRepositoryMock, Mockito.times(1))
               .incluir(Mockito.argThat(c -> 
                   c.getDescricao().equals("Sabonete") &&
                   c.getPreco() == 20.0
               ));
    }


    @Test
    public void testConsultarProduto() {
        Produto produtoEsperado = new Produto(1, "Sabonete", 20.0);

        Mockito.when(produtoRepositoryMock.consultar(1)).thenReturn(produtoEsperado);

        Produto produtoObtido = produtoService.buscarProdutoPorCodigo(1);
        assertNotNull(produtoObtido, "O produto deve ser encontrado");
        assertEquals("Sabonete", produtoObtido.getDescricao(), "O nome do produto deve ser 'Sabonete'");
    }

}

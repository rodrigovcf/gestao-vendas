package testes;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.rodrigo.gestaovendas.app.ClienteService;
import com.rodrigo.gestaovendas.domain.models.Cliente;
import com.rodrigo.gestaovendas.domain.repositories.ClienteRepository;

public class ClienteServiceTest {

    private ClienteService clienteService;
    private ClienteRepository clienteRepositoryMock;

    @BeforeEach
    public void setup() {
        clienteRepositoryMock = Mockito.mock(ClienteRepository.class);
        clienteService = new ClienteService(clienteRepositoryMock);
    }

    @Test
    public void testCadastrarCliente() {
        // Simula os dados do cliente
        Cliente cliente = new Cliente();
        cliente.setNome("Gabriel Araújo");
        cliente.setLimiteCompra(200.0);
        cliente.setDiaFechamentoFatura(null);

        // Ajusta o mock para retornar true ao chamar incluir()
        Mockito.when(clienteRepositoryMock.incluir(Mockito.any(Cliente.class))).thenReturn(cliente);

        // Executa o método que deve ser testado
        clienteService.cadastrarCliente(cliente.getNome(), cliente.getLimiteCompra(), null);

        // Verifica se o repositório foi chamado corretamente
        Mockito.verify(clienteRepositoryMock, Mockito.times(1))
               .incluir(Mockito.argThat(c -> 
                   c.getNome().equals("Gabriel Araújo") &&
                   c.getLimiteCompra() == 200.0
               ));
    }


    @Test
    public void testConsultarCliente() {
        Cliente clienteEsperado = new Cliente(1, "Gabriel Araújo", 200.0, null);

        Mockito.when(clienteRepositoryMock.consultar(1)).thenReturn(clienteEsperado);

        Cliente clienteObtido = clienteService.buscarClientePorId(1);
        assertNotNull(clienteObtido, "O cliente deve ser encontrado");
        assertEquals("Gabriel Araújo", clienteObtido.getNome(), "O nome do cliente deve ser 'Gabriel Araújo'");
    }

}

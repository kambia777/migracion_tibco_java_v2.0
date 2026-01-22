package com.example.demo.test;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.entity.Cliente;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.service.ClienteService;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ClienteServiceTest {
	
	@Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    // Inicializamos los mocks
    public ClienteServiceTest() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void obtenerClientePorCodigo_cuandoExiste_devuelveCliente() {
        // GIVEN
        String codigo = "1";
        Cliente cliente = new Cliente();
        cliente.setCodigo(codigo);
        cliente.setNombre("Cliente Test");

        when(clienteRepository.findByCodigo(codigo))
                .thenReturn(cliente);

        // WHEN
        Cliente resultado = clienteService.obtenerClientePorCodigo(codigo);

        // THEN
        assertNotNull(resultado);
        assertEquals(codigo, resultado.getCodigo());
        assertEquals("Cliente Test", resultado.getNombre());

        verify(clienteRepository, times(1)).findByCodigo(codigo);
    }
    
   
    void obtenerClientePorCodigo_cuandoNoExiste_lanzaExcepcion() {
        // GIVEN
        String codigo = "999";
        when(clienteRepository.findByCodigo(codigo)).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clienteService.obtenerClientePorCodigo(codigo);
        });

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(clienteRepository, times(1)).findByCodigo(codigo);
    }

}

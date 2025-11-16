/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mycompany.ejemplo_conversor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author arley-mantilla
 */
public class ConversorMonedaTest {
    
    private static ConversorMoneda conversor;
    
    @BeforeAll
    public static void recibeClase()
    {
        conversor = new ConversorMoneda();
    }
    
    @BeforeEach
    public void setup()
    {
        
    }
    
    //se ejecuta antes 
    
    public ConversorMonedaTest() {
    }

    @Test
    public void dolarEuro()
    {
        double dolar = 10.0;
        ConversorMoneda conversor = new ConversorMoneda();
        double resultado_esperado = 12.0;
        double resultado_obtenido = conversor.dolaresEuros(dolar);
        assertEquals(resultado_esperado, resultado_obtenido, 0.001, "La conversion de dolares a euros no es correcta");
    }
    
    @Test
    public void euroDolar()
    {
        double euro = 12.0;
        ConversorMoneda conversor = new ConversorMoneda();
        double resultado_esperado = 10.0;
        double resultado_obtenido = conversor.eurosDolares(euro);
        assertEquals(resultado_esperado, resultado_obtenido, 0.001, "La conversion de euros a dolares no es correcta");
    }
    
    
    
    
}

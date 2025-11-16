/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ejemplo_conversor;

/**
 *
 * @author arley-mantilla
 */
public class ConversorMoneda 
{
    private static final double TASA_CONVERSION = 1.2;
    
    public double dolaresEuros(double dolar)
    {
        return (dolar * TASA_CONVERSION);
    }
    public double eurosDolares(double euro)
    {
        return (euro / TASA_CONVERSION);
    }
}

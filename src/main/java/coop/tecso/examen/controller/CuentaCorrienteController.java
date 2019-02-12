package coop.tecso.examen.controller;

import coop.tecso.examen.model.CuentaCorriente;
import coop.tecso.examen.model.PersonaFisica;
import coop.tecso.examen.service.CuentaCorrienteService;
import coop.tecso.examen.util.CuentaCorrienteConMovimientosException;
import coop.tecso.examen.util.DescubiertoException;
import coop.tecso.examen.util.RestResponse;
import coop.tecso.examen.util.TipoMoneda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CuentaCorrienteController {

    @Autowired
    protected CuentaCorrienteService cuentaCorrienteService;
    
    protected ObjectMapper mapper;

    @RequestMapping("/cuentacorriente")
    public List<CuentaCorriente> getAll() {
        return cuentaCorrienteService.findAll();
    }

    @RequestMapping("/cuentacorriente/{id}")
    public CuentaCorriente getById(@PathVariable Long id) {
        return cuentaCorrienteService.findById(id);
    }

 // Ingresar nuevo o actualizar existente
    @RequestMapping(value = "/cuentacorriente", method = RequestMethod.POST)
    public RestResponse saveOrUpdate(@RequestBody CuentaCorriente cuentaCorriente) {
        Long id = cuentaCorriente.getId();

        if(!validate(cuentaCorriente)){
            return new RestResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                    "Faltan campos por diligenciar");
        }

        if(Objects.nonNull(id) && id > 0) {
            CuentaCorriente actual = cuentaCorrienteService.findById(cuentaCorriente.getId());
            if(Objects.nonNull(actual)) {
                cuentaCorriente.setSaldo(actual.getSaldo());
            }
        }

        try {
            cuentaCorrienteService.save(cuentaCorriente);
        }catch(DescubiertoException ex){
            return new RestResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                    "El saldo no puede ser mayor al descubierto");
        }

        return new RestResponse(HttpStatus.OK.value(), "Operación exitosa");
    }

    // Borrar existente
    @RequestMapping(value = "/cuentacorriente/{id}", method = RequestMethod.DELETE)
    public RestResponse delete(@PathVariable Long id) {
        try {
            cuentaCorrienteService.delete(id);
        }catch(CuentaCorrienteConMovimientosException ex){
            return new RestResponse(HttpStatus.NOT_ACCEPTABLE.value(),
                    "La cuenta no puede ser borrada debido a que posee movimientos asociados");
        }

        return new RestResponse(HttpStatus.OK.value(), "Operación exitosa");
    }

    private boolean validate(CuentaCorriente cuentaCorriente) {
        if (Objects.isNull(cuentaCorriente.getNumero()) ||
                Objects.isNull(cuentaCorriente.getMoneda())
        ) {
            return false;
        }

        return true;
    }
}

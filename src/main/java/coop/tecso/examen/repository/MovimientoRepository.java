package coop.tecso.examen.repository;

import coop.tecso.examen.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    public List<Movimiento> findByCuentaCorrienteNumeroOrderByFechaDesc(Long numero);

    public List<Movimiento> findByCuentaCorrienteId(Long id);
}

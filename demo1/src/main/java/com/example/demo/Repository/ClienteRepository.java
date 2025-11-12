package com.example.demo.Repository;
import com.example.demo.entity.Cliente; // importa tu entidad
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}

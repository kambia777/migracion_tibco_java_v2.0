package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.DetallePedido;


@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

}

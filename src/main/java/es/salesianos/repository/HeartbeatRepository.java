package es.salesianos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.salesianos.model.HeartBeat;

@Repository("heartbeatRepository")
public interface HeartbeatRepository extends JpaRepository<HeartBeat, Integer> {

}

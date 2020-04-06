package es.salesianos.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.salesianos.model.HeartBeat;

@Repository("heartbeatRepository")
public interface HeartbeatRepository extends JpaRepository<HeartBeat, Integer> {

	List<HeartBeat> findAllByTokenidAndEventDateBetween(String tokenid, LocalDateTime from, LocalDateTime to);

}

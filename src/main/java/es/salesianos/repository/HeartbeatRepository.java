package es.salesianos.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.salesianos.model.HeartBeat;

@Repository("heartbeatRepository")
public interface HeartbeatRepository extends JpaRepository<HeartBeat, Integer> {

	Optional<List<HeartBeat>> findAllByTokenidAndEventDateBetweenOrderByEventDate(String tokenid, LocalDateTime from, LocalDateTime to);

}

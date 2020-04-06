package es.salesianos.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.salesianos.model.ChartSlice;
import es.salesianos.model.HeartBeat;
import es.salesianos.repository.HeartbeatRepository;
import lombok.extern.log4j.Log4j2;

@RestController
@CrossOrigin(origins = { "http://localhost", "http://localhost:3000", "null" })
@RequestMapping(value = "/api")
@Log4j2
public class WakaTimeRestController {

	@Autowired
	HeartbeatRepository repository;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "/heartbeats")
	public ResponseEntity heartbeats(@RequestHeader HttpHeaders headers, @RequestBody String heartbeatJson) {
		String token = getTokenIdFrom(headers);
		ObjectMapper mapper = configureObjectMapper();
		try {
			List<HeartBeat> heartBeats = mapper.readValue(heartbeatJson, newListTypeTypeInference());
			for (HeartBeat heartBeat : heartBeats) {
				addLocalDateFromTimestamp(heartBeat);
				heartBeat.setTokenid(token);
			}
			repository.saveAll(heartBeats);
		} catch (JsonProcessingException e) {
			log.error(e);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/Chart")
	public ResponseEntity<List<ChartSlice>> query(
			@RequestParam String tokenId,
			@RequestParam String topic,
			@RequestParam(required = false) String from, 
			@RequestParam(required = false) String to) {
		List<ChartSlice> results = new ArrayList<ChartSlice>();
		ChartSlice uno = new ChartSlice(20, "Airfare");
		ChartSlice dos = new ChartSlice(24, "Food & Drinks");
		ChartSlice tres = new ChartSlice(20, "Accomodation");
		ChartSlice cuatro = new ChartSlice(14, "Transportation");
		ChartSlice cinco = new ChartSlice(12, "Activities");
		ChartSlice seis = new ChartSlice(10, "Misc");
		results.add(uno);
		results.add(dos);
		results.add(tres);
		results.add(cuatro);
		results.add(cinco);
		results.add(seis);
		LocalDateTime dateFrom = StringUtils.isEmpty(from) ? LocalDateTime.now().minusWeeks(2)
				: LocalDateTime.parse(from);
		LocalDateTime dateTo = StringUtils.isEmpty(to) ? LocalDateTime.now() : LocalDateTime.parse(to);
		List<HeartBeat> heartbeats = new ArrayList<HeartBeat>();
		if(StringUtils.isEmpty(topic)) {
			switch (topic) {
			case "branch":
				heartbeats = repository.findByBranchAndTokenidAndEventDateBetweenFromAndTo(tokenId, topic, dateFrom,
						dateTo);
				break;
			case "project":
				heartbeats = repository.findByBranchAndTokenidAndEventDateBetweenFromAndTo(tokenId, topic, dateFrom,
						dateTo);
				break;
			case "language":
				heartbeats = repository.findByLanguageAndTokenidAndEventDateBetweenFromAndTo(tokenId, topic,
						dateFrom,
						dateTo);
				break;
			case "filename":
				heartbeats = repository.findByEntityLikeTokenidAndAndEventDateBetweenFromAndTo(tokenId, topic,
						dateFrom,
						dateTo);
				break;
			default:
				break;
			}
		} else {
			heartbeats = repository.findByTokenidAndEventDateBetweenFromAndTo(tokenId, dateFrom, dateTo);
		}
		// results = transformHeartBeatsToChartSlices(heartbeats);
		return new ResponseEntity<List<ChartSlice>>(results, HttpStatus.OK);
	}

	private List<ChartSlice> transformHeartBeatsToChartSlices(List<HeartBeat> heartbeats) {
		List<ChartSlice> slices = new ArrayList<ChartSlice>();
		heartbeats.forEach((heartbeat) -> {
			// TODO
		});
		// TODO Auto-generated method stub
		return null;
	}

	private String getTokenIdFrom(HttpHeaders headers) {
		String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
		String token = authHeader.split(" ")[1];
		return token;
	}

	private ObjectMapper configureObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// FIXME la propiedad is_write no se mapea y es ignorada
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}

	private void addLocalDateFromTimestamp(HeartBeat heartBeat) {
		Instant instant = Instant.ofEpochSecond((long) Double.parseDouble(heartBeat.getTime()));
		heartBeat.setEventDate(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
	}

	private TypeReference<List<HeartBeat>> newListTypeTypeInference() {
		return new TypeReference<List<HeartBeat>>() {
		};
	}
}

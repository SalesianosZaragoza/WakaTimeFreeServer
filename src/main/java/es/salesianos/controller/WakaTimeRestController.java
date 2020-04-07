package es.salesianos.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.salesianos.model.ChartSlice;
import es.salesianos.model.HeartBeat;
import es.salesianos.reducer.HeartBeatReducer;
import es.salesianos.repository.HeartbeatRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@CrossOrigin(origins = { "http://localhost", "http://localhost:3000", "http://127.0.0.1", "http://127.0.0.1:3000" })
@RequestMapping(value = "/api")
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class WakaTimeRestController {

	public WakaTimeRestController() {
		mapper = configureObjectMapper();
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@RequestMapping(value = "/heartbeats")
	public ResponseEntity heartbeats(@RequestHeader HttpHeaders headers, @RequestBody String heartbeatJson) {
		token = getTokenIdFrom(headers);
		try {
			List<HeartBeat> heartBeats = parseJson(heartbeatJson);
			for (HeartBeat heartBeat : heartBeats) {
				preprocess(heartBeat);
			}
			Predicate<HeartBeat> p = new Predicate<HeartBeat>() {

				@Override
				public boolean test(HeartBeat heartbeat) {
					return (heartbeat.getEventDate().plusMinutes(60).compareTo(LocalDateTime.now()) > 0);
				}
			};
			List<HeartBeat> collect = heartBeats.stream().filter(p).collect(Collectors.toList());
			repository.saveAll(collect);
		} catch (JsonProcessingException e) {
			log.error(e);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/Chart")
	public ResponseEntity<ChartSlice> query(@RequestParam String tokenId, @RequestParam String topic,
			@RequestParam(required = false) String from, @RequestParam(required = false) String to) {

		ChartSlice result = new ChartSlice();

		LocalDate dateFrom = parseDateFromOrDefault(from);
		LocalDate dateTo = parseDateToOrDefault(to).plusDays(1);

		Optional<List<HeartBeat>> heartbeats = repository.findAllByTokenidAndEventDateBetweenOrderByEventDate(tokenId,
				dateFrom.atStartOfDay(), dateTo.atStartOfDay());
		result = transformHeartBeatsToChartSlices(topic, heartbeats);

		log.debug(topic + " chart info: " + result);
		return new ResponseEntity<ChartSlice>(result, HttpStatus.OK);
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/All")
	public ResponseEntity<ChartSlice> all(@RequestParam String tokenId, @RequestParam(required = false) String from,
			@RequestParam(required = false) String to) {
		ChartSlice result = new ChartSlice();

		LocalDate dateFrom = parseDateFromOrDefault(from);
		LocalDate dateTo = parseDateToOrDefault(to).plusDays(1);

		Optional<List<HeartBeat>> heartbeats = repository.findAllByTokenidAndEventDateBetweenOrderByEventDate(tokenId,
				dateFrom.atStartOfDay(), dateTo.atStartOfDay());

		Optional<HeartBeat> accumulated = heartbeats.orElseThrow().stream().reduce(new HeartBeatReducer());
		long diff = accumulated.orElseThrow().getDuration().toHoursPart();

		log.debug("ALL hours worked between: " + diff);
		result.getValue().add(diff);
		result.getLabel().add("hours worked between:" + dateFrom + " and " + dateTo);
		return new ResponseEntity<ChartSlice>(result, HttpStatus.OK);
	}

	private void preprocess(HeartBeat heartBeat) {
		heartBeat.setEventDate(addLocalDateFromTimestamp(heartBeat.getTime()));
		heartBeat.setTokenid(new String(Base64.getDecoder().decode(token)));
		heartBeat.setEntity(extractFileName(heartBeat.getEntity()));
	}

	private List<HeartBeat> parseJson(String heartbeatJson) throws JsonProcessingException, JsonMappingException {
		return mapper.readValue(heartbeatJson, buildListTypeTypeInference());
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

	private LocalDateTime addLocalDateFromTimestamp(Long time) {
		Instant instant = Instant.ofEpochSecond(time);
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	private String extractFileName(String filename) {
		return FilenameUtils.getName(filename);
	}

	private LocalDate parseDateToOrDefault(String to) {
		return StringUtils.isEmpty(to) ? LocalDate.now() : parseDate(to);
	}

	private LocalDate parseDateFromOrDefault(String from) {
		return StringUtils.isEmpty(from) ? LocalDate.now().minusWeeks(2) : parseDate(from);
	}

	private LocalDate parseDate(String to) {
		return LocalDate.parse(to, formatter);
	}

	private ChartSlice transformHeartBeatsToChartSlices(String topic, Optional<List<HeartBeat>> heartbeats) {
		ChartSlice slice = new ChartSlice();
		Map<String, List<HeartBeat>> hashMap = new HashMap<String, List<HeartBeat>>();
		hashMap = groupHeartBeatsByTopic(topic, heartbeats);
		Set<String> keySet = hashMap.keySet();
		for (String key : keySet) {
			Optional<HeartBeat> accumulated = timeAccumulated(hashMap, key);
			Duration duration = accumulated.orElseThrow().getDuration();
			if (duration != null) {
				Long milli = duration.toMillis();
				slice.getLabel().add(key);
				slice.getValue().add(milli);
			}
		}
		normalize(slice);
		return slice;
	}

	private void normalize(ChartSlice slice) {
		Long total = Long.valueOf(0);

		log.debug("data before normalization:" + slice.getValue());
		for (Long value : slice.getValue()) {
			total += value;
		}

		log.debug("total ammount: " + total);
		if (total == 0)
			return;

		List<Long> data = slice.getValue();
		for (int i = 0; i < data.size(); i++) {
			Long value = (data.get(i) * 100) / total;
			data.set(i, value);
		}

		log.debug("data after normalization:" + data);
		slice.setValue(data);
	}

	private Optional<HeartBeat> timeAccumulated(Map<String, List<HeartBeat>> hashMap, String key) {
		List<HeartBeat> heartBeats = hashMap.get(key);
		Optional<HeartBeat> reduced = heartBeats.stream().reduce(new HeartBeatReducer());
		HeartBeat beat = reduced.get();
		return reduced;
	}

	private Map<String, List<HeartBeat>> groupHeartBeatsByTopic(String topic, Optional<List<HeartBeat>> heartbeats) {
		Map<String, List<HeartBeat>> hashMap = new HashMap<String, List<HeartBeat>>();
		switch (topic) {
		case "branch":
			hashMap = heartbeats.orElseThrow().stream()
					.collect(Collectors.groupingBy(w -> w.getBranch() == null ? "UnknownBranch" : w.getBranch()));
			break;
		case "project":
			hashMap = heartbeats.orElseThrow()
					.stream()
					.collect(Collectors.groupingBy(w -> w.getProject() == null ? "UnknownProject" : w.getProject()));
			break;
		case "language":
			hashMap = heartbeats.orElseThrow()
					.stream()
					.collect(Collectors.groupingBy(w -> w.getLanguage() == null ? "UnknownLanguage" : w.getLanguage()));
			break;
		case "filename":
			hashMap = heartbeats.orElseThrow()
					.stream()
					.collect(Collectors.groupingBy(w -> w.getEntity() == null ? "UnknownFile" : w.getEntity()));
			break;
		default:

			break;
		}
		return hashMap;
	}

	private TypeReference<List<HeartBeat>> buildListTypeTypeInference() {
		return new TypeReference<List<HeartBeat>>() {
		};
	}

	ObjectMapper mapper;
	String token;
	@Autowired
	HeartbeatRepository repository;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}

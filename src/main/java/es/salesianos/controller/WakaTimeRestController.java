package es.salesianos.controller;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
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
@CrossOrigin(origins = { "http://localhost", "http://localhost:3000", "http://127.0.0.1", "http://127.0.0.1:3000",
		"null" })
@RequestMapping(value = "/api")
@Log4j2
public class WakaTimeRestController {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
				heartBeat.setTokenid(new String(Base64.getDecoder().decode(token)));
				extractFileName(heartBeat);
			}
			repository.saveAll(heartBeats);
		} catch (JsonProcessingException e) {
			log.error(e);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	private void extractFileName(HeartBeat heartBeat) {
			heartBeat.setEntity(FilenameUtils.getName(heartBeat.getEntity()));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@RequestMapping(value = "/Chart")
	public ResponseEntity<List<ChartSlice>> query(
			@RequestParam String tokenId,
			@RequestParam String topic,
			@RequestParam(required = false) String from, 
			@RequestParam(required = false) String to) {
		List<ChartSlice> results = new ArrayList<ChartSlice>();
		LocalDate dateFrom = StringUtils.isEmpty(from) ? LocalDate.now().minusWeeks(2)
				: LocalDate.parse(from, formatter);
		LocalDate dateTo = StringUtils.isEmpty(to) ? LocalDate.now() : LocalDate.parse(to, formatter);
		List<HeartBeat> heartbeats = new ArrayList<HeartBeat>();
		heartbeats = repository.findAllByTokenidAndEventDateBetween(tokenId, dateFrom.atStartOfDay(),
				dateTo.atStartOfDay());
		results = transformHeartBeatsToChartSlices(topic, heartbeats);
		System.out.println(results);
		return new ResponseEntity<List<ChartSlice>>(results, HttpStatus.OK);
	}

	private List<ChartSlice> transformHeartBeatsToChartSlices(String topic, List<HeartBeat> heartbeats) {
		List<ChartSlice> slices = new ArrayList<ChartSlice>();
		Map<String, List<HeartBeat>> hashMap = new HashMap<String, List<HeartBeat>>();
		hashMap = groupHeartBeats(topic, heartbeats);
		Set<String> keySet = hashMap.keySet();
		for (String key : keySet) {
			ChartSlice slice = new ChartSlice(0, key);
			Optional<HeartBeat> accumulated = timeAccumulated(hashMap, key);
			if (accumulated.isPresent()) {
				slice.setValue(
						accumulated.get().getEventDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
				slices.add(slice);
			}
		}
		normalize(slices);
		return slices;
	}

	private void normalize(List<ChartSlice> slices) {
		// TODO Auto-generated method stub
		long total = 0;
		for (ChartSlice chartSlice : slices) {
			total += chartSlice.getValue();
		}
		for (ChartSlice chartSlice : slices) {
			chartSlice.setValue((chartSlice.getValue() * 100) / total);
		}
	}

	private Optional<HeartBeat> timeAccumulated(Map<String, List<HeartBeat>> hashMap, String key) {
		List<HeartBeat> heartBeats = hashMap.get(key);
		BinaryOperator<HeartBeat> operator = new BinaryOperator<HeartBeat>() {
			HeartBeat accum = new HeartBeat();
			{
				accum.setEventDate(LocalDateTime.now());
			}
			@Override
			public HeartBeat apply(HeartBeat before, HeartBeat now) {
				if (null == before || null == now)
					return accum;
				LocalDateTime beforeDate = before.getEventDate();
				LocalDateTime nowDate = now.getEventDate();
				if (null == beforeDate || null == nowDate)
					return accum;
				if (nowDate.compareTo(beforeDate.plusMinutes(10)) == 1) {
					Duration duration = Duration.between(now.getEventDate(), before.getEventDate());
					accum.setEventDate(accum.getEventDate().plus(duration));
				}
				return accum;
			}
		};

		return heartBeats.stream().reduce(operator);
	}

	private Map<String, List<HeartBeat>> groupHeartBeats(String topic, List<HeartBeat> heartbeats) {
		Map<String, List<HeartBeat>> hashMap = new HashMap<String, List<HeartBeat>>();
		switch (topic) {
		case "branch":
			hashMap = heartbeats.stream()
					.collect(Collectors
							.groupingBy(w -> w.getBranch() == null ? "absentBranch" : w.getBranch()));
			break;
		case "project":
			hashMap = heartbeats.stream()
					.collect(Collectors.groupingBy(w -> w.getProject() == null ? "absentProject" : w.getProject()));
			break;
		case "language":
			hashMap = heartbeats.stream()
					.collect(Collectors.groupingBy(w -> w.getLanguage() == null ? "absentLanguage" : w.getLanguage()));
			break;
		case "filename":
			hashMap = heartbeats.stream()
					.collect(Collectors.groupingBy(w -> w.getEntity() == null ? "absentFile" : w.getEntity()));
			break;
		default:
			break;
		}
		return hashMap;
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

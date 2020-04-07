package es.salesianos.reducer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.BinaryOperator;

import es.salesianos.model.HeartBeat;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class HeartBeatReducer implements BinaryOperator<HeartBeat> {

	HeartBeat accum = new HeartBeat();

	public HeartBeatReducer() {
		accum.setDuration(Duration.ZERO);
	}

	@Override
	public HeartBeat apply(HeartBeat before, HeartBeat now) {
		if (null == before || null == now)
			return accum;
		LocalDateTime beforeDate = before.getEventDate();
		LocalDateTime nowDate = now.getEventDate();
		if (null == beforeDate || null == nowDate)
			return accum;
		log.debug("Must satisfy:" + beforeDate + " before to " + nowDate);
		if (nowDate.compareTo(beforeDate.plusMinutes(10)) < 0) {
			log.debug("reducing:" + beforeDate + " and " + nowDate);
			Duration duration = Duration.between(now.getEventDate(), before.getEventDate()).abs();
			accum.setDuration(accum.getDuration().plus(duration));
		}
		return accum;
	}
}

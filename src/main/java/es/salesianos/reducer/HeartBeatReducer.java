package es.salesianos.reducer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.BinaryOperator;

import es.salesianos.model.HeartBeat;

public class HeartBeatReducer implements BinaryOperator<HeartBeat> {

	HeartBeat accum = new HeartBeat();

	public HeartBeatReducer() {
		accum.setEventDate(LocalDateTime.now());
	}

	public HeartBeatReducer(LocalDateTime localdatetime) {
		accum.setEventDate(localdatetime);
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
}

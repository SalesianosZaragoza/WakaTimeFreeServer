package es.salesianos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartSlice {

	private long value;
	private String label;

}

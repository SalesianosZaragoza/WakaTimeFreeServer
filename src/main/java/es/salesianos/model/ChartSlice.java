package es.salesianos.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartSlice {

	private List<Long> value = new ArrayList<Long>();
	private List<String> label = new ArrayList<String>();

}

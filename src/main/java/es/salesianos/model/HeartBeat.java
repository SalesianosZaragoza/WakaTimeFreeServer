package es.salesianos.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "HEARTBEAT")
@Data
public class HeartBeat {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;

	private Long time;
	// ": 1585685196.8117783,
	private String entity;
	private String type;
	private String category;
	private boolean is_write;
	private String project;
	private String branch;
	private String language;
	@ElementCollection
	private List<String> dependencies;
	private String lines;
	private String lineno;
	private String cursorpos;
	private String user_agent;
	private String tokenid;
	private LocalDateTime eventDate;

}

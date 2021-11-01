package com.example.demo.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NurseAddress implements Serializable {
	private String address;

	private String cityName;

	private String stateName;

}
